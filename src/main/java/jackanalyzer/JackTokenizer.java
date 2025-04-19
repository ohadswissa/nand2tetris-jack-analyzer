package jackanalyzer;
import  java.io.*;
import java.util.*;
/**
 * The JackTokenizer class breaks a .jack file into individual tokens,
 * which are the smallest meaningful elements in the Jack language.
 *
 * Tokens include:
 * - KEYWORD: Words like "class", "method", "if", "while", etc.
 * - SYMBOL: Characters like '{', '}', '=', '+', etc.
 * - IDENTIFIER: Names of variables, classes, methods, etc.
 * - INT_CONST: Numbers like 123.
 * - STRING_CONST: Text in quotes, like "hello".
 *
 * Key Features:
 * - Removes all comments from the file.
 * - Processes the file and stores all tokens in a list.
 * - Provides methods to navigate through tokens and retrieve their type and value.
 *
 * How it works:
 * 1. Reads the .jack file during initialization.
 * 2. Splits the file into tokens and stores them in a list.
 * 3. Allows to navigate and access tokens with methods like hasMoreTokens() and advance().
 */
public class JackTokenizer {
    private BufferedReader reader; // Reads the input file line by line.
    private String currentToken; // Obtains the current token being processed.
    private List<String> tokens; // All the extracted tokens.
    private int tokenIndex; // Current token index in the tokens list.
    private boolean insideBlockComment = false; // For removing comments method.
    /**
     * Saving the predefined Jack keywords.
     */
    private static final Set<String> KEYWORDS = Set.of(
            "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean",
            "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return"
    );

    /**
     * Saving the predefined Jack symbols.
     */
    private static final Set<Character> SYMBOLS = Set.of(
            '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    );

    /**
     * Constructor for initializing the tokenizer and extract (with a helper function) the tokens from the input file.
     *
     * @param inputFile as the jack file needed to be tokenized.
     * @throws IOException if for some reason the file cannot be read.
     */
    public JackTokenizer(File inputFile) throws IOException {
        reader = new BufferedReader(new FileReader(inputFile));
        tokens = new ArrayList<>(); // Initialization for the tokens list.
        tokenIndex = 0; // Initialization for the token index, corresponding to the beginning of the token list.
        tokenizeFile(); // Tokenizes the file.
        if (!tokens.isEmpty()) {
            currentToken = tokens.get(0); //The first token set to be the current token if it exists.
        }
    }

    /**
     * Checks if there are more tokens inside the given input.
     * @return true if there are more tokens , else - false.
     */
    public boolean hasMoreTokens() {
        return tokenIndex < tokens.size();
    }

    /**
     * Getting the next token and set it to be the current token only if hasMoreToken() == true.
     */
    public void advance() {
        if (hasMoreTokens()) {
            currentToken = tokens.get(tokenIndex++);
        }
    }

    /**
     * @return the type of the current token as a constant.
     */
    public String tokenType() {
        if (KEYWORDS.contains(currentToken)) { // For keyword and symbol we'll use the sets we defined.
            return "KEYWORD";
        } else if (SYMBOLS.contains(currentToken.charAt(0))) {
            return "SYMBOL";
        } else if (currentToken.matches("\\d+")) { // '\\d' matches a digit (0-9), + means "one or more digits".
            return "INT_CONST";
        } else if (currentToken.startsWith("\"") && currentToken.endsWith("\"")) { // This checks if the currentToken starts and ends with a double quote ("), indicating a string constant.
            return "STRING_CONST";
        } else {
            return "IDENTIFIER";
        }
    }

    /**
     * @return the keyword which is the current token. as a constant. this method should be called only if tokenType is KEYWORD.
     */
    public String keyWord() {
        return currentToken;
    }

    /**
     * @return the character which is the current token. should be called only if tokenType is symbol.
     */
    public char symbol() {
        return currentToken.charAt(0);
    }

    /**
     * @return the string which is the current token. should be called only if tokenType is identifier.
     */
     public String identifier() {
         return currentToken;

     }
    /**
     * @return the int value of the current token. should be called only if tokenType() is INT_CONST.
     */
    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    /**
     * @return the string value of the current token. should be called only if tokenType() is STRING_CONST.
     */
    public String stringVal() {
        return currentToken.substring(1, currentToken.length() - 1);
    }

    /**
     * Reads the input file line by line and takes care of comments. after removing them - tokenize it.
     *
     * @throws IOException if an error arises while reading the file.
     */
    private void tokenizeFile() throws IOException {
        String line = reader.readLine();
        while (line != null) {
            // trimming and removing comments
            line = removeComments(line).trim();
            if (!line.isEmpty()) {
                tokens.addAll(tokenizeLine(line));
            }
            line = reader.readLine();
        }
        reader.close();
    }

    /**
     * the purpose of this helper function is to remove single line and in line comments from a given line.
     * Adjusted couple of times to handle large block of comments.
     * @param line of jack code as a string.
     * @return the given line without comments.
     */
    private String removeComments(String line) {
        // If we're inside a block comment, look for the closing "*/".
        if (insideBlockComment) {
            if (line.contains("*/")) {
                line = line.substring(line.indexOf("*/") + 2); // Remove everything up to "*/".
                insideBlockComment = false; // Exiting the block comment
            } else {
                // Entire line is part of the block comment, so skip it
                return "";
            }
        }

        // Handle single-line comments "//".
        if (line.contains("//")) {
            line = line.substring(0, line.indexOf("//"));
        }

        // Handle block comments "/* ... */".
        if (line.contains("/*")) {
            if (line.contains("*/")) {
                // Inline block comment (e.g., code /* comment */ code)
                line = line.substring(0, line.indexOf("/*")) + line.substring(line.indexOf("*/") + 2);
            } else {
                // Block comment starts but doesn't end on this line
                line = line.substring(0, line.indexOf("/*"));
                insideBlockComment = true; // Entering block comment
            }
        }

        return line.trim(); // Return the cleaned-up line.
    }

    /**
     * Tokenizing a single line of jack code into a list of tokens.
     * @param line as the jack code line.
     * @return A list of tokens that been extracted from the jack line.
     */
    private List<String> tokenizeLine(String line) {
        List<String> tokensLine = new ArrayList<>();
        StringBuilder token = new StringBuilder(); // For elegant building purposes.
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                // We are at the end of the token.
                if (token.length() > 0) {
                    tokensLine.add(token.toString());
                    token.setLength(0); // Reset the StringBuilder after a token has been completed for reusing it to build the next token.
                }
            } else if (SYMBOLS.contains(c)) {
                // Individual symbol : add it directly as a standalone token.
                if (token.length() > 0) {
                    tokensLine.add(token.toString());
                    token.setLength(0); // As seen above.
                }
                tokensLine.add(String.valueOf(c)); //Adding the symbol as a token.
            } else if (c == '"') {
                int IndexOfEnd = line.indexOf('"', i + 1); // By this operation we find the second appearance of ' " '.
                if (IndexOfEnd != -1) {
                    tokensLine.add(line.substring(i , IndexOfEnd + 1)); // The string content.
                    i = IndexOfEnd; // For continue iterations
                }
            } else {
                token.append(c);
                }
            }
        if (token.length() > 0) {
            tokensLine.add(token.toString());
        }
        return tokensLine;
        }

    /**
     * To maintain encapsulation and still access the currentToken.
     * @return current token.
     */
    public String getCurrentToken() {
        return currentToken;
    }
    /**
     * To maintain encapsulation and still access the identifier.
     * @return current token.
     */
    public String getIdentifier() {
        return currentToken;
    }
}



