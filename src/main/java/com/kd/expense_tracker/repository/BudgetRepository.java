package com.kd.expense_tracker.repository;

import com.kd.expense_tracker.model.Budget;
import com.kd.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    List<Budget> findByUserId(Long userId);
}