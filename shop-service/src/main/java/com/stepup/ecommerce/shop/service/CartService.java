package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.cart.AddCartItemRequest;
import com.stepup.ecommerce.shop.cart.CartItemResponse;
import com.stepup.ecommerce.shop.cart.CartResponse;
import com.stepup.ecommerce.shop.cart.UpdateCartItemRequest;
import com.stepup.ecommerce.shop.entity.Cart;
import com.stepup.ecommerce.shop.entity.CartItem;
import com.stepup.ecommerce.shop.entity.CartStatus;
import com.stepup.ecommerce.shop.entity.Product;
import com.stepup.ecommerce.shop.repository.CartItemRepository;
import com.stepup.ecommerce.shop.repository.CartRepository;
import com.stepup.ecommerce.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateActiveCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Cart cart = getOrCreateActiveCart(userId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + request.quantity());
        cartItemRepository.save(item);

        return getCart(userId);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);

        return getCart(userId);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {

        Cart cart = getOrCreateActiveCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("Cart item does not belong to this user");
        }

        cartItemRepository.delete(item);

        return getCart(userId);
    }

    public CartResponse getCart(Long userId) {

        Cart cart = getOrCreateActiveCart(userId);

        List<CartItem> items = cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses = items.stream()
                .map(item -> new CartItemResponse(
                        item.getCartItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getCartId(), itemResponses, total);
    }
}