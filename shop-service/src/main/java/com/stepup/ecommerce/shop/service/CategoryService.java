package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.category.CategoryRequest;
import com.stepup.ecommerce.shop.category.CategoryResponse;
import com.stepup.ecommerce.shop.entity.Category;
import com.stepup.ecommerce.shop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        category = categoryRepository.save(category);

        return toResponse(category);
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toResponse(category);
    }

    public CategoryResponse update(Long categoryId, CategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());

        category = categoryRepository.save(category);

        return toResponse(category);
    }

    public void delete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(categoryId);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getDescription()
        );
    }
}