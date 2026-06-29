package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.BudgetRequest;
import com.kd.expense_tracker.dto.BudgetResponse;
import com.kd.expense_tracker.model.Budget;
import com.kd.expense_tracker.security.UserPrincipal;
import com.kd.expense_tracker.service.BudgetService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "CRUD operations for spending budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.createBudget(
                principal.getId(),
                request.getCategoryId(),
                request.getLimitAmount(),
                request.getStartDate(),
                request.getEndDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(budget));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        List<BudgetResponse> budgets = budgetService.getBudgetsForUser(principal.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getOne(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetForUser(principal.getId(), budgetId);
        return ResponseEntity.ok(toResponse(budget));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable Long budgetId,
                                                 @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.updateBudget(
                principal.getId(),
                budgetId,
                request.getCategoryId(),
                request.getLimitAmount(),
                request.getStartDate(),
                request.getEndDate()
        );
        return ResponseEntity.ok(toResponse(budget));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                       @PathVariable Long budgetId) {
        budgetService.deleteBudget(principal.getId(), budgetId);
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