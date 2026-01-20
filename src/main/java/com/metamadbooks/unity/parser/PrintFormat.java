package com.metamadbooks.unity.parser;

/**
 * Output formats for the UnityTreePrinter.
 */
public enum PrintFormat {
    /**
     * Diagnostic log-style output showing ENTER/EXIT events.
     */
    LOG,

    /**
     * Indented hierarchical tree format.
     */
    TREE,

    /**
     * S-expression (Lisp-like) format.
     */
    LISP
}
