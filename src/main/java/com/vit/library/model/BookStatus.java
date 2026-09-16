package com.vit.library.model;

public enum BookStatus {
    AVAILABLE,
    ISSUED,
    RESERVED,
    LOST;

    public String label() {
        return switch (this) {
            case AVAILABLE -> "Available";
            case ISSUED -> "Issued";
            case RESERVED -> "Reserved";
            case LOST -> "Lost";
        };
    }
}
