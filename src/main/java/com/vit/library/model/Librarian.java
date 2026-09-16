package com.vit.library.model;

public class Librarian extends Person {

    private final String staffId;

    public Librarian(String id, String name, String email, String phone, String staffId) {
        super(id, name, email, phone);
        this.staffId = staffId;
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    @Override
    public String describe() {
        return super.describe() + " | Staff ID: " + staffId;
    }

    public String getStaffId() {
        return staffId;
    }
}
