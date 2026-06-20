package com.example.library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private final Path dataDirectory;
    private final Path booksFile;
    private final Path membersFile;

    public DataStore(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.booksFile = dataDirectory.resolve("books.txt");
        this.membersFile = dataDirectory.resolve("members.txt");
    }

    public List<Book> loadBooks() {
        if (!Files.exists(booksFile)) {
            return seedBooks();
        }

        try {
            List<Book> books = new ArrayList<>();
            for (String row : Files.readAllLines(booksFile, StandardCharsets.UTF_8)) {
                if (!row.isBlank()) {
                    books.add(Book.fromDataRow(row));
                }
            }
            return books;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read books data.", ex);
        }
    }

    public List<Member> loadMembers() {
        if (!Files.exists(membersFile)) {
            return new ArrayList<>();
        }

        try {
            List<Member> members = new ArrayList<>();
            for (String row : Files.readAllLines(membersFile, StandardCharsets.UTF_8)) {
                if (!row.isBlank()) {
                    members.add(Member.fromDataRow(row));
                }
            }
            return members;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read members data.", ex);
        }
    }

    public void save(List<Book> books, List<Member> members) {
        try {
            Files.createDirectories(dataDirectory);
            Files.write(booksFile, books.stream().map(Book::toDataRow).toList(), StandardCharsets.UTF_8);
            Files.write(membersFile, members.stream().map(Member::toDataRow).toList(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save library data.", ex);
        }
    }

    public static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n");
    }

    public static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;

        for (char c : value.toCharArray()) {
            if (escaping) {
                if (c == 'p') {
                    result.append('|');
                } else if (c == 'n') {
                    result.append('\n');
                } else {
                    result.append(c);
                }
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                result.append(c);
            }
        }

        if (escaping) {
            result.append('\\');
        }
        return result.toString();
    }

    public static String[] split(String row, int expectedParts) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (char c : row.toCharArray()) {
            if (escaping) {
                current.append('\\').append(c);
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else if (c == '|') {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (escaping) {
            current.append('\\');
        }
        parts.add(current.toString());

        if (parts.size() != expectedParts) {
            throw new IllegalArgumentException("Invalid data row: " + row);
        }

        return parts.toArray(String[]::new);
    }

    private List<Book> seedBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Clean Code", "Robert C. Martin", false, null));
        books.add(new Book(2, "Effective Java", "Joshua Bloch", false, null));
        books.add(new Book(3, "Head First Java", "Kathy Sierra", false, null));
        return books;
    }
}
