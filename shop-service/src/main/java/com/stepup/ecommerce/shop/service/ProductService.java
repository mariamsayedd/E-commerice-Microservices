package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.client.InventoryClient;
import com.stepup.ecommerce.shop.client.InventoryProductDto;
import com.stepup.ecommerce.shop.entity.Category;
import com.stepup.ecommerce.shop.entity.Product;
import com.stepup.ecommerce.shop.product.ProductRequest;
import com.stepup.ecommerce.shop.product.ProductResponse;
import com.stepup.ecommerce.shop.repository.CategoryRepository;
import com.stepup.ecommerce.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryGatewayService inventoryGatewayService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryClient inventoryClient,
            InventoryGatewayService inventoryGatewayService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryGatewayService = inventoryGatewayService;
    }

    public ProductResponse create(ProductRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setImageUrl(request.imageUrl());

        product = productRepository.save(product);

        String sku = "SKU-" + product.getProductId() + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        inventoryGatewayService.createProduct(
                new InventoryProductDto(product.getProductId(), product.getName(), sku)
        );

        return toResponse(product);
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    public ProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(product);
    }

    public ProductResponse update(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setImageUrl(request.imageUrl());

        product = productRepository.save(product);
        return toResponse(product);
    }

    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getCategoryId(),
                product.getCategory().getName(),
                product.getImageUrl()
        );
    }
}