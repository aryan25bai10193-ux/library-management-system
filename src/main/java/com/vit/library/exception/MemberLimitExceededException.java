package com.vit.library.exception;

public class MemberLimitExceededException extends LibraryException {
    public MemberLimitExceededException(String memberId, int limit) {
        super("Member '" + memberId + "' has already reached their borrowing limit of " + limit + " book(s).");
    }
}
