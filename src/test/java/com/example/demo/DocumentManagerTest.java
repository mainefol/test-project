package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mykyta Liashko
 */
class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();

        // Prepopulate the storage with test data
        documentManager.save(DocumentManager.Document.builder()
                .id("doc1")
                .title("Java Programming Guide")
                .content("Content about Java basics")
                .author(DocumentManager.Author.builder().id("author1").name("Alice").build())
                .created(Instant.parse("2024-01-01T00:00:00Z"))
                .build());

        documentManager.save(DocumentManager.Document.builder()
                .id("doc2")
                .title("Python Guide")
                .content("Content about Python programming")
                .author(DocumentManager.Author.builder().id("author2").name("Bob").build())
                .created(Instant.parse("2024-02-01T00:00:00Z"))
                .build());

        documentManager.save(DocumentManager.Document.builder()
                .id("doc3")
                .title("Advanced Java Concepts")
                .content("Content about advanced Java topics")
                .author(DocumentManager.Author.builder().id("author1").name("Alice").build())
                .created(Instant.parse("2024-03-01T00:00:00Z"))
                .build());
    }

    // Tests for save
    @Test
    public void save_NewDocumentWithValidData_DocumentSavedSuccessfully() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("author1").name("Alice").build())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("Test Title", savedDocument.getTitle());
        assertEquals("Test Content", savedDocument.getContent());
    }

    @Test
    public void save_ExistingDocumentWithId_DocumentUpdatedSuccessfully() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("doc1")
                .title("Java Programming Guide")
                .content("Content about Java basics")
                .author(DocumentManager.Author.builder().id("author1").name("Alice").build())
                .created(Instant.parse("2024-11-11T00:00:00Z"))
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertEquals("doc1", savedDocument.getId());
        assertEquals(Instant.parse("2024-11-11T00:00:00Z"), savedDocument.getCreated());
        assertEquals("Java Programming Guide", savedDocument.getTitle());
    }

    // Tests for findById
    @Test
    public void findById_ExistingId_DocumentReturned() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("doc1");

        assertTrue(foundDocument.isPresent());
        assertEquals("Java Programming Guide", foundDocument.get().getTitle());
    }

    @Test
    public void findById_NonExistingId_EmptyOptionalReturned() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("nonexistent-id");

        assertFalse(foundDocument.isPresent());
    }

    // Tests for search
    @ParameterizedTest
    @MethodSource("provideSearchRequests")
    public void search_WithDifferentConditions_ReturnsExpectedCount(DocumentManager.SearchRequest request, int expectedCount) {
        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(expectedCount, results.size());
    }

    private static Stream<Arguments> provideSearchRequests() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        DocumentManager.SearchRequest.builder()
                                .titlePrefixes(List.of("Java"))
                                .build(),
                        1 // Expected count of documents
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        DocumentManager.SearchRequest.builder()
                                .containsContents(List.of("Python"))
                                .build(),
                        1 // Expected count of documents
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        DocumentManager.SearchRequest.builder()
                                .authorIds(List.of("author1"))
                                .build(),
                        2 // Expected count of documents
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        DocumentManager.SearchRequest.builder()
                                .createdFrom(Instant.parse("2024-01-01T00:00:00Z"))
                                .createdTo(Instant.parse("2024-12-31T23:59:59Z"))
                                .build(),
                        2 // Expected count of documents
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        DocumentManager.SearchRequest.builder()
                                .titlePrefixes(List.of("Advanced"))
                                .containsContents(List.of("Java"))
                                .authorIds(List.of("author1"))
                                .build(),
                        1 // Expected count of documents
                )
        );
    }

}