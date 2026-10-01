package com.personaldatastore.application.dto;

import java.util.List;

public record ForecastResponse(
        String riskLevel,
        double projectedCashFlowNextMonth,
        double liquidityCoverageRatio,
        List<String> suggestions
) {
}
