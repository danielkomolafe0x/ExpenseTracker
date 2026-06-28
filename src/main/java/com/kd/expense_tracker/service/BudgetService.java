package com.kd.expense_tracker.service;

import com.kd.expense_tracker.exception.InvalidRequestException;
import com.kd.expense_tracker.exception.ResourceNotFoundException;
import com.kd.expense_tracker.model.Budget;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.repository.BudgetRepository;
import com.kd.expense_tracker.repository.CategoryRepository;
import com.kd.expense_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Budget createBudget(Long userId, Long categoryId, BigDecimal limitAmount,
                               LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateDateRange(startDate, endDate);
        Category category = categoryId != null ? getOwnedCategory(userId, categoryId) : null;

        Budget budget = new Budget(limitAmount, startDate, endDate, category, user);
        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsForUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget getBudgetForUser(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget not found");
        }

        return budget;
    }

    public Budget updateBudget(Long userId, Long budgetId, Long categoryId, BigDecimal limitAmount,
                               LocalDate startDate, LocalDate endDate) {
        Budget budget = getBudgetForUser(userId, budgetId);
        validateDateRange(startDate, endDate);
        Category category = categoryId != null ? getOwnedCategory(userId, categoryId) : null;

        budget.setLimitAmount(limitAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budget.setCategory(category);

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = getBudgetForUser(userId, budgetId);
        budgetRepository.delete(budget);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("End date must be after start date");
        }
    }

    private Category getOwnedCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        return category;
    }
}