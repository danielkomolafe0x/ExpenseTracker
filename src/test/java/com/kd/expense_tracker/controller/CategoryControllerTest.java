package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.exception.ResourceNotFoundException;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.Role;
import com.kd.expense_tracker.model.TransactionType;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.security.UserPrincipal;
import com.kd.expense_tracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private UserPrincipal testPrincipal() {
        User user = new User("alice", "alice@example.com", "hashed", Role.USER);
        user.setId(1L);
        return new UserPrincipal(user);
    }

    @Test
    void getAll_returnsCategoriesForAuthenticatedUser() {
        Category category = new Category("Groceries", TransactionType.EXPENSE, new User());
        category.setId(10L);

        when(categoryService.getCategoriesForUser(1L)).thenReturn(List.of(category));

        ResponseEntity<?> response = categoryController.getAll(testPrincipal());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert response.getBody() != null;
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void create_savesAndReturns201() {
        Category saved = new Category("Groceries", TransactionType.EXPENSE, new User());
        saved.setId(10L);

        when(categoryService.createCategory(eq(1L), eq("Groceries"), eq(TransactionType.EXPENSE)))
                .thenReturn(saved);

        var request = new com.kd.expense_tracker.dto.CategoryRequest();
        request.setName("Groceries");
        request.setType(TransactionType.EXPENSE);

        ResponseEntity<?> response = categoryController.create(testPrincipal(), request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getOne_returnsCategory_whenOwnedByCaller() {
        Category category = new Category("Groceries", TransactionType.EXPENSE, new User());
        category.setId(10L);

        when(categoryService.getCategoryForUser(1L, 10L)).thenReturn(category);

        ResponseEntity<?> response = categoryController.getOne(testPrincipal(), 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getOne_throwsResourceNotFoundException_whenCategoryNotFound() {
        when(categoryService.getCategoryForUser(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                categoryController.getOne(testPrincipal(), 999L)
        );
    }

    @Test
    void delete_returns204_whenOwnedByCaller() {
        ResponseEntity<?> response = categoryController.delete(testPrincipal(), 10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).deleteCategory(1L, 10L);
    }

    @Test
    void update_returnsUpdatedCategory() {
        Category updated = new Category("Groceries & Household", TransactionType.EXPENSE, new User());
        updated.setId(10L);

        when(categoryService.updateCategory(eq(1L), eq(10L), any(), any()))
                .thenReturn(updated);

        var request = new com.kd.expense_tracker.dto.CategoryRequest();
        request.setName("Groceries & Household");
        request.setType(TransactionType.EXPENSE);

        ResponseEntity<?> response = categoryController.update(testPrincipal(), 10L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}