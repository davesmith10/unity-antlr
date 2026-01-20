/**
 * Unity Markup Language Grammar
 *
 * Unity is a markup language implemented in standard JSON that provides
 * essential components from the XML Infoset using JSON Arrays as the
 * primary structural element.
 *
 * Grammar enforces syntax; semantic validation (XML Name rules, attribute
 * position constraints) is handled by UnityValidationListener.
 */

grammar Unity;

@header {
package com.metamadbooks.unity.parser;
}

// =============================================================================
// Parser Rules
// =============================================================================

/**
 * Entry point: A Unity document must be a single Unity element
 */
unity
    : unityElement EOF
    ;

/**
 * A Unity element is a JSON array with:
 * - Index 0: element name (required)
 * - Index 1+: optional content (attributes object, child elements, primitives)
 */
unityElement
    : '[' elementName (',' elementContent)* ']'
    ;

/**
 * Element name must be a string (validated as XML Name by listener)
 */
elementName
    : STRING
    ;

/**
 * Content can be: attributes object, nested element, or primitive value
 * Position validation (attributes only at index 1) done by listener
 */
elementContent
    : attributesObject
    | unityElement
    | primitiveValue
    ;

/**
 * Attributes object: JSON object with string keys and primitive values
 * No nested objects or arrays allowed (enforced by grammar)
 */
attributesObject
    : '{' '}'
    | '{' attributePair (',' attributePair)* '}'
    ;

/**
 * Attribute pair: string key (validated as XML Name) with primitive value
 */
attributePair
    : STRING ':' attributeValue
    ;

/**
 * Attribute values must be primitives (no nested objects or arrays)
 */
attributeValue
    : STRING
    | NUMBER
    | 'true'
    | 'false'
    | 'null'
    ;

/**
 * Primitive values in element content
 */
primitiveValue
    : STRING
    | NUMBER
    | 'true'
    | 'false'
    | 'null'
    ;

// =============================================================================
// Lexer Rules (based on JSON.g4)
// =============================================================================

STRING
    : '"' (ESC | SAFECODEPOINT)* '"'
    ;

fragment ESC
    : '\\' (["\\/bfnrt] | UNICODE)
    ;

fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;

fragment HEX
    : [0-9a-fA-F]
    ;

fragment SAFECODEPOINT
    : ~["\\\u0000-\u001F]
    ;

NUMBER
    : '-'? INT ('.' [0-9]+)? EXP?
    ;

fragment INT
    : '0'
    | [1-9] [0-9]*
    ;

fragment EXP
    : [Ee] [+-]? [0-9]+
    ;

WS
    : [ \t\n\r]+ -> skip
    ;
