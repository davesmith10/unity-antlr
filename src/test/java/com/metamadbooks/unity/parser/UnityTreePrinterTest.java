package com.metamadbooks.unity.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the UnityTreePrinter listener.
 */
class UnityTreePrinterTest {

    // =========================================================================
    // LOG Format Tests
    // =========================================================================

    @Test
    @DisplayName("LOG format: simple element")
    void testLogFormatSimpleElement() {
        Unity.ParseResult result = Unity.parse("[\"x\"]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("ENTER element: \"x\""));
        assertTrue(output.contains("EXIT element: \"x\""));
        assertTrue(output.contains("at /x"));
    }

    @Test
    @DisplayName("LOG format: element with text content")
    void testLogFormatElementWithText() {
        Unity.ParseResult result = Unity.parse("[\"x\", \"hello\"]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("ENTER element: \"x\""));
        assertTrue(output.contains("CONTENT: \"hello\""));
        assertTrue(output.contains("EXIT element: \"x\""));
    }

    @Test
    @DisplayName("LOG format: element with attributes")
    void testLogFormatElementWithAttributes() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"a\": \"1\", \"b\": 42}]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("ENTER element: \"x\""));
        assertTrue(output.contains("ATTRIBUTE: a = \"1\""));
        assertTrue(output.contains("ATTRIBUTE: b = 42"));
        assertTrue(output.contains("EXIT element: \"x\""));
    }

    @Test
    @DisplayName("LOG format: nested elements")
    void testLogFormatNestedElements() {
        Unity.ParseResult result = Unity.parse("[\"x\", [\"y\", [\"z\"]]]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("ENTER element: \"x\" at /x"));
        assertTrue(output.contains("ENTER element: \"y\" at /x/y"));
        assertTrue(output.contains("ENTER element: \"z\" at /x/y/z"));
        assertTrue(output.contains("EXIT element: \"z\""));
        assertTrue(output.contains("EXIT element: \"y\""));
        assertTrue(output.contains("EXIT element: \"x\""));
    }

    @Test
    @DisplayName("LOG format: default print method")
    void testLogFormatDefault() {
        Unity.ParseResult result = Unity.parse("[\"x\"]");
        String output = Unity.print(result);

        assertTrue(output.contains("ENTER element: \"x\""));
    }

    // =========================================================================
    // TREE Format Tests
    // =========================================================================

    @Test
    @DisplayName("TREE format: simple element")
    void testTreeFormatSimpleElement() {
        Unity.ParseResult result = Unity.parse("[\"x\"]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Element(x)"));
    }

    @Test
    @DisplayName("TREE format: element with text content")
    void testTreeFormatElementWithText() {
        Unity.ParseResult result = Unity.parse("[\"x\", \"hello\"]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Element(x)"));
        assertTrue(output.contains("Content(\"hello\")"));
    }

    @Test
    @DisplayName("TREE format: element with attributes")
    void testTreeFormatElementWithAttributes() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"a\": \"1\"}]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Element(x)"));
        assertTrue(output.contains("Attr(a=\"1\")"));
    }

    @Test
    @DisplayName("TREE format: nested elements show indentation")
    void testTreeFormatNestedElements() {
        Unity.ParseResult result = Unity.parse("[\"x\", [\"y\"]]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Element(x)"));
        assertTrue(output.contains("Element(y)"));
        // Verify nesting through indentation
        String[] lines = output.split("\n");
        boolean foundX = false;
        boolean foundIndentedY = false;
        for (String line : lines) {
            if (line.equals("Element(x)")) {
                foundX = true;
            }
            if (line.equals("  Element(y)")) {
                foundIndentedY = true;
            }
        }
        assertTrue(foundX, "Should find Element(x) at root level");
        assertTrue(foundIndentedY, "Should find Element(y) indented");
    }

    // =========================================================================
    // LISP Format Tests
    // =========================================================================

    @Test
    @DisplayName("LISP format: simple element")
    void testLispFormatSimpleElement() {
        Unity.ParseResult result = Unity.parse("[\"x\"]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(element \"x\")"));
    }

    @Test
    @DisplayName("LISP format: element with text content")
    void testLispFormatElementWithText() {
        Unity.ParseResult result = Unity.parse("[\"x\", \"hello\"]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(element \"x\""));
        assertTrue(output.contains("(content \"hello\")"));
    }

    @Test
    @DisplayName("LISP format: element with attributes")
    void testLispFormatElementWithAttributes() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"a\": \"1\"}]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(element \"x\""));
        assertTrue(output.contains("(attrs"));
        assertTrue(output.contains("(a \"1\")"));
    }

    @Test
    @DisplayName("LISP format: nested elements")
    void testLispFormatNestedElements() {
        Unity.ParseResult result = Unity.parse("[\"x\", [\"y\"]]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(element \"x\""));
        assertTrue(output.contains("(element \"y\")"));
    }

    // =========================================================================
    // Mixed Content Tests
    // =========================================================================

    @Test
    @DisplayName("LOG format: mixed content with attributes, text, and nested element")
    void testLogFormatMixedContent() {
        Unity.ParseResult result = Unity.parse("[\"root\", {\"id\": \"123\"}, \"text\", [\"child\"]]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("ENTER element: \"root\""));
        assertTrue(output.contains("ATTRIBUTE: id = \"123\""));
        assertTrue(output.contains("CONTENT: \"text\""));
        assertTrue(output.contains("ENTER element: \"child\""));
        assertTrue(output.contains("EXIT element: \"child\""));
        assertTrue(output.contains("EXIT element: \"root\""));
    }

    @Test
    @DisplayName("TREE format: mixed content")
    void testTreeFormatMixedContent() {
        Unity.ParseResult result = Unity.parse("[\"root\", {\"id\": \"123\"}, \"text\", [\"child\"]]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Element(root)"));
        assertTrue(output.contains("Attr(id=\"123\")"));
        assertTrue(output.contains("Content(\"text\")"));
        assertTrue(output.contains("Element(child)"));
    }

    @Test
    @DisplayName("LISP format: mixed content")
    void testLispFormatMixedContent() {
        Unity.ParseResult result = Unity.parse("[\"root\", {\"id\": \"123\"}, \"text\", [\"child\"]]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(element \"root\""));
        assertTrue(output.contains("(attrs"));
        assertTrue(output.contains("(id \"123\")"));
        assertTrue(output.contains("(content \"text\")"));
        assertTrue(output.contains("(element \"child\")"));
    }

    // =========================================================================
    // Special Values Tests
    // =========================================================================

    @Test
    @DisplayName("LOG format: numeric content")
    void testLogFormatNumericContent() {
        Unity.ParseResult result = Unity.parse("[\"x\", 42]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("CONTENT: 42"));
    }

    @Test
    @DisplayName("LOG format: boolean and null content")
    void testLogFormatBooleanNullContent() {
        Unity.ParseResult result = Unity.parse("[\"x\", true, false, null]");
        String output = Unity.print(result, PrintFormat.LOG);

        assertTrue(output.contains("CONTENT: true"));
        assertTrue(output.contains("CONTENT: false"));
        assertTrue(output.contains("CONTENT: null"));
    }

    @Test
    @DisplayName("TREE format: boolean attribute values")
    void testTreeFormatBooleanAttributes() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"enabled\": true, \"disabled\": false}]");
        String output = Unity.print(result, PrintFormat.TREE);

        assertTrue(output.contains("Attr(enabled=true)"));
        assertTrue(output.contains("Attr(disabled=false)"));
    }

    @Test
    @DisplayName("LISP format: null attribute value")
    void testLispFormatNullAttribute() {
        Unity.ParseResult result = Unity.parse("[\"x\", {\"value\": null}]");
        String output = Unity.print(result, PrintFormat.LISP);

        assertTrue(output.contains("(value null)"));
    }

    // =========================================================================
    // Deep Nesting Tests
    // =========================================================================

    @Test
    @DisplayName("All formats: deeply nested structure")
    void testDeeplyNestedStructure() {
        Unity.ParseResult result = Unity.parse("[\"a\", [\"b\", [\"c\", [\"d\"]]]]");

        String logOutput = Unity.print(result, PrintFormat.LOG);
        assertTrue(logOutput.contains("at /a/b/c/d"));

        String treeOutput = Unity.print(result, PrintFormat.TREE);
        assertTrue(treeOutput.contains("Element(a)"));
        assertTrue(treeOutput.contains("Element(d)"));

        String lispOutput = Unity.print(result, PrintFormat.LISP);
        assertTrue(lispOutput.contains("(element \"a\""));
        assertTrue(lispOutput.contains("(element \"d\")"));
    }
}
