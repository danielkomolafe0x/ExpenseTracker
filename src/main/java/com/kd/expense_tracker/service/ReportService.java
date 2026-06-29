package com.kd.expense_tracker.service;

import com.kd.expense_tracker.dto.CategorySpendingResponse;
import com.kd.expense_tracker.dto.SpendingSummaryResponse;
import com.kd.expense_tracker.exception.InvalidRequestException;
import com.kd.expense_tracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<CategorySpendingResponse> getSpendingByCategory(Long userId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return transactionRepository.getSpendingByCategory(userId, startDate, endDate);
    }

    public SpendingSummaryResponse getSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        BigDecimal totalIncome = transactionRepository.sumIncomeByUserAndDateRange(userId, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserAndDateRange(userId, startDate, endDate);
        return new SpendingSummaryResponse(startDate, endDate, totalIncome, totalExpense);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("End date must be after start date");
        }
    }
}