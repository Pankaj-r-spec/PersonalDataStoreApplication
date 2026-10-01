package com.personaldatastore.application.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Node("Expense")
public class Expense {

    @Id
    @GeneratedValue
    private Long id;

    @Property("category")
    private String category;

    @Property("amount")
    private double amount;

    @Property("expenseDate")
    private LocalDate expenseDate;

    @Property("recurring")
    private boolean recurring;

    @Property("notes")
    private String notes;

    public Expense() {
    }

    public Expense(String category, double amount, LocalDate expenseDate, boolean recurring, String notes) {
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.recurring = recurring;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
