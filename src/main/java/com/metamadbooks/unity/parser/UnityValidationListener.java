package com.metamadbooks.unity.parser;

import org.antlr.v4.runtime.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * ANTLR listener that performs semantic validation on Unity documents.
 * <p>
 * Validates:
 * <ul>
 *   <li>Element name at index 0 is a valid XML Name</li>
 *   <li>Attributes object (if present) only at index 1</li>
 *   <li>Attribute names are valid XML Names</li>
 *   <li>No objects in content positions (index 2+)</li>
 * </ul>
 */
public class UnityValidationListener extends UnityBaseListener {

    private final List<ValidationError> errors = new ArrayList<>();
    private final Deque<String> elementPath = new ArrayDeque<>();
    private int contentIndex = 0;

    /**
     * Returns the list of validation errors found during parsing.
     *
     * @return list of validation errors (empty if document is valid)
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Returns true if no validation errors were found.
     *
     * @return true if document is valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    @Override
    public void enterUnityElement(UnityParser.UnityElementContext ctx) {
        // Reset content index for this element
        contentIndex = 0;
    }

    @Override
    public void exitUnityElement(UnityParser.UnityElementContext ctx) {
        // Pop element from path when exiting
        if (!elementPath.isEmpty()) {
            elementPath.pop();
        }
    }

    @Override
    public void exitElementName(UnityParser.ElementNameContext ctx) {
        Token token = ctx.STRING().getSymbol();
        String rawName = token.getText();
        String name = unquoteString(rawName);

        // Validate XML Name
        if (!XmlNameValidator.isValidName(name)) {
            errors.add(new ValidationError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Invalid element name '" + name + "': must be a valid XML Name",
                    getCurrentPath()
            ));
        }

        // Push element name onto path
        elementPath.push(name);
    }

    @Override
    public void enterElementContent(UnityParser.ElementContentContext ctx) {
        // Increment content index (starts at 1 for first content item after element name)
        contentIndex++;
    }

    @Override
    public void exitElementContent(UnityParser.ElementContentContext ctx) {
        // Check if this content is an attributes object at wrong position
        if (ctx.attributesObject() != null && contentIndex > 1) {
            Token token = ctx.getStart();
            errors.add(new ValidationError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Attributes object must be at index 1 (found at index " + contentIndex + ")",
                    getCurrentPath()
            ));
        }
    }

    @Override
    public void exitAttributePair(UnityParser.AttributePairContext ctx) {
        Token token = ctx.STRING().getSymbol();
        String rawName = token.getText();
        String name = unquoteString(rawName);

        // Validate attribute name is a valid XML Name
        if (!XmlNameValidator.isValidName(name)) {
            errors.add(new ValidationError(
                    token.getLine(),
                    token.getCharPositionInLine(),
                    "Invalid attribute name '" + name + "': must be a valid XML Name",
                    getCurrentPath()
            ));
        }
    }

    /**
     * Removes surrounding quotes from a JSON string token.
     */
    private String unquoteString(String quoted) {
        if (quoted == null || quoted.length() < 2) {
            return quoted;
        }
        // Remove surrounding quotes
        String unquoted = quoted.substring(1, quoted.length() - 1);
        // Handle escape sequences
        return unescapeString(unquoted);
    }

    /**
     * Processes JSON escape sequences in a string.
     */
    private String unescapeString(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(next);
                        i += 2;
                        break;
                    case 'b':
                        sb.append('\b');
                        i += 2;
                        break;
                    case 'f':
                        sb.append('\f');
                        i += 2;
                        break;
                    case 'n':
                        sb.append('\n');
                        i += 2;
                        break;
                    case 'r':
                        sb.append('\r');
                        i += 2;
                        break;
                    case 't':
                        sb.append('\t');
                        i += 2;
                        break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            String hex = s.substring(i + 2, i + 6);
                            try {
                                int codePoint = Integer.parseInt(hex, 16);
                                sb.append((char) codePoint);
                                i += 6;
                            } catch (NumberFormatException e) {
                                sb.append(c);
                                i++;
                            }
                        } else {
                            sb.append(c);
                            i++;
                        }
                        break;
                    default:
                        sb.append(c);
                        i++;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * Returns the current element path as a string.
     */
    private String getCurrentPath() {
        if (elementPath.isEmpty()) {
            return "/";
        }
        StringBuilder sb = new StringBuilder();
        // Convert deque to array and iterate in reverse (bottom to top)
        Object[] pathArray = elementPath.toArray();
        for (int i = pathArray.length - 1; i >= 0; i--) {
            sb.append("/").append(pathArray[i]);
        }
        return sb.toString();
    }
}
