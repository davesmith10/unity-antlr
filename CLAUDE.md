# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Unity is a markup language implemented in standard JSON that provides essential components from the XML Infoset. It uses JSON Arrays as the primary structural element to represent heirarchical data.

Unity is not necessarily useful for any particular purpose. The point of the project is to explore the intuition that JSON as concieved by Douglas Crockford is "low level" and "unstructured."
Unstructured is probably hyperbole, but we can imagine sophisticated markup structures being implemented using JSON with an additional set of tighter constraints.

**Repositories:** 

https://github.com/davesmith10/unity           - implement a validator-RI that leverages JSON-java (org.json) with additional rules. 
https://github.com/davesmith10/unity-antlr     - implement a lexer/parser grammar for Unity

https://github.com/antlr/grammars-v4.git       - Source for ANTLR example grammars

## Unity Markup Language Rules

1. **Top level** must be a JSON Array (not a JSON Object)
2. **Element name** (required) is always at index 0 of the array - must conform to XML Infoset element name rules
3. **Attributes** (optional) must be at index 1 as a JSON Object - only valid position, cannot contain nested Arrays/Objects, values must be String, Number, Boolean, or Null
4. **Content** appears at index ≥ 2 (or index ≥ 1 if no attributes) - may contain String, Number, Boolean, Null, or nested Unity productions (JSON Arrays)
5. **Empty elements**: `["element"]` for self-closing, `["element", ""]` for empty with tags

## Unity to XML Mapping Examples

```
["x"]                                    -> <x/>
["x", ""]                                -> <x></x>
["x", "hello world"]                     -> <x>hello world</x>
["x", {"a": "attrib1", "b": 42}]         -> <x a="attrib1" b="42"/>
["x", ["y"]]                             -> <x><y/></x>
["x", "text", ["y"], "more"]             -> <x>text<y/>more</x>
```

## Key Constraints

- Character encoding must be UTF-8 (per JSON spec)
- Whitespace in string content is preserved
- Element order in arrays is significant and preserved
- Attribute order in objects must be guaranteed (required for cryptographic digests)
- As in standard JSON, it is not necessary to quote or double quote attribute values (such as for numbers or booleans or null). Attribute names must meet the same requirements as in XML.
- element and attribute names have similar constraints as described in the infoset docs:

"The first character of a Name MUST be a NameStartChar, and any other characters MUST be NameChars; this mechanism is used to prevent names from beginning with European (ASCII) digits or with basic combining characters. Almost all characters are permitted in names, except those which either are or reasonably could be used as delimiters. The intention is to be inclusive rather than exclusive..."


[4]   	NameStartChar	   ::=   	":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
[4a]   	NameChar	   ::=   	NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
[5]   	Name	   	   ::=   	NameStartChar (NameChar)*
[6]   	Names	   	   ::=   	Name (#x20 Name)*
[7]   	Nmtoken	   	   ::=   	(NameChar)+
[8]   	Nmtokens	   ::=   	Nmtoken (#x20 Nmtoken)*


## Namespace Support

Unity supports namespaces via prefixed element names (e.g., `ns1:element`) with namespace declarations made in the root attribute. For example:

["us:unity", {"Unity"="us", "Dockerfile"="df1"}, [...]]

The concept here is an outer wrapper of metadata where "Unity" and "Dockerfile" are schemas defined elsewhere.

The Unity namespace and schema system will be defined later, but it will contain some standard comment elements. E.g., assuming the core Unity schema token prefix is 'us', then

["us:comment", "I am a standard UTF-8 comment text"]

["us:comment", {"error-code"=23, "recoverable"=false},  "I am a standard UTF-8 comment text with attributes"]

We can use this approach to represent other markup special infoset productions such as prolog, PI, and document type declarations.


## Tools

Development is taking place on a Windows 11 PC inside of WSL, using Ubuntu as the linux OS flavor. 

The RI is built with Java 21, which is available in the current development environment. 
Maven 3 (mvn) is available in the current development environment.
The ANTLR parser generator is available through the command runantlr.sh or through the ANTLR maven plugin, see below. 

## ANTLR Details ##

```
<build>
    <plugins>
        <plugin>
            <groupId>org.antlr</groupId>
            <artifactId>antlr4-maven-plugin</artifactId>
            <version>4.13.2</version> <!-- Use the latest version -->
            <configuration>
                <sourceDirectory>src/main/antlr4</sourceDirectory>
                <!-- Other configurations can go here -->
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>antlr4</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

```
<dependencies>
    <dependency>
        <groupId>org.antlr</groupId>
        <artifactId>antlr4-runtime</artifactId>
        <version>4.13.2</version> <!-- Must match the plugin version -->
    </dependency>
</dependencies>
```

## ANTLR Grammars ##

The grammars of interest are JSON, JSON5, and XML (for the lexer rules). These are canonically located at https://github.com/antlr/grammars-v4.git and are currently present in @repos/grammars-v4

---

## unity-antlr Project Implementation

The unity-antlr repository contains a complete ANTLR-based parser and validator for the Unity markup language.

### Project Structure

```
repos/unity-antlr/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── antlr4/com/metamadbooks/unity/parser/
│   │   │   ├── Unity.g4          (Main Unity grammar)
│   │   │   ├── JSON.g4           (Reference JSON grammar)
│   │   │   ├── JSON5.g4          (Extended JSON grammar)
│   │   │   ├── XMLLexer.g4       (Reference XML lexer)
│   │   │   └── XMLParser.g4      (Reference XML parser)
│   │   └── java/com/metamadbooks/unity/parser/
│   │       ├── Unity.java                  (Facade API)
│   │       ├── UnityValidationListener.java (Semantic validation)
│   │       ├── UnityTreePrinter.java       (Parse tree printer)
│   │       ├── PrintFormat.java            (Output format enum)
│   │       ├── ValidationError.java        (Error representation)
│   │       └── XmlNameValidator.java       (XML Name validation)
│   └── test/java/com/metamadbooks/unity/parser/
│       ├── UnityParserTest.java            (Comprehensive tests)
│       └── UnityTreePrinterTest.java       (Tree printer tests)
```

### Build Configuration

- **Group/Artifact**: com.metamadbooks / unity-antlr
- **Version**: 1.0.0-SNAPSHOT
- **Java**: 21
- **ANTLR**: 4.13.2
- **Testing**: JUnit 5.10.2

The pom.xml configures antlr4-maven-plugin to process only Unity.g4 and generate both visitor and listener classes.

### Unity.g4 Grammar

The main grammar defines the complete Unity syntax:

- `unity` - Entry point: single UnityElement + EOF
- `unityElement` - JSON array with element name at index 0
- `elementName` - String validated as XML Name by listener
- `elementContent` - Attributes object, nested elements, or primitives
- `attributesObject` - JSON object with string keys and primitive values only
- `attributePair` - Key (validated as XML Name) with primitive value
- `primitiveValue` - STRING, NUMBER, true, false, null

### Java Components

**Unity.java** - Main API facade
- `parse(String input)` - Returns ParseResult with tree and errors
- `isValid(String input)` - Quick validity check
- `print(ParseResult, PrintFormat)` - Formats parse tree for display
- `print(ParseResult)` - Formats parse tree in LOG format (default)
- Inner class `ParseResult` provides access to parse tree and validation errors

**UnityValidationListener.java** - Semantic validation via ANTLR listener pattern
- Validates element/attribute names as XML Names
- Ensures attributes object only appears at index 1
- Prevents nested objects/arrays in attributes
- Maintains element path stack for error context (e.g., "/root/child")

**XmlNameValidator.java** - Implements XML 1.0 Name production rules
- NameStartChar and NameChar validation per W3C spec
- Supports Unicode ranges (except surrogate pairs >= 0x10000)

**ValidationError.java** - Error representation with line, column, message, and path

**PrintFormat.java** - Enum defining output formats for tree printing
- `LOG` - Diagnostic log-style with ENTER/EXIT events and element paths
- `TREE` - Indented hierarchical tree format
- `LISP` - S-expression (Lisp-like) format

**UnityTreePrinter.java** - ANTLR listener for formatted parse tree output
- Extends `UnityBaseListener` to walk parse trees
- Supports all three `PrintFormat` output styles
- Tracks element path for contextual output

### Parse Tree Printing

The `Unity.print()` method formats parse trees for display in three output formats:

```java
Unity.ParseResult result = Unity.parse("[\"root\", {\"id\": \"123\"}, \"text\", [\"child\"]]");

// LOG format (default) - diagnostic with ENTER/EXIT events
String logOutput = Unity.print(result, PrintFormat.LOG);
// Output:
// ENTER element: "root" at /root
//   ATTRIBUTE: id = "123"
//   CONTENT: "text"
//   ENTER element: "child" at /root/child
//   EXIT element: "child"
// EXIT element: "root"

// TREE format - indented hierarchical view
String treeOutput = Unity.print(result, PrintFormat.TREE);
// Output:
// Element(root)
//   Attr(id="123")
//   Content("text")
//   Element(child)

// LISP format - S-expression style
String lispOutput = Unity.print(result, PrintFormat.LISP);
// Output:
// (element "root"
//   (attrs (id "123"))
//   (content "text")
//   (element "child"))
```

### Test Coverage

**UnityParserTest.java** contains 40+ test cases covering:

- **Valid documents**: Self-closing elements, text content, attributes, namespaced elements, nested structures, mixed content, numeric/boolean/null values
- **Syntax errors**: Non-array top level, empty arrays, wrong top-level types
- **Semantic errors**: Invalid element/attribute names (digits, spaces, hyphens at start), attributes at wrong index
- **Edge cases**: Unicode element names, escaped characters, multiline documents
- **Error reporting**: Line/column accuracy, multiple error collection

**UnityTreePrinterTest.java** contains 21 test cases covering:

- **LOG format**: Simple elements, text content, attributes, nested elements, mixed content
- **TREE format**: Indentation verification, attribute formatting, content display
- **LISP format**: S-expression structure, nested parentheses, attribute groups
- **Special values**: Numeric content, boolean/null values in content and attributes
- **Deep nesting**: Multi-level element hierarchies with path tracking

### Building and Testing

```bash
cd repos/unity-antlr
mvn clean compile      # Generates parser from Unity.g4, compiles Java
mvn test               # Runs test suite
mvn package            # Creates JAR artifact
```

---

## Specification Reference

- Unity spec version: 1.0.0
- XML Infoset reference: https://www.w3.org/TR/2004/REC-xml-infoset-20040204/
