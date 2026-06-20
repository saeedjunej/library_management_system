package com.example.library;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class DesktopApp extends JFrame {
    private final LibraryService libraryService;
    private final DefaultTableModel booksModel;
    private final DefaultTableModel membersModel;
    private final JTextField searchField;

    public DesktopApp() {
        super("Library Management System");
        this.libraryService = new LibraryService(new DataStore(Path.of("data")));
        this.booksModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.membersModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.searchField = new JTextField(24);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        refreshTables();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The app still works with Java's default look and feel.
            }
            new DesktopApp().setVisible(true);
        });
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));

        JLabel title = new JLabel("Library Management System");
        title.setFont(title.getFont().deriveFont(24f));

        JLabel subtitle = new JLabel("Manage books, members, issue records, and returns from one desktop screen.");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        JPanel text = new JPanel(new BorderLayout(0, 4));
        text.add(title, BorderLayout.NORTH);
        text.add(subtitle, BorderLayout.SOUTH);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Issue / Return", createIssuePanel());
        return tabs;
    }

    private JPanel createBooksPanel() {
        JTable table = new JTable(booksModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);

        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(event -> addBook());

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> searchBooks());

        JButton clearButton = new JButton("Show All");
        clearButton.addActionListener(event -> {
            searchField.setText("");
            refreshBooks(libraryService.getBooks());
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controls.add(new JLabel("Title or author"));
        controls.add(searchField);
        controls.add(searchButton);
        controls.add(clearButton);
        controls.add(addBookButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMembersPanel() {
        JTable table = new JTable(membersModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);

        JButton addMemberButton = new JButton("Register Member");
        addMemberButton.addActionListener(event -> addMember());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controls.add(addMemberButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createIssuePanel() {
        JTextField bookIdField = new JTextField(10);
        JTextField memberIdField = new JTextField(10);

        JButton issueButton = new JButton("Issue Book");
        issueButton.addActionListener(event -> {
            Integer bookId = readNumber(bookIdField, "Book ID");
            Integer memberId = readNumber(memberIdField, "Member ID");
            if (bookId == null || memberId == null) {
                return;
            }
            showMessage(libraryService.issueBook(bookId, memberId));
            refreshTables();
        });

        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(event -> {
            Integer bookId = readNumber(bookIdField, "Book ID");
            if (bookId == null) {
                return;
            }
            showMessage(libraryService.returnBook(bookId));
            refreshTables();
        });

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Book ID"), gbc);
        gbc.gridx = 1;
        form.add(bookIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Member ID"), gbc);
        gbc.gridx = 1;
        form.add(memberIdField, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.add(issueButton);
        actions.add(returnButton);

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(actions, gbc);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private void addBook() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JPanel form = twoFieldForm("Book title", titleField, "Author", authorField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                showMessage("Book title and author are required.");
                return;
            }
            libraryService.addBook(title, author);
            refreshTables();
        }
    }

    private void addMember() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JPanel form = twoFieldForm("Member name", nameField, "Phone", phoneField);

        int result = JOptionPane.showConfirmDialog(this, form, "Register Member", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                showMessage("Member name and phone are required.");
                return;
            }
            libraryService.addMember(name, phone);
            refreshTables();
        }
    }

    private JPanel twoFieldForm(String firstLabel, JTextField firstField, String secondLabel, JTextField secondField) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel(firstLabel), gbc);
        gbc.gridx = 1;
        firstField.setColumns(24);
        form.add(firstField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel(secondLabel), gbc);
        gbc.gridx = 1;
        secondField.setColumns(24);
        form.add(secondField, gbc);
        return form;
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshBooks(libraryService.getBooks());
            return;
        }
        refreshBooks(libraryService.searchBooks(keyword));
    }

    private Integer readNumber(JTextField field, String label) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage(label + " must be a number.");
            return null;
        }
    }

    private void refreshTables() {
        refreshBooks(libraryService.getBooks());
        refreshMembers();
    }

    private void refreshBooks(List<Book> books) {
        booksModel.setRowCount(0);
        for (Book book : books) {
            String status = book.isIssued() ? "Issued to member #" + book.getIssuedToMemberId() : "Available";
            booksModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), status});
        }
    }

    private void refreshMembers() {
        membersModel.setRowCount(0);
        for (Member member : libraryService.getMembers()) {
            membersModel.addRow(new Object[]{member.getId(), member.getName(), member.getPhone()});
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
