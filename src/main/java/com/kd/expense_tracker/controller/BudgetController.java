package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.BudgetRequest;
import com.kd.expense_tracker.dto.BudgetResponse;
import com.kd.expense_tracker.model.Budget;
import com.kd.expense_tracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/users/{userId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@PathVariable Long userId,
                                                 @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.createBudget(
                userId,
                request.getCategoryId(),
                request.getLimitAmount(),
                request.getStartDate(),
                request.getEndDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(budget));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll(@PathVariable Long userId) {
        List<BudgetResponse> budgets = budgetService.getBudgetsForUser(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getOne(@PathVariable Long userId,
                                                 @PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetForUser(userId, budgetId);
        return ResponseEntity.ok(toResponse(budget));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> update(@PathVariable Long userId,
                                                 @PathVariable Long budgetId,
                                                 @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.updateBudget(
                userId,
                budgetId,
                request.getCategoryId(),
                request.getLimitAmount(),
                request.getStartDate(),
                request.getEndDate()
        );
        return ResponseEntity.ok(toResponse(budget));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long budgetId) {
        budgetService.deleteBudget(userId, budgetId);
        return ResponseEntity.noContent().build();
    }

    private BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getLimitAmount(),
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getCategory() != null ? budget.getCategory().getId() : null,
                budget.getCategory() != null ? budget.getCategory().getName() : null,
                budget.getUser().getId()
        );
    }
}