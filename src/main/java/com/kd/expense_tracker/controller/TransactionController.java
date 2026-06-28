package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.TransactionRequest;
import com.kd.expense_tracker.dto.TransactionResponse;
import com.kd.expense_tracker.model.Transaction;
import com.kd.expense_tracker.service.TransactionService;
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
@RequestMapping("/api/users/{userId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@PathVariable Long userId,
                                                      @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                userId,
                request.getCategoryId(),
                request.getAmount(),
                request.getDescription(),
                request.getDate(),
                request.getType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(@PathVariable Long userId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsForUser(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getOne(@PathVariable Long userId,
                                                      @PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionForUser(userId, transactionId);
        return ResponseEntity.ok(toResponse(transaction));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long userId,
                                                      @PathVariable Long transactionId,
                                                      @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.updateTransaction(
                userId,
                transactionId,
                request.getCategoryId(),
                request.getAmount(),
                request.getDescription(),
                request.getDate(),
                request.getType()
        );
        return ResponseEntity.ok(toResponse(transaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getUser().getId()
        );
    }
}