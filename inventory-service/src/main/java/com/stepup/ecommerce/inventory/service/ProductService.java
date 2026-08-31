package com.stepup.ecommerce.inventory.service;

import com.stepup.ecommerce.inventory.entity.Product;
import com.stepup.ecommerce.inventory.product.ProductRequest;
import com.stepup.ecommerce.inventory.product.ProductResponse;
import com.stepup.ecommerce.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {

        if (productRepository.existsById(request.productId())) {
            throw new RuntimeException("Product already exists in inventory");
        }

        if (productRepository.existsBySku(request.sku())) {
            throw new RuntimeException("SKU already exists");
        }

        Product product = new Product();
        product.setProductId(request.productId());
        product.setName(request.name());
        product.setSku(request.sku());

        product = productRepository.save(product);

        return toResponse(product);
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(product);
    }

    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getProductId(), product.getName(), product.getSku());
    }
}