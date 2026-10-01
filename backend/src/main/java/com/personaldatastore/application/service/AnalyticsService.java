package com.personaldatastore.application.service;

import com.personaldatastore.application.domain.Expense;
import com.personaldatastore.application.domain.Investment;
import com.personaldatastore.application.dto.ForecastResponse;
import com.personaldatastore.application.repository.ExpenseRepository;
import com.personaldatastore.application.repository.InvestmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final InvestmentRepository investmentRepository;
    private final ExpenseRepository expenseRepository;

    public AnalyticsService(InvestmentRepository investmentRepository, ExpenseRepository expenseRepository) {
        this.investmentRepository = investmentRepository;
        this.expenseRepository = expenseRepository;
    }

    public ForecastResponse generateForecast() {
        double totalCurrentValue = investmentRepository.findAll().stream()
                .mapToDouble(Investment::getCurrentValue)
                .sum();

        double totalMonthlyExpenses = expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double liquidityCoverageRatio = totalCurrentValue > 0 ? totalCurrentValue / Math.max(totalMonthlyExpenses, 1) : 0;
        double projectedCashFlowNextMonth = totalCurrentValue - totalMonthlyExpenses;

        String riskLevel = "Low";
        if (liquidityCoverageRatio < 2) {
            riskLevel = "Moderate";
        }
        if (liquidityCoverageRatio < 1) {
            riskLevel = "High";
        }

        List<String> suggestions = new ArrayList<>();
        suggestions.add("Maintain at least 3 months of essential spending in emergency fund.");
        suggestions.add("Review recurring expense categories with rising monthly totals.");
        suggestions.add("Rebalance investment allocation if short-term liquidity falls below target.");

        return new ForecastResponse(riskLevel, projectedCashFlowNextMonth, liquidityCoverageRatio, suggestions);
    }
}
