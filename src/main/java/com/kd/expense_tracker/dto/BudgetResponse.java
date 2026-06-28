package com.kd.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetResponse {

    private Long id;
    private BigDecimal limitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
    private String categoryName;
    private Long userId;

    public BudgetResponse() {
    }

    public BudgetResponse(Long id, BigDecimal limitAmount, LocalDate startDate, LocalDate endDate,
                          Long categoryId, String categoryName, Long userId) {
        this.id = id;
        this.limitAmount = limitAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}