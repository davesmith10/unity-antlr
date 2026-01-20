package com.metamadbooks.unity.parser;

/**
 * Represents a validation error found during Unity document parsing.
 */
public class ValidationError {

    private final int line;
    private final int column;
    private final String message;
    private final String path;

    /**
     * Creates a new validation error.
     *
     * @param line    the line number where the error occurred (1-based)
     * @param column  the column number where the error occurred (0-based)
     * @param message a description of the error
     * @param path    the path to the element where the error occurred (e.g., "/root/child")
     */
    public ValidationError(int line, int column, String message, String path) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.path = path;
    }

    /**
     * Creates a new validation error without path information.
     *
     * @param line    the line number where the error occurred (1-based)
     * @param column  the column number where the error occurred (0-based)
     * @param message a description of the error
     */
    public ValidationError(int line, int column, String message) {
        this(line, column, message, null);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("line ").append(line).append(":").append(column);
        if (path != null && !path.isEmpty()) {
            sb.append(" at ").append(path);
        }
        sb.append(" - ").append(message);
        return sb.toString();
    }
}
