package com.kd.expense_tracker.controller;

import com.kd.expense_tracker.dto.CategorySpendingResponse;
import com.kd.expense_tracker.dto.SpendingSummaryResponse;
import com.kd.expense_tracker.security.UserPrincipal;
import com.kd.expense_tracker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Spending summaries by category and date range")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/by-category")
    @Operation(summary = "Spending by category",
            description = "Returns total EXPENSE amounts grouped by category for the given date range.")
    public ResponseEntity<List<CategorySpendingResponse>> getSpendingByCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Start of the date range (inclusive), format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End of the date range (inclusive), format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getSpendingByCategory(principal.getId(), startDate, endDate));
    }

    @GetMapping("/summary")
    @Operation(summary = "Income/expense summary",
            description = "Returns total income, total expense, and net amount for the given date range.")
    public ResponseEntity<SpendingSummaryResponse> getSummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Start of the date range (inclusive), format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End of the date range (inclusive), format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getSummary(principal.getId(), startDate, endDate));
    }
}