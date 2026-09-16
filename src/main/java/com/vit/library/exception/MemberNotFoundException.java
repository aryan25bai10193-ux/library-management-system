package com.vit.library.exception;

public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String memberId) {
        super("No member found with ID '" + memberId + "'.");
    }
}
