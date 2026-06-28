package com.kd.expense_tracker.service;

import com.kd.expense_tracker.exception.ResourceNotFoundException;
import com.kd.expense_tracker.model.Category;
import com.kd.expense_tracker.model.Transaction;
import com.kd.expense_tracker.model.TransactionType;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.repository.CategoryRepository;
import com.kd.expense_tracker.repository.TransactionRepository;
import com.kd.expense_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction createTransaction(Long userId, Long categoryId, BigDecimal amount,
                                         String description, LocalDate date, TransactionType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = getOwnedCategory(userId, categoryId);

        Transaction transaction = new Transaction(amount, description, date, type, category, user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsForUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction getTransactionForUser(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction not found");
        }

        return transaction;
    }

    public Transaction updateTransaction(Long userId, Long transactionId, Long categoryId, BigDecimal amount,
                                         String description, LocalDate date, TransactionType type) {
        Transaction transaction = getTransactionForUser(userId, transactionId);
        Category category = getOwnedCategory(userId, categoryId);

        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);
        transaction.setType(type);
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = getTransactionForUser(userId, transactionId);
        transactionRepository.delete(transaction);
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