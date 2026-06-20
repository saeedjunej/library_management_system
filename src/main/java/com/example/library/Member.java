package com.example.library;

public class Member {
    private final int id;
    private String name;
    private String phone;

    public Member(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String toDataRow() {
        return id + "|" + DataStore.escape(name) + "|" + DataStore.escape(phone);
    }

    public static Member fromDataRow(String row) {
        String[] parts = DataStore.split(row, 3);
        return new Member(
                Integer.parseInt(parts[0]),
                DataStore.unescape(parts[1]),
                DataStore.unescape(parts[2]));
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s", id, name, phone);
    }
}
