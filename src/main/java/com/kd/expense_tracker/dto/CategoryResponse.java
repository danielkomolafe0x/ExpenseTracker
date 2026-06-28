package com.kd.expense_tracker.dto;

import com.kd.expense_tracker.model.TransactionType;

public class CategoryResponse {

    private Long id;
    private String name;
    private TransactionType type;
    private Long userId;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, TransactionType type, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}