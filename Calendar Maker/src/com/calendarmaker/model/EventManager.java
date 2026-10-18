package com.calendarmaker.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Manages calendar events, user notes, and integration with holiday provider.
 * Supports file persistence via standard JSON format without third-party dependencies.
 */
public class EventManager {

    public static final String DEFAULT_STORAGE_FILE = "events.json";

    private final List<CalendarEvent> customEvents = new ArrayList<>();
    private boolean includeHolidays = true;

    public EventManager() {
        // Automatically attempt loading existing events.json if present
        Path defaultPath = Paths.get(DEFAULT_STORAGE_FILE);
        if (Files.exists(defaultPath)) {
            try {
                loadFromFile(defaultPath);
            } catch (Exception ignored) {
                // Ignore initial file load issues if file is empty or corrupted
            }
        }
    }

    public boolean isIncludeHolidays() {
        return includeHolidays;
    }

    public void setIncludeHolidays(boolean includeHolidays) {
        this.includeHolidays = includeHolidays;
    }

    public synchronized void addEvent(CalendarEvent event) {
        if (event != null) {
            customEvents.add(event);
            Collections.sort(customEvents);
        }
    }

    public synchronized boolean removeEvent(String id) {
        if (id == null) return false;
        return customEvents.removeIf(e -> e.getId().equalsIgnoreCase(id.trim()));
    }

    public synchronized List<CalendarEvent> getCustomEvents() {
        return Collections.unmodifiableList(new ArrayList<>(customEvents));
    }

    public synchronized void clearCustomEvents() {
        customEvents.clear();
    }

    /**
     * Gets all events (custom + optional holidays) occurring on a specific date.
     */
    public synchronized List<CalendarEvent> getEventsForDate(LocalDate date) {
        List<CalendarEvent> results = new ArrayList<>();
        for (CalendarEvent e : customEvents) {
            if (e.matchesDate(date)) {
                results.add(e);
            }
        }

        if (includeHolidays) {
            List<CalendarEvent> holidays = HolidayProvider.getHolidaysForYear(date.getYear());
            for (CalendarEvent h : holidays) {
                if (h.matchesDate(date)) {
                    results.add(h);
                }
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Gets all events occurring in a given month.
     */
    public synchronized List<CalendarEvent> getEventsForMonth(YearMonth ym) {
        List<CalendarEvent> results = new ArrayList<>();
        int year = ym.getYear();
        int monthVal = ym.getMonthValue();

        for (CalendarEvent e : customEvents) {
            if (e.isRecurringAnnual()) {
                if (e.getDate().getMonthValue() == monthVal) {
                    results.add(e);
                }
            } else if (e.getDate().getYear() == year && e.getDate().getMonthValue() == monthVal) {
                results.add(e);
            }
        }

        if (includeHolidays) {
            results.addAll(HolidayProvider.getHolidaysForMonth(ym));
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Gets all events occurring in a given year.
     */
    public synchronized List<CalendarEvent> getEventsForYear(int year) {
        List<CalendarEvent> results = new ArrayList<>();
        for (CalendarEvent e : customEvents) {
            if (e.isRecurringAnnual() || e.getDate().getYear() == year) {
                results.add(e);
            }
        }

        if (includeHolidays) {
            results.addAll(HolidayProvider.getHolidaysForYear(year));
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Saves custom events to file as readable JSON.
     */
    public synchronized void saveToFile(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < customEvents.size(); i++) {
            CalendarEvent e = customEvents.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(escapeJson(e.getId())).append("\",\n");
            sb.append("    \"title\": \"").append(escapeJson(e.getTitle())).append("\",\n");
            sb.append("    \"date\": \"").append(e.getDate()).append("\",\n");
            sb.append("    \"category\": \"").append(e.getCategory().name()).append("\",\n");
            sb.append("    \"description\": \"").append(escapeJson(e.getDescription())).append("\",\n");
            sb.append("    \"recurringAnnual\": ").append(e.isRecurringAnnual()).append("\n");
            sb.append("  }");
            if (i < customEvents.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(sb.toString());
        }
    }

    /**
     * Loads custom events from JSON file.
     */
    public synchronized void loadFromFile(Path path) throws IOException {
        if (!Files.exists(path)) return;

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        List<CalendarEvent> loaded = parseJsonEvents(content.toString());
        customEvents.clear();
        customEvents.addAll(loaded);
        Collections.sort(customEvents);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    /**
     * Lightweight JSON array of objects parser for CalendarEvents.
     */
    private static List<CalendarEvent> parseJsonEvents(String json) {
        List<CalendarEvent> list = new ArrayList<>();
        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return list;
        }

        // Split objects by finding matching curly braces
        int depth = 0;
        int objStart = -1;
        boolean inQuote = false;
        boolean escapeNext = false;

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (escapeNext) {
                escapeNext = false;
                continue;
            }
            if (c == '\\') {
                escapeNext = true;
                continue;
            }
            if (c == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (!inQuote) {
                if (c == '{') {
                    if (depth == 0) {
                        objStart = i;
                    }
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && objStart != -1) {
                        String objContent = trimmed.substring(objStart + 1, i);
                        CalendarEvent ev = parseSingleEvent(objContent);
                        if (ev != null) {
                            list.add(ev);
                        }
                        objStart = -1;
                    }
                }
            }
        }
        return list;
    }

    private static CalendarEvent parseSingleEvent(String objContent) {
        Map<String, String> map = new HashMap<>();
        String[] lines = objContent.split("\n");
        for (String line : lines) {
            String l = line.trim();
            if (l.endsWith(",")) {
                l = l.substring(0, l.length() - 1).trim();
            }
            int colonIdx = l.indexOf(':');
            if (colonIdx > 0) {
                String key = l.substring(0, colonIdx).trim().replace("\"", "");
                String val = l.substring(colonIdx + 1).trim();
                if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(key, unescapeJson(val));
            }
        }

        try {
            String id = map.get("id");
            String title = map.getOrDefault("title", "Untitled");
            String dateStr = map.get("date");
            if (dateStr == null) return null;
            LocalDate date = LocalDate.parse(dateStr);
            EventCategory cat = EventCategory.fromString(map.get("category"));
            String desc = map.getOrDefault("description", "");
            boolean recur = Boolean.parseBoolean(map.getOrDefault("recurringAnnual", "false"));

            return new CalendarEvent(id, title, date, cat, desc, recur);
        } catch (Exception e) {
            return null;
        }
    }
}
