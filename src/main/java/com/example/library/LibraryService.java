package com.example.library;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private final List<Book> books;
    private final List<Member> members;
    private final DataStore dataStore;

    public LibraryService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.books = dataStore.loadBooks();
        this.members = dataStore.loadMembers();
    }

    public List<Book> getBooks() {
        return books.stream()
                .sorted(Comparator.comparingInt(Book::getId))
                .toList();
    }

    public List<Member> getMembers() {
        return members.stream()
                .sorted(Comparator.comparingInt(Member::getId))
                .toList();
    }

    public Book addBook(String title, String author) {
        Book book = new Book(nextBookId(), title, author, false, null);
        books.add(book);
        save();
        return book;
    }

    public Member addMember(String name, String phone) {
        Member member = new Member(nextMemberId(), name, phone);
        members.add(member);
        save();
        return member;
    }

    public List<Book> searchBooks(String keyword) {
        String query = keyword.toLowerCase();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query)
                        || book.getAuthor().toLowerCase().contains(query))
                .sorted(Comparator.comparingInt(Book::getId))
                .toList();
    }

    public String issueBook(int bookId, int memberId) {
        Optional<Book> book = findBook(bookId);
        Optional<Member> member = findMember(memberId);

        if (book.isEmpty()) {
            return "Book not found.";
        }
        if (member.isEmpty()) {
            return "Member not found.";
        }
        if (book.get().isIssued()) {
            return "Book is already issued.";
        }

        book.get().issueTo(memberId);
        save();
        return "Book issued successfully.";
    }

    public String returnBook(int bookId) {
        Optional<Book> book = findBook(bookId);

        if (book.isEmpty()) {
            return "Book not found.";
        }
        if (!book.get().isIssued()) {
            return "Book is already available.";
        }

        book.get().returnBook();
        save();
        return "Book returned successfully.";
    }

    private Optional<Book> findBook(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst();
    }

    private Optional<Member> findMember(int id) {
        return members.stream().filter(member -> member.getId() == id).findFirst();
    }

    private int nextBookId() {
        return books.stream().mapToInt(Book::getId).max().orElse(0) + 1;
    }

    private int nextMemberId() {
        return members.stream().mapToInt(Member::getId).max().orElse(0) + 1;
    }

    private void save() {
        dataStore.save(books, members);
    }
}
