package com.personaldatastore.application.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personaldatastore.application.domain.Expense;
import com.personaldatastore.application.domain.Investment;
import com.personaldatastore.application.domain.Notification;
import com.personaldatastore.application.dto.ExpenseRequest;
import com.personaldatastore.application.dto.InvestmentRequest;
import com.personaldatastore.application.service.PortfolioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return portfolioService.getDashboardSummary();
    }

    @GetMapping("/investments")
    public List<Investment> getInvestments() {
        return portfolioService.getInvestments();
    }

    @PostMapping("/investments")
    public Investment createInvestment(@RequestBody InvestmentRequest request) {
        return portfolioService.createInvestment(request);
    }

    @PutMapping("/investments/{id}")
    public Investment updateInvestment(@PathVariable Long id, @RequestBody InvestmentRequest request) {
        return portfolioService.updateInvestment(id, request);
    }

    @DeleteMapping("/investments/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        portfolioService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        return portfolioService.getExpenses();
    }

    @PostMapping("/expenses")
    public Expense createExpense(@RequestBody ExpenseRequest request) {
        return portfolioService.createExpense(request);
    }

    @PutMapping("/expenses/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        return portfolioService.updateExpense(id, request);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        portfolioService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications() {
        return portfolioService.getNotifications();
    }
}
