package com.contactbook.storage;

import com.contactbook.model.Contact;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles JSON persistence of contacts to {@code ~/.contactbook/contacts.json}.
 */
public class ContactStorage {

    private static final String DIR_NAME  = ".contactbook";
    private static final String FILE_NAME = "contacts.json";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path storageFile;
    private final Gson gson;

    public ContactStorage() {
        Path home = Paths.get(System.getProperty("user.home"));
        Path dir  = home.resolve(DIR_NAME);
        storageFile = dir.resolve(FILE_NAME);

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    /**
     * Loads contacts from disk. Returns an empty list if the file does not exist
     * or cannot be parsed.
     */
    public List<Contact> loadContacts() {
        if (!Files.exists(storageFile)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(storageFile, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Contact>>() {}.getType();
            List<Contact> contacts = gson.fromJson(json, listType);
            return contacts != null ? contacts : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            System.err.println("[ContactStorage] Failed to load contacts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the given list of contacts to disk, creating parent directories as needed.
     */
    public void saveContacts(List<Contact> contacts) {
        try {
            Files.createDirectories(storageFile.getParent());
            String json = gson.toJson(contacts);
            Files.writeString(storageFile, json, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("[ContactStorage] Failed to save contacts: " + e.getMessage());
        }
    }

    // ── LocalDate adapter ────────────────────────────────────────────────────

    private static final class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATE_FMT));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return LocalDate.parse(json.getAsString(), DATE_FMT);
            } catch (Exception e) {
                throw new JsonParseException("Invalid date: " + json.getAsString(), e);
            }
        }
    }
}
