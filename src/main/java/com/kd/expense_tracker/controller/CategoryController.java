package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.CategoryRequest;
import com.kd.expense_tracker.dto.CategoryResponse;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.security.UserPrincipal;
import com.kd.expense_tracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.createCategory(principal.getId(), request.getName(), request.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        List<CategoryResponse> categories = categoryService.getCategoriesForUser(principal.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getOne(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long categoryId) {
        Category category = categoryService.getCategoryForUser(principal.getId(), categoryId);
        return ResponseEntity.ok(toResponse(category));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long categoryId,
                                                   @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.updateCategory(principal.getId(), categoryId, request.getName(), request.getType());
        return ResponseEntity.ok(toResponse(category));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                       @PathVariable Long categoryId) {
        categoryService.deleteCategory(principal.getId(), categoryId);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getUser().getId()
        );
    }
}