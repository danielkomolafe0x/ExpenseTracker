package com.kd.expense_tracker.service;

import com.kd.expense_tracker.exception.ResourceNotFoundException;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.Role;
import com.kd.expense_tracker.model.TransactionType;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.repository.CategoryRepository;
import com.kd.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User owner;
    private User otherUser;
    private Category category;

    private void setUpOwnedCategory() {
        owner = new User("alice", "alice@example.com", "hashed", Role.USER);
        setId(owner, 1L);

        otherUser = new User("bob", "bob@example.com", "hashed", Role.USER);
        setId(otherUser, 2L);

        category = new Category("Groceries", TransactionType.EXPENSE, owner);
        setId(category, 10L);
    }

    private void setId(Object entity, Long id) {
        if (entity instanceof User u) {
            u.setId(id);
        } else if (entity instanceof Category c) {
            c.setId(id);
        }
    }

    @Test
    void createCategory_savesCategoryForExistingUser() {
        setUpOwnedCategory();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.createCategory(1L, "Groceries", TransactionType.EXPENSE);

        assertEquals("Groceries", result.getName());
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(owner, result.getUser());
    }

    @Test
    void createCategory_throwsWhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.createCategory(99L, "Groceries", TransactionType.EXPENSE)
        );
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryForUser_returnsCategoryWhenOwnedByCaller() {
        setUpOwnedCategory();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryForUser(1L, 10L);

        assertEquals(category, result);
    }

    @Test
    void getCategoryForUser_throwsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getCategoryForUser(1L, 999L)
        );
    }

    @Test
    void getCategoryForUser_throwsWhenCategoryBelongsToAnotherUser() {
        setUpOwnedCategory();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        // category belongs to user 1 (owner); requesting as user 2 (otherUser) must fail
        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getCategoryForUser(2L, 10L)
        );
    }

    @Test
    void deleteCategory_deletesWhenOwnedByCaller() {
        setUpOwnedCategory();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L, 10L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void getCategoriesForUser_returnsAllCategoriesForThatUser() {
        setUpOwnedCategory();
        when(categoryRepository.findByUserId(1L)).thenReturn(List.of(category));

        List<Category> result = categoryService.getCategoriesForUser(1L);

        assertEquals(1, result.size());
        assertEquals(category, result.get(0));
    }
}