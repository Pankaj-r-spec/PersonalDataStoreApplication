package com.personaldatastore.application.controller;

import com.personaldatastore.application.dto.ForecastResponse;
import com.personaldatastore.application.service.AnalyticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics/forecast")
    public ForecastResponse getForecast() {
        return analyticsService.generateForecast();
    }
}
