package com.calendarmaker.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

/**
 * Computes Indian national holidays, gazetted holidays, and major religious & cultural festivals.
 */
public class HolidayProvider {

    // Dynamic lunar/solar festival entries by year: YYYY -> list of (title, month, day, description)
    private static final Map<Integer, List<FestivalEntry>> FESTIVALS_BY_YEAR = new HashMap<>();

    static {
        // 2024
        registerFestivals(2024, Arrays.asList(
                new FestivalEntry("Maha Shivratri", 3, 8, "Gazetted / Cultural Festival"),
                new FestivalEntry("Holi", 3, 25, "Gazetted Holiday / Festival of Colors"),
                new FestivalEntry("Good Friday", 3, 29, "Gazetted Holiday"),
                new FestivalEntry("Eid-ul-Fitr", 4, 11, "Gazetted Holiday / Ramzan Eid"),
                new FestivalEntry("Mahavir Jayanti", 4, 21, "Gazetted Holiday"),
                new FestivalEntry("Buddha Purnima", 5, 23, "Gazetted Holiday"),
                new FestivalEntry("Bakrid / Eid-ul-Adha", 6, 17, "Gazetted Holiday"),
                new FestivalEntry("Muharram", 7, 17, "Gazetted Holiday"),
                new FestivalEntry("Raksha Bandhan", 8, 19, "Cultural Festival / Restricted"),
                new FestivalEntry("Krishna Janmashtami", 8, 26, "Gazetted / Cultural Festival"),
                new FestivalEntry("Ganesh Chaturthi", 9, 7, "Gazetted / Vinayaka Chavithi"),
                new FestivalEntry("Dussehra / Vijaya Dashami", 10, 12, "Gazetted Holiday"),
                new FestivalEntry("Karwa Chauth", 10, 20, "Cultural Observance / Restricted"),
                new FestivalEntry("Diwali / Deepavali", 10, 31, "Gazetted Holiday / Festival of Lights"),
                new FestivalEntry("Govardhan Puja", 11, 2, "Cultural Festival / Restricted"),
                new FestivalEntry("Bhai Dooj", 11, 3, "Cultural Festival / Restricted"),
                new FestivalEntry("Chhath Puja", 11, 7, "Cultural Festival / Restricted"),
                new FestivalEntry("Guru Nanak Jayanti", 11, 15, "Gazetted Holiday")
        ));

        // 2025
        registerFestivals(2025, Arrays.asList(
                new FestivalEntry("Maha Shivratri", 2, 26, "Gazetted / Cultural Festival"),
                new FestivalEntry("Holi", 3, 14, "Gazetted Holiday / Festival of Colors"),
                new FestivalEntry("Eid-ul-Fitr", 3, 31, "Gazetted Holiday / Ramzan Eid"),
                new FestivalEntry("Mahavir Jayanti", 4, 10, "Gazetted Holiday"),
                new FestivalEntry("Good Friday", 4, 18, "Gazetted Holiday"),
                new FestivalEntry("Buddha Purnima", 5, 12, "Gazetted Holiday"),
                new FestivalEntry("Bakrid / Eid-ul-Adha", 6, 7, "Gazetted Holiday"),
                new FestivalEntry("Muharram", 7, 6, "Gazetted Holiday"),
                new FestivalEntry("Raksha Bandhan", 8, 9, "Cultural Festival / Restricted"),
                new FestivalEntry("Krishna Janmashtami", 8, 16, "Gazetted / Cultural Festival"),
                new FestivalEntry("Ganesh Chaturthi", 8, 27, "Gazetted / Vinayaka Chavithi"),
                new FestivalEntry("Milad-un-Nabi", 9, 5, "Gazetted Holiday"),
                new FestivalEntry("Dussehra / Vijaya Dashami", 10, 2, "Gazetted Holiday"),
                new FestivalEntry("Karwa Chauth", 10, 10, "Cultural Observance / Restricted"),
                new FestivalEntry("Diwali / Deepavali", 10, 20, "Gazetted Holiday / Festival of Lights"),
                new FestivalEntry("Govardhan Puja", 10, 21, "Cultural Festival / Restricted"),
                new FestivalEntry("Bhai Dooj", 10, 22, "Cultural Festival / Restricted"),
                new FestivalEntry("Chhath Puja", 10, 28, "Cultural Festival / Restricted"),
                new FestivalEntry("Guru Nanak Jayanti", 11, 5, "Gazetted Holiday")
        ));

        // 2026
        registerFestivals(2026, Arrays.asList(
                new FestivalEntry("Maha Shivratri", 2, 15, "Gazetted / Cultural Festival"),
                new FestivalEntry("Holi", 3, 4, "Gazetted Holiday / Festival of Colors"),
                new FestivalEntry("Eid-ul-Fitr", 3, 20, "Gazetted Holiday / Ramzan Eid"),
                new FestivalEntry("Mahavir Jayanti", 3, 31, "Gazetted Holiday"),
                new FestivalEntry("Good Friday", 4, 3, "Gazetted Holiday"),
                new FestivalEntry("Bakrid / Eid-ul-Adha", 5, 27, "Gazetted Holiday"),
                new FestivalEntry("Buddha Purnima", 5, 31, "Gazetted Holiday"),
                new FestivalEntry("Muharram", 6, 26, "Gazetted Holiday"),
                new FestivalEntry("Milad-un-Nabi", 8, 26, "Gazetted Holiday"),
                new FestivalEntry("Raksha Bandhan", 8, 28, "Cultural Festival / Restricted"),
                new FestivalEntry("Krishna Janmashtami", 9, 4, "Gazetted / Cultural Festival"),
                new FestivalEntry("Ganesh Chaturthi", 9, 14, "Gazetted / Vinayaka Chavithi"),
                new FestivalEntry("Dussehra / Vijaya Dashami", 10, 20, "Gazetted Holiday"),
                new FestivalEntry("Karwa Chauth", 10, 29, "Cultural Observance / Restricted"),
                new FestivalEntry("Diwali / Deepavali", 11, 8, "Gazetted Holiday / Festival of Lights"),
                new FestivalEntry("Govardhan Puja", 11, 9, "Cultural Festival / Restricted"),
                new FestivalEntry("Bhai Dooj", 11, 10, "Cultural Festival / Restricted"),
                new FestivalEntry("Chhath Puja", 11, 15, "Cultural Festival / Restricted"),
                new FestivalEntry("Guru Nanak Jayanti", 11, 24, "Gazetted Holiday")
        ));

        // 2027
        registerFestivals(2027, Arrays.asList(
                new FestivalEntry("Maha Shivratri", 3, 6, "Gazetted / Cultural Festival"),
                new FestivalEntry("Eid-ul-Fitr", 3, 10, "Gazetted Holiday / Ramzan Eid"),
                new FestivalEntry("Holi", 3, 22, "Gazetted Holiday / Festival of Colors"),
                new FestivalEntry("Good Friday", 3, 26, "Gazetted Holiday"),
                new FestivalEntry("Mahavir Jayanti", 4, 19, "Gazetted Holiday"),
                new FestivalEntry("Bakrid / Eid-ul-Adha", 5, 17, "Gazetted Holiday"),
                new FestivalEntry("Buddha Purnima", 5, 20, "Gazetted Holiday"),
                new FestivalEntry("Muharram", 6, 16, "Gazetted Holiday"),
                new FestivalEntry("Raksha Bandhan", 8, 17, "Cultural Festival / Restricted"),
                new FestivalEntry("Krishna Janmashtami", 8, 25, "Gazetted / Cultural Festival"),
                new FestivalEntry("Ganesh Chaturthi", 9, 4, "Gazetted / Vinayaka Chavithi"),
                new FestivalEntry("Dussehra / Vijaya Dashami", 10, 9, "Gazetted Holiday"),
                new FestivalEntry("Karwa Chauth", 10, 19, "Cultural Observance / Restricted"),
                new FestivalEntry("Diwali / Deepavali", 10, 29, "Gazetted Holiday / Festival of Lights"),
                new FestivalEntry("Govardhan Puja", 10, 30, "Cultural Festival / Restricted"),
                new FestivalEntry("Bhai Dooj", 10, 31, "Cultural Festival / Restricted"),
                new FestivalEntry("Chhath Puja", 11, 5, "Cultural Festival / Restricted"),
                new FestivalEntry("Guru Nanak Jayanti", 11, 14, "Gazetted Holiday")
        ));
    }

    private static void registerFestivals(int year, List<FestivalEntry> entries) {
        FESTIVALS_BY_YEAR.put(year, entries);
    }

    /**
     * Generates all Indian national, gazetted, and cultural holidays for the given year.
     */
    public static List<CalendarEvent> getHolidaysForYear(int year) {
        List<CalendarEvent> holidays = new ArrayList<>();

        // 1. National Gazetted Holidays (Mandatory public holidays across all States and Union Territories)
        holidays.add(new CalendarEvent("Republic Day", LocalDate.of(year, Month.JANUARY, 26), EventCategory.HOLIDAY, "National Gazetted Holiday", true));
        holidays.add(new CalendarEvent("Independence Day", LocalDate.of(year, Month.AUGUST, 15), EventCategory.HOLIDAY, "National Gazetted Holiday", true));
        holidays.add(new CalendarEvent("Mahatma Gandhi Jayanti", LocalDate.of(year, Month.OCTOBER, 2), EventCategory.HOLIDAY, "National Gazetted Holiday", true));

        // 2. Fixed Indian Annual Holidays & Observances
        holidays.add(new CalendarEvent("New Year's Day", LocalDate.of(year, Month.JANUARY, 1), EventCategory.HOLIDAY, "Observance", true));
        holidays.add(new CalendarEvent("Makar Sankranti / Pongal", LocalDate.of(year, Month.JANUARY, 14), EventCategory.HOLIDAY, "Harvest Festival / Gazetted", true));
        holidays.add(new CalendarEvent("Netaji Subhas Chandra Bose Jayanti", LocalDate.of(year, Month.JANUARY, 23), EventCategory.HOLIDAY, "Parakram Diwas", true));
        holidays.add(new CalendarEvent("Dr. B.R. Ambedkar Jayanti", LocalDate.of(year, Month.APRIL, 14), EventCategory.HOLIDAY, "Gazetted Holiday", true));
        holidays.add(new CalendarEvent("May Day / Maharashtra Day", LocalDate.of(year, Month.MAY, 1), EventCategory.HOLIDAY, "Labour Day", true));
        holidays.add(new CalendarEvent("Rashtriya Ekta Diwas", LocalDate.of(year, Month.OCTOBER, 31), EventCategory.HOLIDAY, "Sardar Patel Jayanti", true));
        holidays.add(new CalendarEvent("Children's Day", LocalDate.of(year, Month.NOVEMBER, 14), EventCategory.HOLIDAY, "Jawaharlal Nehru Jayanti", true));
        holidays.add(new CalendarEvent("Christmas Day", LocalDate.of(year, Month.DECEMBER, 25), EventCategory.HOLIDAY, "Gazetted Holiday", true));

        // 3. Dynamic Lunar and Solar Indian Festivals
        List<FestivalEntry> entries = FESTIVALS_BY_YEAR.get(year);
        if (entries != null) {
            for (FestivalEntry entry : entries) {
                holidays.add(new CalendarEvent(entry.title, LocalDate.of(year, entry.month, entry.day), EventCategory.HOLIDAY, entry.description));
            }
        } else {
            // Intelligent fallback for years outside preloaded table
            holidays.addAll(getApproximatedFestivals(year));
        }

        Collections.sort(holidays);
        return holidays;
    }

    /**
     * Filters holidays for a specific month.
     */
    public static List<CalendarEvent> getHolidaysForMonth(YearMonth yearMonth) {
        List<CalendarEvent> yearHolidays = getHolidaysForYear(yearMonth.getYear());
        List<CalendarEvent> monthHolidays = new ArrayList<>();
        for (CalendarEvent event : yearHolidays) {
            if (event.getDate().getMonth() == yearMonth.getMonth()) {
                monthHolidays.add(event);
            }
        }
        return monthHolidays;
    }

    /**
     * Approximate dates for major festivals for years outside 2024-2027 range.
     */
    private static List<CalendarEvent> getApproximatedFestivals(int year) {
        List<CalendarEvent> list = new ArrayList<>();
        list.add(new CalendarEvent("Maha Shivratri", LocalDate.of(year, Month.FEBRUARY, 28), EventCategory.HOLIDAY, "Gazetted / Cultural Festival"));
        list.add(new CalendarEvent("Holi", LocalDate.of(year, Month.MARCH, 15), EventCategory.HOLIDAY, "Gazetted Holiday / Festival of Colors"));
        list.add(new CalendarEvent("Good Friday", LocalDate.of(year, Month.APRIL, 10), EventCategory.HOLIDAY, "Gazetted Holiday"));
        list.add(new CalendarEvent("Mahavir Jayanti", LocalDate.of(year, Month.APRIL, 15), EventCategory.HOLIDAY, "Gazetted Holiday"));
        list.add(new CalendarEvent("Buddha Purnima", LocalDate.of(year, Month.MAY, 15), EventCategory.HOLIDAY, "Gazetted Holiday"));
        list.add(new CalendarEvent("Janmashtami", LocalDate.of(year, Month.AUGUST, 20), EventCategory.HOLIDAY, "Gazetted / Cultural Festival"));
        list.add(new CalendarEvent("Ganesh Chaturthi", LocalDate.of(year, Month.SEPTEMBER, 5), EventCategory.HOLIDAY, "Gazetted / Vinayaka Chavithi"));
        list.add(new CalendarEvent("Dussehra / Vijaya Dashami", LocalDate.of(year, Month.OCTOBER, 15), EventCategory.HOLIDAY, "Gazetted Holiday"));
        list.add(new CalendarEvent("Diwali / Deepavali", LocalDate.of(year, Month.NOVEMBER, 1), EventCategory.HOLIDAY, "Gazetted Holiday / Festival of Lights"));
        list.add(new CalendarEvent("Guru Nanak Jayanti", LocalDate.of(year, Month.NOVEMBER, 20), EventCategory.HOLIDAY, "Gazetted Holiday"));
        return list;
    }

    private static class FestivalEntry {
        final String title;
        final int month;
        final int day;
        final String description;

        FestivalEntry(String title, int month, int day, String description) {
            this.title = title;
            this.month = month;
            this.day = day;
            this.description = description;
        }
    }
}
