package com.example.moneytracker_silarac.data; // <--- CORREGIDO

public class CategoryTotal {
    public String categoryName;
    public double totalAmount;
    public String color;

    public CategoryTotal(String categoryName, double totalAmount, String color) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.color = color;
    }
}