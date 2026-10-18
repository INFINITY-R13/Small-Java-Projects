package com.calendarmaker.model;

import com.calendarmaker.util.DateUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a calendar event, holiday, reminder, or birthday.
 */
public class CalendarEvent implements Comparable<CalendarEvent> {

    private final String id;
    private String title;
    private LocalDate date;
    private EventCategory category;
    private String description;
    private boolean recurringAnnual;

    public CalendarEvent(String id, String title, LocalDate date, EventCategory category, String description, boolean recurringAnnual) {
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString().substring(0, 8) : id;
        this.title = title == null ? "Untitled Event" : title.trim();
        this.date = date == null ? LocalDate.now() : date;
        this.category = category == null ? EventCategory.OTHER : category;
        this.description = description == null ? "" : description.trim();
        this.recurringAnnual = recurringAnnual;
    }

    public CalendarEvent(String title, LocalDate date, EventCategory category, String description, boolean recurringAnnual) {
        this(null, title, date, category, description, recurringAnnual);
    }

    public CalendarEvent(String title, LocalDate date, EventCategory category, String description) {
        this(null, title, date, category, description, false);
    }

    public CalendarEvent(String title, LocalDate date, EventCategory category) {
        this(null, title, date, category, "", false);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRecurringAnnual() {
        return recurringAnnual;
    }

    public void setRecurringAnnual(boolean recurringAnnual) {
        this.recurringAnnual = recurringAnnual;
    }

    /**
     * Checks if this event occurs on the given date (accounting for annual recurrence).
     */
    public boolean matchesDate(LocalDate queryDate) {
        if (queryDate == null) return false;
        if (recurringAnnual) {
            return this.date.getMonth() == queryDate.getMonth() &&
                   this.date.getDayOfMonth() == queryDate.getDayOfMonth();
        }
        return this.date.equals(queryDate);
    }

    @Override
    public int compareTo(CalendarEvent o) {
        if (o == null) return 1;
        int dateCmp = this.date.compareTo(o.date);
        if (dateCmp != 0) return dateCmp;
        return this.title.compareToIgnoreCase(o.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarEvent that = (CalendarEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s%s (%s)",
                category.getDisplayName(),
                DateUtils.formatIso(date),
                title,
                recurringAnnual ? " (Annual)" : "",
                description.isEmpty() ? "No description" : description);
    }
}
