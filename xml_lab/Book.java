package org.example;

public class Book {
    private String id;
    private String title;
    private String author;
    private int year;
    private String category;
    private double price;
    private int totalCopies;
    private int availableCopies;

    public Book() {}

    public Book(String id, String title, String author, int year,
                String category, double price, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.category = category;
        this.price = price;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    @Override
    public String toString() {
        return String.format("Book{id='%s', title='%s', author='%s', year=%d, category='%s', price=%.2f, total=%d, available=%d}",
                id, title, author, year, category, price, totalCopies, availableCopies);
    }
}