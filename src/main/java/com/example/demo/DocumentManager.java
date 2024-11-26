package com.example.demo;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }
        if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> matchesTitlePrefixes(doc, request.getTitlePrefixes()))
                .filter(doc -> matchesContainsContents(doc, request.getContainsContents()))
                .filter(doc -> matchesAuthorIds(doc, request.getAuthorIds()))
                .filter(doc -> matchesCreatedFrom(doc, request.getCreatedFrom()))
                .filter(doc -> matchesCreatedTo(doc, request.getCreatedTo()))
                .toList();
    }

    // Method for checking titlePrefixes
    private boolean matchesTitlePrefixes(Document doc, List<String> titlePrefixes) {
        if (titlePrefixes == null) return true;
        if (doc.getTitle() == null) return false;
        return titlePrefixes.stream().anyMatch(doc.getTitle()::startsWith);
    }

    // Method for checking containsContents
    private boolean matchesContainsContents(Document doc, List<String> containsContents) {
        if (containsContents == null) return true;
        if (doc.getContent() == null) return false;
        return containsContents.stream().anyMatch(doc.getContent()::contains);
    }

    // Method for checking authorIds
    private boolean matchesAuthorIds(Document doc, List<String> authorIds) {
        if (authorIds == null) return true;
        if (doc.getAuthor() == null) return false;
        return authorIds.contains(doc.getAuthor().getId());
    }

    // Method for checking createdFrom
    private boolean matchesCreatedFrom(Document doc, Instant createdFrom) {
        if (createdFrom == null) return true;
        return doc.getCreated().isAfter(createdFrom);
    }

    // Method for checking createdTo
    private boolean matchesCreatedTo(Document doc, Instant createdTo) {
        if (createdTo == null) return true;
        return doc.getCreated().isBefore(createdTo);
    }


    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}