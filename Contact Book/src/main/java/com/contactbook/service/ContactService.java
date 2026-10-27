package com.contactbook.service;

import com.contactbook.model.Contact;
import com.contactbook.storage.ContactStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory service layer for managing contacts.
 * All mutations are automatically persisted.
 */
public class ContactService {

    private final ContactStorage storage;
    private final List<Contact> contacts;

    public ContactService() {
        storage  = new ContactStorage();
        contacts = new ArrayList<>(storage.loadContacts());
        sortContacts();
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /** Adds a new contact and persists the change. */
    public void addContact(Contact contact) {
        contacts.add(contact);
        sortContacts();
        persist();
    }

    /**
     * Replaces the contact whose id matches {@code updated.getId()} and persists.
     *
     * @throws IllegalArgumentException if no contact with that id exists
     */
    public void updateContact(Contact updated) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(updated.getId())) {
                contacts.set(i, updated);
                sortContacts();
                persist();
                return;
            }
        }
        throw new IllegalArgumentException("Contact not found: " + updated.getId());
    }

    /**
     * Removes the contact with the given id and persists the change.
     *
     * @return true if a contact was removed
     */
    public boolean deleteContact(String id) {
        boolean removed = contacts.removeIf(c -> c.getId().equals(id));
        if (removed) persist();
        return removed;
    }

    /** Returns an unmodifiable view of all contacts (sorted). */
    public List<Contact> getAllContacts() {
        return List.copyOf(contacts);
    }

    /**
     * Returns contacts whose name, email, phone, or instagram contain the query
     * (case-insensitive). If query is blank, returns all contacts.
     */
    public List<Contact> searchContacts(String query) {
        if (query == null || query.isBlank()) return getAllContacts();
        String q = query.trim().toLowerCase();
        return contacts.stream()
                .filter(c -> matches(c, q))
                .collect(Collectors.toList());
    }

    /** Finds a contact by id. */
    public Optional<Contact> findById(String id) {
        return contacts.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean matches(Contact c, String query) {
        return containsIgnoreCase(c.getName(),      query)
            || containsIgnoreCase(c.getEmail(),     query)
            || containsIgnoreCase(c.getPhone(),     query)
            || containsIgnoreCase(c.getInstagram(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void sortContacts() {
        contacts.sort(Comparator.comparing(
                c -> c.getName() == null ? "" : c.getName().toLowerCase()));
    }

    private void persist() {
        storage.saveContacts(contacts);
    }
}
