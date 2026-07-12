package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.TransactionRequest;
import com.kd.expense_tracker.dto.TransactionResponse;
import com.kd.expense_tracker.model.Transaction;
import com.kd.expense_tracker.security.UserPrincipal;
import com.kd.expense_tracker.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "CRUD operations for income and expense transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                      @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                principal.getId(),
                request.getCategoryId(),
                request.getAmount(),
                request.getDescription(),
                request.getDate(),
                request.getType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        List<TransactionResponse> transactions = transactionService.getTransactionsForUser(principal.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getOne(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionForUser(principal.getId(), transactionId);
        return ResponseEntity.ok(toResponse(transaction));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long transactionId,
                                                      @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.updateTransaction(
                principal.getId(),
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
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                       @PathVariable Long transactionId) {
        transactionService.deleteTransaction(principal.getId(), transactionId);
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