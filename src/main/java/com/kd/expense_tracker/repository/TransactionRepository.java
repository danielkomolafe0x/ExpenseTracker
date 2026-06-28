package com.kd.expense_tracker.repository;

import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.Transaction;
import com.kd.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserAndCategory(User user, Category category);
}