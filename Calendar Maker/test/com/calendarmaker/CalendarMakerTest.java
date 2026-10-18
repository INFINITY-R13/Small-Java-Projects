package com.calendarmaker;

import com.calendarmaker.generator.*;
import com.calendarmaker.model.*;
import com.calendarmaker.util.AnsiColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

/**
 * Self-contained unit and integration test runner for Calendar Maker.
 * Validates date mathematics, Indian holiday calculations, and multi-format renderers.
 */
public class CalendarMakerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   RUNNING CALENDAR MAKER TEST SUITE (INDIA)     ");
        System.out.println("=================================================");

        runTest("Leap Year and Month Length", CalendarMakerTest::testLeapYear);
        runTest("Indian National Holidays (Republic Day, Independence Day, Gandhi Jayanti)", CalendarMakerTest::testIndianNationalHolidays);
        runTest("Indian Major Festivals (Diwali, Holi, Dussehra, Eid, etc.)", CalendarMakerTest::testIndianFestivals);
        runTest("Event Management (Add, Query, Remove)", CalendarMakerTest::testEventManagerAddRemove);
        runTest("Annual Recurring Events", CalendarMakerTest::testAnnualRecurring);
        runTest("JSON Persistence (Save & Load)", CalendarMakerTest::testJsonPersistence);
        runTest("Console Calendar Renderer (Indian Holidays)", CalendarMakerTest::testConsoleRenderer);
        runTest("HTML Calendar Renderer (Indian Holidays)", CalendarMakerTest::testHtmlRenderer);
        runTest("Markdown Calendar Renderer (Indian Holidays)", CalendarMakerTest::testMarkdownRenderer);
        runTest("iCalendar (.ics) Renderer (Indian Holidays)", CalendarMakerTest::testIcsRenderer);
        runTest("CSV Calendar Renderer (Indian Holidays)", CalendarMakerTest::testCsvRenderer);
        runTest("Week Start Day Adjustment", CalendarMakerTest::testWeekStartDay);

        System.out.println("\n-------------------------------------------------");
        System.out.printf("Tests Run: %d | Passed: %d | Failed: %d\n", testsRun, testsPassed, testsFailed);
        System.out.println("-------------------------------------------------");

        if (testsFailed > 0) {
            System.err.println("TEST SUITE FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! ✓");
        }
    }

    private static void runTest(String name, TestRunnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println(" [PASS] " + name);
        } catch (Throwable t) {
            testsFailed++;
            System.err.println(" [FAIL] " + name + ": " + t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void testLeapYear() {
        assertTrue(YearMonth.of(2024, 2).lengthOfMonth() == 29, "2024 Feb should have 29 days (leap year)");
        assertTrue(YearMonth.of(2026, 2).lengthOfMonth() == 28, "2026 Feb should have 28 days (common year)");
        assertTrue(YearMonth.of(2000, 2).lengthOfMonth() == 29, "2000 Feb should have 29 days (century leap year)");
        assertTrue(YearMonth.of(1900, 2).lengthOfMonth() == 28, "1900 Feb should have 28 days (non-leap century)");
    }

    private static void testIndianNationalHolidays() {
        List<CalendarEvent> holidays2026 = HolidayProvider.getHolidaysForYear(2026);

        // Republic Day: 26 January
        boolean hasRepublicDay = holidays2026.stream().anyMatch(e ->
                e.getTitle().equals("Republic Day") && e.getDate().equals(LocalDate.of(2026, Month.JANUARY, 26)));
        assertTrue(hasRepublicDay, "Republic Day must be January 26");

        // Independence Day: 15 August
        boolean hasIndependenceDay = holidays2026.stream().anyMatch(e ->
                e.getTitle().equals("Independence Day") && e.getDate().equals(LocalDate.of(2026, Month.AUGUST, 15)));
        assertTrue(hasIndependenceDay, "Independence Day must be August 15");

        // Mahatma Gandhi Jayanti: 02 October
        boolean hasGandhiJayanti = holidays2026.stream().anyMatch(e ->
                e.getTitle().equals("Mahatma Gandhi Jayanti") && e.getDate().equals(LocalDate.of(2026, Month.OCTOBER, 2)));
        assertTrue(hasGandhiJayanti, "Mahatma Gandhi Jayanti must be October 2");
    }

    private static void testIndianFestivals() {
        List<CalendarEvent> holidays2026 = HolidayProvider.getHolidaysForYear(2026);

        // Holi 2026: March 4
        boolean hasHoli = holidays2026.stream().anyMatch(e ->
                e.getTitle().contains("Holi") && e.getDate().equals(LocalDate.of(2026, Month.MARCH, 4)));
        assertTrue(hasHoli, "Holi 2026 should be March 4");

        // Dussehra 2026: October 20
        boolean hasDussehra = holidays2026.stream().anyMatch(e ->
                e.getTitle().contains("Dussehra") && e.getDate().equals(LocalDate.of(2026, Month.OCTOBER, 20)));
        assertTrue(hasDussehra, "Dussehra 2026 should be October 20");

        // Diwali 2026: November 8
        boolean hasDiwali = holidays2026.stream().anyMatch(e ->
                e.getTitle().contains("Diwali") && e.getDate().equals(LocalDate.of(2026, Month.NOVEMBER, 8)));
        assertTrue(hasDiwali, "Diwali 2026 should be November 8");

        // Makar Sankranti: January 14
        boolean hasSankranti = holidays2026.stream().anyMatch(e ->
                e.getTitle().contains("Makar Sankranti") && e.getDate().equals(LocalDate.of(2026, Month.JANUARY, 14)));
        assertTrue(hasSankranti, "Makar Sankranti should be January 14");

        assertTrue(holidays2026.size() >= 20, "Should have 20+ Indian holidays and festivals configured");
    }

    private static void testEventManagerAddRemove() {
        EventManager em = new EventManager();
        em.clearCustomEvents();

        CalendarEvent event = new CalendarEvent("Sprint Review", LocalDate.of(2026, 10, 20), EventCategory.WORK, "Demo features");
        em.addEvent(event);

        List<CalendarEvent> dateEvents = em.getEventsForDate(LocalDate.of(2026, 10, 20));
        assertTrue(dateEvents.stream().anyMatch(e -> e.getTitle().equals("Sprint Review")), "Event should be found on date");

        boolean removed = em.removeEvent(event.getId());
        assertTrue(removed, "Event should be removed by ID");
        assertTrue(em.getCustomEvents().isEmpty(), "Custom events list should be empty after removal");
    }

    private static void testAnnualRecurring() {
        EventManager em = new EventManager();
        em.clearCustomEvents();

        CalendarEvent bday = new CalendarEvent("Raghuraj's Birthday", LocalDate.of(2026, 6, 13), EventCategory.BIRTHDAY, "Raghuraj's Birthday", true);
        em.addEvent(bday);

        List<CalendarEvent> events2026 = em.getEventsForDate(LocalDate.of(2026, 6, 13));
        assertTrue(events2026.stream().anyMatch(e -> e.getTitle().equals("Raghuraj's Birthday")), "Recurring birthday should match in 2026");

        List<CalendarEvent> events2027 = em.getEventsForDate(LocalDate.of(2027, 6, 13));
        assertTrue(events2027.stream().anyMatch(e -> e.getTitle().equals("Raghuraj's Birthday")), "Recurring birthday should match in 2027");

        List<CalendarEvent> eventsWrongDay = em.getEventsForDate(LocalDate.of(2026, 6, 14));
        assertTrue(eventsWrongDay.stream().noneMatch(e -> e.getTitle().equals("Raghuraj's Birthday")), "Should not match different day");
    }

    private static void testJsonPersistence() throws IOException {
        Path tempFile = Files.createTempFile("cal_events_test", ".json");
        try {
            EventManager em = new EventManager();
            em.clearCustomEvents();

            em.addEvent(new CalendarEvent("id-1", "Raghuraj's Birthday", LocalDate.of(2026, 6, 13), EventCategory.BIRTHDAY, "Raghuraj's Birthday", true));
            em.addEvent(new CalendarEvent("id-2", "Diwali Celebration", LocalDate.of(2026, 11, 8), EventCategory.HOLIDAY, "Diwali prayers", true));
            em.saveToFile(tempFile);

            assertTrue(Files.exists(tempFile), "Saved file should exist");
            assertTrue(Files.size(tempFile) > 0, "Saved file should not be empty");

            EventManager loaded = new EventManager();
            loaded.clearCustomEvents();
            loaded.loadFromFile(tempFile);

            assertEquals(2, loaded.getCustomEvents().size(), "Should load 2 events");
            CalendarEvent e1 = loaded.getCustomEvents().stream().filter(e -> e.getId().equals("id-1")).findFirst().orElse(null);
            assertTrue(e1 != null, "Event 1 should be loaded");
            assertEquals("Raghuraj's Birthday", e1.getTitle(), "Title matches");
            assertEquals(EventCategory.BIRTHDAY, e1.getCategory(), "Category matches");
            assertTrue(e1.isRecurringAnnual(), "Recurring flag should be true");

            CalendarEvent e2 = loaded.getCustomEvents().stream().filter(e -> e.getId().equals("id-2")).findFirst().orElse(null);
            assertTrue(e2 != null, "Event 2 should be loaded");
            assertEquals("Diwali Celebration", e2.getTitle(), "Title matches");
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static void testConsoleRenderer() {
        ConsoleCalendarRenderer renderer = new ConsoleCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);
        List<CalendarEvent> holidays = HolidayProvider.getHolidaysForMonth(ym);

        String output = renderer.renderMonth(ym, holidays, DayOfWeek.SUNDAY);
        assertTrue(output.contains("OCTOBER 2026"), "Console output should contain month title");
        assertTrue(output.contains("Sun") && output.contains("Mon"), "Console output should contain day names");
        assertTrue(output.contains("Mahatma Gandhi Jayanti"), "Console output should list Mahatma Gandhi Jayanti");
        assertTrue(output.contains("Dussehra"), "Console output should list Dussehra");
    }

    private static void testHtmlRenderer() {
        HtmlCalendarRenderer renderer = new HtmlCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);
        List<CalendarEvent> holidays = HolidayProvider.getHolidaysForMonth(ym);

        String html = renderer.renderMonth(ym, holidays, DayOfWeek.SUNDAY);
        assertTrue(html.contains("<!DOCTYPE html>"), "HTML should have doctype");
        assertTrue(html.contains("calendar-grid"), "HTML should have calendar grid class");
        assertTrue(html.contains("Mahatma Gandhi Jayanti"), "HTML should include Mahatma Gandhi Jayanti");
        assertTrue(html.contains("print"), "HTML should include print button / media");
    }

    private static void testMarkdownRenderer() {
        MarkdownCalendarRenderer renderer = new MarkdownCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);
        List<CalendarEvent> holidays = HolidayProvider.getHolidaysForMonth(ym);

        String md = renderer.renderMonth(ym, holidays, DayOfWeek.SUNDAY);
        assertTrue(md.contains("# 📅 Calendar: October 2026"), "Markdown should have H1 title");
        assertTrue(md.contains("| Sun | Mon |"), "Markdown should contain table header");
        assertTrue(md.contains("Mahatma Gandhi Jayanti"), "Markdown should list Gandhi Jayanti");
    }

    private static void testIcsRenderer() {
        IcsCalendarRenderer renderer = new IcsCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);
        List<CalendarEvent> holidays = HolidayProvider.getHolidaysForMonth(ym);

        String ics = renderer.renderMonth(ym, holidays, DayOfWeek.SUNDAY);
        assertTrue(ics.contains("BEGIN:VCALENDAR"), "ICS should contain BEGIN:VCALENDAR");
        assertTrue(ics.contains("BEGIN:VEVENT"), "ICS should contain BEGIN:VEVENT");
        assertTrue(ics.contains("SUMMARY:Mahatma Gandhi Jayanti"), "ICS should contain Gandhi Jayanti summary");
        assertTrue(ics.contains("END:VCALENDAR"), "ICS should contain END:VCALENDAR");
    }

    private static void testCsvRenderer() {
        CsvCalendarRenderer renderer = new CsvCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);
        List<CalendarEvent> holidays = HolidayProvider.getHolidaysForMonth(ym);

        String csv = renderer.renderMonth(ym, holidays, DayOfWeek.SUNDAY);
        assertTrue(csv.startsWith("Date,DayOfWeek,Title,Category,AnnualRecurring,Description"), "CSV should have header");
        assertTrue(csv.contains("2026-10-02"), "CSV should contain Gandhi Jayanti date");
        assertTrue(csv.contains("Mahatma Gandhi Jayanti"), "CSV should contain Gandhi Jayanti title");
    }

    private static void testWeekStartDay() {
        ConsoleCalendarRenderer renderer = new ConsoleCalendarRenderer();
        YearMonth ym = YearMonth.of(2026, 10);

        String sunStart = AnsiColor.stripAnsi(renderer.renderMonth(ym, null, DayOfWeek.SUNDAY));
        String monStart = AnsiColor.stripAnsi(renderer.renderMonth(ym, null, DayOfWeek.MONDAY));

        assertTrue(sunStart.contains("Sun"), "Sunday start calendar contains Sun");
        assertTrue(monStart.contains("Mon"), "Monday start calendar contains Mon");
        assertTrue(!sunStart.equals(monStart), "Calendars with different start days should differ in layout");
    }

    @FunctionalInterface
    interface TestRunnable {
        void run() throws Throwable;
    }
}
