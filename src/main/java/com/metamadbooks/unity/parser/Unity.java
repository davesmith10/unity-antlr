package com.metamadbooks.unity.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade class for parsing and validating Unity documents.
 * <p>
 * Usage:
 * <pre>{@code
 * Unity.ParseResult result = Unity.parse("[\"element\", \"content\"]");
 * if (result.isValid()) {
 *     // Document is valid
 * } else {
 *     for (ValidationError error : result.getErrors()) {
 *         System.out.println(error);
 *     }
 * }
 * }</pre>
 */
public final class Unity {

    private Unity() {
    }

    /**
     * Parses and validates a Unity document.
     *
     * @param input the Unity document as a string
     * @return the parse result containing validation errors (if any)
     */
    public static ParseResult parse(String input) {
        List<ValidationError> errors = new ArrayList<>();

        // Create lexer and parser
        UnityLexer lexer = new UnityLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        UnityParser parser = new UnityParser(tokens);

        // Add error listener to capture syntax errors
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        // Parse the document
        UnityParser.UnityContext tree = parser.unity();

        // Add syntax errors
        errors.addAll(syntaxErrorListener.getErrors());

        // If no syntax errors, perform semantic validation
        if (syntaxErrorListener.getErrors().isEmpty()) {
            UnityValidationListener validationListener = new UnityValidationListener();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(validationListener, tree);
            errors.addAll(validationListener.getErrors());
        }

        return new ParseResult(tree, errors);
    }

    /**
     * Validates a Unity document without returning the parse tree.
     *
     * @param input the Unity document as a string
     * @return true if the document is valid, false otherwise
     */
    public static boolean isValid(String input) {
        return parse(input).isValid();
    }

    /**
     * Result of parsing a Unity document.
     */
    public static class ParseResult {
        private final UnityParser.UnityContext parseTree;
        private final List<ValidationError> errors;

        ParseResult(UnityParser.UnityContext parseTree, List<ValidationError> errors) {
            this.parseTree = parseTree;
            this.errors = errors;
        }

        /**
         * Returns true if the document is valid (no errors).
         */
        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Returns the list of validation errors.
         */
        public List<ValidationError> getErrors() {
            return errors;
        }

        /**
         * Returns the parse tree (may be incomplete if there were syntax errors).
         */
        public UnityParser.UnityContext getParseTree() {
            return parseTree;
        }
    }

    /**
     * Error listener that collects syntax errors during parsing.
     */
    private static class SyntaxErrorListener extends BaseErrorListener {
        private final List<ValidationError> errors = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg,
                                RecognitionException e) {
            errors.add(new ValidationError(line, charPositionInLine, "Syntax error: " + msg));
        }

        public List<ValidationError> getErrors() {
            return errors;
        }
    }
}
