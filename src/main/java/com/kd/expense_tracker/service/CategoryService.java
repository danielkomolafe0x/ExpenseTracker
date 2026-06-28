package com.kd.expense_tracker.service;

import com.kd.expense_tracker.exception.ResourceNotFoundException;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.TransactionType;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.repository.CategoryRepository;
import com.kd.expense_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Category createCategory(Long userId, String name, TransactionType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = new Category(name, type, user);
        return categoryRepository.save(category);
    }

    public List<Category> getCategoriesForUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Category getCategoryForUser(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        return category;
    }

    public Category updateCategory(Long userId, Long categoryId, String name, TransactionType type) {
        Category category = getCategoryForUser(userId, categoryId);
        category.setName(name);
        category.setType(type);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long userId, Long categoryId) {
        Category category = getCategoryForUser(userId, categoryId);
        categoryRepository.delete(category);
    }
}