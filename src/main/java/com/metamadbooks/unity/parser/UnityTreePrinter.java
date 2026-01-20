package com.metamadbooks.unity.parser;

import org.antlr.v4.runtime.Token;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ANTLR listener that walks the parse tree and produces formatted output.
 * <p>
 * Supports three output formats:
 * <ul>
 *   <li>{@link PrintFormat#LOG} - Diagnostic log-style with ENTER/EXIT events</li>
 *   <li>{@link PrintFormat#TREE} - Indented hierarchical tree</li>
 *   <li>{@link PrintFormat#LISP} - S-expression style</li>
 * </ul>
 */
public class UnityTreePrinter extends UnityBaseListener {

    private final PrintFormat format;
    private final StringBuilder output = new StringBuilder();
    private final Deque<String> elementPath = new ArrayDeque<>();
    private int depth = 0;
    private boolean inAttributes = false;
    private boolean firstAttribute = true;
    private String currentElementName = null;

    /**
     * Creates a new printer with the specified output format.
     *
     * @param format the output format to use
     */
    public UnityTreePrinter(PrintFormat format) {
        this.format = format;
    }

    /**
     * Returns the accumulated output.
     *
     * @return the formatted output string
     */
    public String getOutput() {
        return output.toString();
    }

    @Override
    public void enterUnityElement(UnityParser.UnityElementContext ctx) {
        currentElementName = null;
    }

    @Override
    public void exitElementName(UnityParser.ElementNameContext ctx) {
        String name = unquoteString(ctx.STRING().getText());
        currentElementName = name;
        elementPath.push(name);

        switch (format) {
            case LOG:
                appendIndent();
                output.append("ENTER element: \"").append(name).append("\" at ").append(getCurrentPath());
                output.append("\n");
                break;
            case TREE:
                appendIndent();
                output.append("Element(").append(name).append(")\n");
                break;
            case LISP:
                if (depth > 0) {
                    output.append("\n");
                    appendIndent();
                }
                output.append("(element \"").append(name).append("\"");
                break;
        }
        depth++;
    }

    @Override
    public void exitUnityElement(UnityParser.UnityElementContext ctx) {
        depth--;

        switch (format) {
            case LOG:
                appendIndent();
                String name = elementPath.isEmpty() ? "?" : elementPath.peek();
                output.append("EXIT element: \"").append(name).append("\"\n");
                break;
            case TREE:
                // Nothing to output on exit for TREE format
                break;
            case LISP:
                output.append(")");
                if (depth == 0) {
                    output.append("\n");
                }
                break;
        }

        if (!elementPath.isEmpty()) {
            elementPath.pop();
        }
    }

    @Override
    public void enterAttributesObject(UnityParser.AttributesObjectContext ctx) {
        inAttributes = true;
        firstAttribute = true;

        switch (format) {
            case LOG:
                // Attributes are printed individually
                break;
            case TREE:
                // Attributes are printed individually
                break;
            case LISP:
                output.append("\n");
                appendIndent();
                output.append("(attrs");
                break;
        }
    }

    @Override
    public void exitAttributesObject(UnityParser.AttributesObjectContext ctx) {
        inAttributes = false;

        if (format == PrintFormat.LISP) {
            output.append(")");
        }
    }

    @Override
    public void exitAttributePair(UnityParser.AttributePairContext ctx) {
        String attrName = unquoteString(ctx.STRING().getText());
        String attrValue = formatAttributeValue(ctx.attributeValue());

        switch (format) {
            case LOG:
                appendIndent();
                output.append("ATTRIBUTE: ").append(attrName).append(" = ").append(attrValue).append("\n");
                break;
            case TREE:
                appendIndent();
                output.append("Attr(").append(attrName).append("=").append(attrValue).append(")\n");
                break;
            case LISP:
                output.append(" (").append(attrName).append(" ").append(attrValue).append(")");
                break;
        }
    }

    @Override
    public void exitPrimitiveValue(UnityParser.PrimitiveValueContext ctx) {
        String value = formatPrimitiveValue(ctx);

        switch (format) {
            case LOG:
                appendIndent();
                output.append("CONTENT: ").append(value).append("\n");
                break;
            case TREE:
                appendIndent();
                output.append("Content(").append(value).append(")\n");
                break;
            case LISP:
                output.append("\n");
                appendIndent();
                output.append("(content ").append(value).append(")");
                break;
        }
    }

    /**
     * Appends indentation based on current depth.
     */
    private void appendIndent() {
        for (int i = 0; i < depth; i++) {
            output.append("  ");
        }
    }

    /**
     * Formats an attribute value for output.
     */
    private String formatAttributeValue(UnityParser.AttributeValueContext ctx) {
        if (ctx.STRING() != null) {
            return "\"" + unquoteString(ctx.STRING().getText()) + "\"";
        } else if (ctx.NUMBER() != null) {
            return ctx.NUMBER().getText();
        } else {
            // true, false, or null
            return ctx.getText();
        }
    }

    /**
     * Formats a primitive value for output.
     */
    private String formatPrimitiveValue(UnityParser.PrimitiveValueContext ctx) {
        if (ctx.STRING() != null) {
            return "\"" + unquoteString(ctx.STRING().getText()) + "\"";
        } else if (ctx.NUMBER() != null) {
            return ctx.NUMBER().getText();
        } else {
            // true, false, or null
            return ctx.getText();
        }
    }

    /**
     * Returns the current element path as a string.
     */
    private String getCurrentPath() {
        if (elementPath.isEmpty()) {
            return "/";
        }
        StringBuilder sb = new StringBuilder();
        Object[] pathArray = elementPath.toArray();
        for (int i = pathArray.length - 1; i >= 0; i--) {
            sb.append("/").append(pathArray[i]);
        }
        return sb.toString();
    }

    /**
     * Removes surrounding quotes from a JSON string token.
     */
    private String unquoteString(String quoted) {
        if (quoted == null || quoted.length() < 2) {
            return quoted;
        }
        String unquoted = quoted.substring(1, quoted.length() - 1);
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
}
