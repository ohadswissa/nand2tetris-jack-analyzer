package jackanalyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JackTokenizerTest {
    private File testFile;
    private JackTokenizer tokenizer;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary test file
        testFile = File.createTempFile("test", ".jack");
        try (PrintWriter writer = new PrintWriter(testFile)) {
            writer.println("// This is a comment");
            writer.println("class Main {");
            writer.println("    function void main() {");
            writer.println("        var int x;");
            writer.println("        let x = 42;");
            writer.println("        do Output.printString(\"Hello, World!\");");
            writer.println("        return;");
            writer.println("    }");
            writer.println("}");
        }
        // Initialize the tokenizer
        tokenizer = new JackTokenizer(testFile);
    }

    @Test
    void testTokenizer() {
        // Expected tokens
        List<String> expectedTokens = List.of(
                "class", "Main", "{",
                "function", "void", "main", "(", ")", "{",
                "var", "int", "x", ";",
                "let", "x", "=", "42", ";",
                "do", "Output", ".", "printString", "(", "\"Hello, World!\"", ")", ";",
                "return", ";",
                "}", "}"
        );

        // Token types for the expected tokens
        List<String> expectedTokenTypes = List.of(
                "KEYWORD", "IDENTIFIER", "SYMBOL",
                "KEYWORD", "KEYWORD", "IDENTIFIER", "SYMBOL", "SYMBOL", "SYMBOL",
                "KEYWORD", "KEYWORD", "IDENTIFIER", "SYMBOL",
                "KEYWORD", "IDENTIFIER", "SYMBOL", "INT_CONST", "SYMBOL",
                "KEYWORD", "IDENTIFIER", "SYMBOL", "IDENTIFIER", "SYMBOL", "STRING_CONST", "SYMBOL", "SYMBOL",
                "KEYWORD", "SYMBOL",
                "SYMBOL", "SYMBOL"
        );

        System.out.println("Starting Tokenizer Test...");
        System.out.println("-------------------------------------------------");

        // Check tokens
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();

            // Get current token and type
            String actualToken = tokenizer.getCurrentToken();
            String actualTokenType = tokenizer.tokenType();

            // Log the current token and its type
            System.out.printf("Processing Token %d: '%s' (%s)%n", index + 1, actualToken, actualTokenType);

            // Validate token value
            assertEquals(expectedTokens.get(index), actualToken,
                    String.format("Mismatch at Token %d: Expected '%s', Found '%s'", index + 1, expectedTokens.get(index), actualToken));

            // Validate token type
            assertEquals(expectedTokenTypes.get(index), actualTokenType,
                    String.format("Type mismatch at Token %d: Expected '%s', Found '%s'", index + 1, expectedTokenTypes.get(index), actualTokenType));

            // Check token-specific methods
            switch (actualTokenType) {
                case "KEYWORD":
                    assertEquals(expectedTokens.get(index), tokenizer.keyWord(),
                            "Keyword mismatch at index " + index);
                    break;
                case "SYMBOL":
                    assertEquals(expectedTokens.get(index).charAt(0), tokenizer.symbol(),
                            "Symbol mismatch at index " + index);
                    break;
                case "IDENTIFIER":
                    assertEquals(expectedTokens.get(index), tokenizer.getIdentifier(),
                            "Identifier mismatch at index " + index);
                    break;
                case "INT_CONST":
                    assertEquals(Integer.parseInt(expectedTokens.get(index)), tokenizer.intVal(),
                            "Integer constant mismatch at index " + index);
                    break;
                case "STRING_CONST":
                    assertEquals(expectedTokens.get(index).substring(1, expectedTokens.get(index).length() - 1),
                            tokenizer.stringVal(),
                            "String constant mismatch at index " + index);
                    break;
            }

            index++;
        }

        System.out.println("-------------------------------------------------");
        System.out.println("All tokens processed successfully!");

        // Ensure all tokens were processed
        assertEquals(expectedTokens.size(), index, "Not all tokens were processed.");
    }
}
