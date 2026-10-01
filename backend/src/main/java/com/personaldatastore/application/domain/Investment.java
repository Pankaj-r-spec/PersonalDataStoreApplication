package com.personaldatastore.application.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Node("Investment")
public class Investment {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("type")
    private String type;

    @Property("category")
    private String category;

    @Property("amount")
    private double amount;

    @Property("currentValue")
    private double currentValue;

    @Property("purchaseDate")
    private LocalDate purchaseDate;

    @Property("notes")
    private String notes;

    public Investment() {
    }

    public Investment(String name, String type, String category, double amount, double currentValue, LocalDate purchaseDate, String notes) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.currentValue = currentValue;
        this.purchaseDate = purchaseDate;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
