package com.metamadbooks.unity.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Unity ANTLR parser and semantic validator.
 */
class UnityParserTest {

    // =========================================================================
    // Valid Unity Documents
    // =========================================================================

    @Test
    @DisplayName("Self-closing element: [\"x\"]")
    void testSelfClosingElement() {
        assertTrue(Unity.isValid("[\"x\"]"));
    }

    @Test
    @DisplayName("Empty element with tags: [\"x\", \"\"]")
    void testEmptyElementWithTags() {
        assertTrue(Unity.isValid("[\"x\", \"\"]"));
    }

    @Test
    @DisplayName("Element with text content: [\"x\", \"hello\"]")
    void testElementWithTextContent() {
        assertTrue(Unity.isValid("[\"x\", \"hello\"]"));
    }

    @Test
    @DisplayName("Element with attributes: [\"x\", {\"a\": \"b\"}]")
    void testElementWithAttributes() {
        assertTrue(Unity.isValid("[\"x\", {\"a\": \"b\"}]"));
    }

    @Test
    @DisplayName("Element with numeric and boolean attributes")
    void testElementWithMixedAttributes() {
        assertTrue(Unity.isValid("[\"x\", {\"a\": 42, \"b\": true, \"c\": false, \"d\": null}]"));
    }

    @Test
    @DisplayName("Nested element: [\"x\", [\"y\"]]")
    void testNestedElement() {
        assertTrue(Unity.isValid("[\"x\", [\"y\"]]"));
    }

    @Test
    @DisplayName("Element with attributes and content")
    void testElementWithAttributesAndContent() {
        assertTrue(Unity.isValid("[\"x\", {\"id\": \"1\"}, \"text\", [\"child\"]]"));
    }

    @Test
    @DisplayName("Namespaced element: [\"ns:element\"]")
    void testNamespacedElement() {
        assertTrue(Unity.isValid("[\"ns:element\"]"));
    }

    @Test
    @DisplayName("Element with namespace declaration")
    void testElementWithNamespaceDeclaration() {
        assertTrue(Unity.isValid("[\"ns:element\", {\"xmlns:ns\": \"http://example.com\"}]"));
    }

    @Test
    @DisplayName("Multiple nested levels")
    void testMultipleNestedLevels() {
        assertTrue(Unity.isValid("[\"root\", [\"level1\", [\"level2\", [\"level3\"]]]]"));
    }

    @Test
    @DisplayName("Mixed content: text and elements")
    void testMixedContent() {
        assertTrue(Unity.isValid("[\"x\", \"text\", [\"y\"], \"more text\"]"));
    }

    @Test
    @DisplayName("Element with underscore in name")
    void testElementWithUnderscore() {
        assertTrue(Unity.isValid("[\"my_element\"]"));
    }

    @Test
    @DisplayName("Element with hyphen in name")
    void testElementWithHyphen() {
        assertTrue(Unity.isValid("[\"my-element\"]"));
    }

    @Test
    @DisplayName("Element with dot in name")
    void testElementWithDot() {
        assertTrue(Unity.isValid("[\"my.element\"]"));
    }

    @Test
    @DisplayName("Element name starting with colon")
    void testElementNameStartingWithColon() {
        assertTrue(Unity.isValid("[\":element\"]"));
    }

    @Test
    @DisplayName("Empty attributes object")
    void testEmptyAttributesObject() {
        assertTrue(Unity.isValid("[\"x\", {}]"));
    }

    @Test
    @DisplayName("Numeric content")
    void testNumericContent() {
        assertTrue(Unity.isValid("[\"x\", 42]"));
    }

    @Test
    @DisplayName("Boolean content")
    void testBooleanContent() {
        assertTrue(Unity.isValid("[\"x\", true, false]"));
    }

    @Test
    @DisplayName("Null content")
    void testNullContent() {
        assertTrue(Unity.isValid("[\"x\", null]"));
    }

    @Test
    @DisplayName("Float attribute value")
    void testFloatAttributeValue() {
        assertTrue(Unity.isValid("[\"x\", {\"value\": 3.14159}]"));
    }

    @Test
    @DisplayName("Negative number attribute")
    void testNegativeNumberAttribute() {
        assertTrue(Unity.isValid("[\"x\", {\"value\": -42}]"));
    }

    @Test
    @DisplayName("Scientific notation attribute")
    void testScientificNotationAttribute() {
        assertTrue(Unity.isValid("[\"x\", {\"value\": 1.5e10}]"));
    }

    // =========================================================================
    // Invalid Unity Documents - Syntax Errors
    // =========================================================================

    @Test
    @DisplayName("Invalid: Top level must be array (object)")
    void testTopLevelObject() {
        Unity.ParseResult result = Unity.parse("{}");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Syntax error")));
    }

    @Test
    @DisplayName("Invalid: Empty array (no element name)")
    void testEmptyArray() {
        Unity.ParseResult result = Unity.parse("[]");
        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("Invalid: Top level is a string")
    void testTopLevelString() {
        Unity.ParseResult result = Unity.parse("\"hello\"");
        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("Invalid: Top level is a number")
    void testTopLevelNumber() {
        Unity.ParseResult result = Unity.parse("42");
        assertFalse(result.isValid());
    }

    // =========================================================================
    // Invalid Unity Documents - Semantic Errors
    // =========================================================================

    @Test
    @DisplayName("Invalid: Element name starts with digit")
    void testElementNameStartsWithDigit() {
        Unity.ParseResult result = Unity.parse("[\"123invalid\"]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid element name")));
    }

    @Test
    @DisplayName("Invalid: Attribute name starts with digit")
    void testAttributeNameStartsWithDigit() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"123attr\": \"value\"}]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid attribute name")));
    }

    @Test
    @DisplayName("Invalid: Attributes at wrong position (index 2)")
    void testAttributesAtWrongPosition() {
        Unity.ParseResult result = Unity.parse("[\"x\", \"text\", {\"a\": \"b\"}]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Attributes object must be at index 1")));
    }

    @Test
    @DisplayName("Invalid: Element name with space")
    void testElementNameWithSpace() {
        Unity.ParseResult result = Unity.parse("[\"element name\"]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid element name")));
    }

    @Test
    @DisplayName("Invalid: Attribute name with space")
    void testAttributeNameWithSpace() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"attr name\": \"value\"}]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid attribute name")));
    }

    @Test
    @DisplayName("Invalid: Element name starting with hyphen")
    void testElementNameStartingWithHyphen() {
        Unity.ParseResult result = Unity.parse("[\"-element\"]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid element name")));
    }

    @Test
    @DisplayName("Invalid: Element name starting with dot")
    void testElementNameStartingWithDot() {
        Unity.ParseResult result = Unity.parse("[\".element\"]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid element name")));
    }

    @Test
    @DisplayName("Invalid: Empty element name")
    void testEmptyElementName() {
        Unity.ParseResult result = Unity.parse("[\"\"]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Invalid element name")));
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Test
    @DisplayName("Unicode element name")
    void testUnicodeElementName() {
        // Greek letters are valid NameStartChars
        assertTrue(Unity.isValid("[\"\\u03B1\\u03B2\\u03B3\"]")); // alpha, beta, gamma
    }

    @Test
    @DisplayName("Escaped characters in content")
    void testEscapedCharactersInContent() {
        assertTrue(Unity.isValid("[\"x\", \"line1\\nline2\\ttab\"]"));
    }

    @Test
    @DisplayName("Unicode escape in element name")
    void testUnicodeEscapeInElementName() {
        // \u0041 is 'A'
        assertTrue(Unity.isValid("[\"\\u0041\"]"));
    }

    @Test
    @DisplayName("Whitespace handling")
    void testWhitespaceHandling() {
        assertTrue(Unity.isValid("[ \"x\" , { \"a\" : \"b\" } , \"text\" ]"));
    }

    @Test
    @DisplayName("Newlines in document")
    void testNewlinesInDocument() {
        String input = """
                [
                    "root",
                    {"id": "1"},
                    [
                        "child",
                        "text"
                    ]
                ]
                """;
        assertTrue(Unity.isValid(input));
    }

    // =========================================================================
    // Error Reporting
    // =========================================================================

    @Test
    @DisplayName("Error includes line and column")
    void testErrorLineAndColumn() {
        Unity.ParseResult result = Unity.parse("[\"123\"]");
        assertFalse(result.isValid());
        ValidationError error = result.getErrors().get(0);
        assertEquals(1, error.getLine());
        assertTrue(error.getColumn() >= 0);
    }

    @Test
    @DisplayName("Multiple errors reported")
    void testMultipleErrors() {
        // Both element name and attribute name are invalid
        Unity.ParseResult result = Unity.parse("[\"123elem\", {\"456attr\": \"value\"}]");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() >= 2);
    }
}
