package com.kd.expense_tracker.repository;

import com.kd.expense_tracker.dto.CategorySpendingResponse;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.Transaction;
import com.kd.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserAndCategory(User user, Category category);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'INCOME' AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomeByUserAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseByUserAndDateRange(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.kd.expense_tracker.dto.CategorySpendingResponse(c.id, c.name, SUM(t.amount)) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<CategorySpendingResponse> getSpendingByCategory(@Param("userId") Long userId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}