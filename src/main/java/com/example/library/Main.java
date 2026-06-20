package com.example.library;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final LibraryService libraryService;

    public Main() {
        this.libraryService = new LibraryService(new DataStore(Path.of("data")));
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("Library Management System");

        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> listBooks();
                case 2 -> addBook();
                case 3 -> registerMember();
                case 4 -> listMembers();
                case 5 -> issueBook();
                case 6 -> returnBook();
                case 7 -> searchBooks();
                case 0 -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. View all books");
        System.out.println("2. Add book");
        System.out.println("3. Register member");
        System.out.println("4. View all members");
        System.out.println("5. Issue book");
        System.out.println("6. Return book");
        System.out.println("7. Search books");
        System.out.println("0. Exit");
    }

    private void listBooks() {
        printRows("Books", libraryService.getBooks());
    }

    private void addBook() {
        String title = readRequiredText("Book title: ");
        String author = readRequiredText("Author name: ");
        Book book = libraryService.addBook(title, author);
        System.out.println("Added book: " + book);
    }

    private void registerMember() {
        String name = readRequiredText("Member name: ");
        String phone = readRequiredText("Phone number: ");
        Member member = libraryService.addMember(name, phone);
        System.out.println("Registered member: " + member);
    }

    private void listMembers() {
        printRows("Members", libraryService.getMembers());
    }

    private void issueBook() {
        int bookId = readInt("Book ID: ");
        int memberId = readInt("Member ID: ");
        System.out.println(libraryService.issueBook(bookId, memberId));
    }

    private void returnBook() {
        int bookId = readInt("Book ID: ");
        System.out.println(libraryService.returnBook(bookId));
    }

    private void searchBooks() {
        String keyword = readRequiredText("Search title or author: ");
        printRows("Search results", libraryService.searchBooks(keyword));
    }

    private <T> void printRows(String heading, List<T> rows) {
        System.out.println();
        System.out.println(heading);
        System.out.println("-".repeat(heading.length()));

        if (rows.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        rows.forEach(System.out::println);
    }

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
