package com.example.library;

public class Book {
    private final int id;
    private String title;
    private String author;
    private boolean issued;
    private Integer issuedToMemberId;

    public Book(int id, String title, String author, boolean issued, Integer issuedToMemberId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.issued = issued;
        this.issuedToMemberId = issuedToMemberId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return issued;
    }

    public Integer getIssuedToMemberId() {
        return issuedToMemberId;
    }

    public void update(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public void issueTo(int memberId) {
        this.issued = true;
        this.issuedToMemberId = memberId;
    }

    public void returnBook() {
        this.issued = false;
        this.issuedToMemberId = null;
    }

    public String toDataRow() {
        return id + "|" + DataStore.escape(title) + "|" + DataStore.escape(author) + "|" + issued + "|"
                + (issuedToMemberId == null ? "" : issuedToMemberId);
    }

    public static Book fromDataRow(String row) {
        String[] parts = DataStore.split(row, 5);
        Integer memberId = parts[4].isBlank() ? null : Integer.parseInt(parts[4]);
        return new Book(
                Integer.parseInt(parts[0]),
                DataStore.unescape(parts[1]),
                DataStore.unescape(parts[2]),
                Boolean.parseBoolean(parts[3]),
                memberId);
    }

    @Override
    public String toString() {
        String status = issued ? "Issued to member #" + issuedToMemberId : "Available";
        return String.format("#%d | %s | %s | %s", id, title, author, status);
    }
}
