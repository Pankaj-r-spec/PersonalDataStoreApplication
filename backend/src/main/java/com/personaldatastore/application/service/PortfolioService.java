package com.personaldatastore.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.personaldatastore.application.domain.Expense;
import com.personaldatastore.application.domain.Investment;
import com.personaldatastore.application.domain.Notification;
import com.personaldatastore.application.dto.ExpenseRequest;
import com.personaldatastore.application.dto.InvestmentRequest;
import com.personaldatastore.application.repository.ExpenseRepository;
import com.personaldatastore.application.repository.InvestmentRepository;
import com.personaldatastore.application.repository.NotificationRepository;

@Service
public class PortfolioService {

    private final InvestmentRepository investmentRepository;
    private final ExpenseRepository expenseRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    public PortfolioService(InvestmentRepository investmentRepository,
                           ExpenseRepository expenseRepository,
                           NotificationRepository notificationRepository,
                           NotificationEventPublisher notificationEventPublisher) {
        this.investmentRepository = investmentRepository;
        this.expenseRepository = expenseRepository;
        this.notificationRepository = notificationRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public Investment createInvestment(InvestmentRequest request) {
        Investment investment = new Investment(
                request.name(),
                request.type(),
                request.category(),
                request.amount(),
                request.currentValue(),
                request.purchaseDate(),
                request.notes()
        );

        Investment saved = investmentRepository.save(investment);
        notificationEventPublisher.publish("portfolio-events", Map.of(
                "event", "investment.created",
                "name", saved.getName(),
                "amount", saved.getAmount(),
                "type", saved.getType(),
                "createdAt", LocalDateTime.now().toString()
        ));
        return saved;
    }

    public Expense createExpense(ExpenseRequest request) {
        Expense expense = new Expense(
                request.category(),
                request.amount(),
                request.expenseDate(),
                request.recurring(),
                request.notes()
        );

        Expense saved = expenseRepository.save(expense);
        notificationEventPublisher.publish("personal-data-notifications", Map.of(
                "event", "expense.created",
                "category", saved.getCategory(),
                "amount", saved.getAmount(),
                "createdAt", LocalDateTime.now().toString()
        ));
        return saved;
    }

    public List<Investment> getInvestments() {
        return investmentRepository.findAll();
    }

    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    public Map<String, Object> getDashboardSummary() {
        double totalInvested = investmentRepository.findAll().stream()
                .mapToDouble(Investment::getAmount)
                .sum();

        double totalCurrentValue = investmentRepository.findAll().stream()
                .mapToDouble(Investment::getCurrentValue)
                .sum();

        double totalMonthlyExpenses = expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalInvested", totalInvested);
        summary.put("totalCurrentValue", totalCurrentValue);
        summary.put("totalMonthlyExpenses", totalMonthlyExpenses);
        summary.put("netPortfolioValue", totalCurrentValue - totalInvested);
        summary.put("emergencyFundStatus", totalCurrentValue > 0 ? "Healthy" : "Watchlist");
        return summary;
    }

    public Investment updateInvestment(Long id, InvestmentRequest request) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found: " + id));

        investment.setName(request.name());
        investment.setType(request.type());
        investment.setCategory(request.category());
        investment.setAmount(request.amount());
        investment.setCurrentValue(request.currentValue());
        investment.setPurchaseDate(request.purchaseDate());
        investment.setNotes(request.notes());

        Investment saved = investmentRepository.save(investment);
        notificationEventPublisher.publish("portfolio-events", Map.of(
                "event", "investment.updated",
                "name", saved.getName(),
                "amount", saved.getAmount(),
                "updatedAt", LocalDateTime.now().toString()
        ));
        return saved;
    }

    public Expense updateExpense(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));

        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setRecurring(request.recurring());
        expense.setNotes(request.notes());

        Expense saved = expenseRepository.save(expense);
        notificationEventPublisher.publish("personal-data-notifications", Map.of(
                "event", "expense.updated",
                "category", saved.getCategory(),
                "amount", saved.getAmount(),
                "updatedAt", LocalDateTime.now().toString()
        ));
        return saved;
    }

    public void deleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public void saveNotification(String type, String message) {
        notificationRepository.save(new Notification(type, message, LocalDateTime.now()));
    }
}
