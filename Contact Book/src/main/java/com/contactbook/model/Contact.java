package com.contactbook.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a contact in the contact book.
 */
public class Contact {

    private String id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate; // nullable
    private String instagram;   // nullable, stored without @
    private long createdAt;

    /** Default constructor for Gson deserialization. */
    public Contact() {
    }

    /** Creates a new contact with a fresh UUID and current timestamp. */
    public Contact(String name, String email, String phone, LocalDate birthDate, String instagram) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.instagram = instagram;
        this.createdAt = System.currentTimeMillis();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getPhone() { return phone; }

    public LocalDate getBirthDate() { return birthDate; }

    public String getInstagram() { return instagram; }

    public long getCreatedAt() { return createdAt; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public void setInstagram(String instagram) { this.instagram = instagram; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Returns up to 2 uppercase initials derived from the contact's name words.
     * E.g. "John Doe" → "JD", "Alice" → "A".
     */
    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, 1).toUpperCase();
        }
        return (words[0].substring(0, 1) + words[words.length - 1].substring(0, 1)).toUpperCase();
    }

    /**
     * Returns "@instagram" if instagram is set, otherwise an empty string.
     */
    public String getDisplayInstagram() {
        if (instagram == null || instagram.isBlank()) return "";
        return "@" + instagram.trim();
    }

    /**
     * Returns true if today is the contact's birthday (same month and day).
     */
    public boolean isBirthdayToday() {
        if (birthDate == null) return false;
        LocalDate today = LocalDate.now();
        return birthDate.getMonthValue() == today.getMonthValue()
                && birthDate.getDayOfMonth() == today.getDayOfMonth();
    }

    @Override
    public String toString() {
        return "Contact{id='" + id + "', name='" + name + "'}";
    }
}
