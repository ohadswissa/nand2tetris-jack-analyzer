package jackanalyzer;
import java.io.*;

/**
 * The CompilationEngine class parses a Jack program and generates an XML representation
 * of its syntax, following the Jack grammar.
 *
 * Responsibilities:
 *  Processes tokens from the JackTokenizer.
 *  Parses and outputs XML for:
 *   - Classes, variables, and subroutines.
 *   - Statements (let, if, while, do, return).
 *   - Expressions, terms, and lists of expressions.
 * Usage:
 * - Initialize with a JackTokenizer and output file.
 * - Call `compileClass()` to start parsing.
 * - Close the engine after parsing to finalize the output.
 * Example:
 * CompilationEngine engine = new CompilationEngine(tokenizer, outputFile);
 * engine.compileClass();
 * engine.close();
 */
public class CompilationEngine {
    private JackTokenizer tokenizer;
    private PrintWriter writer;
    private int spaceCheckerLevel = 0; // For XML formatting purposes.

    /**
     * Creates a new compilation engine with the given input and output.
     * @param tokenizer the JackTokenizer providing the input tokens.
     * @param outputFile is the file where the XML output will be written.
     */
    public CompilationEngine(JackTokenizer tokenizer, File outputFile) throws FileNotFoundException {
        this.tokenizer = tokenizer;
        this.writer = new PrintWriter(outputFile);
    }

    /**
     * Compiles a complete class.
     */
    public void compileClass() {
        writer.println(spaceChecker() + "<class>");
        spaceCheckerLevel++; // Increase indentation for nested elements.
        tokenizer.advance();
        // Handles 'class'.
        writer.println(spaceChecker() + "<keyword> class </keyword>");
        tokenizer.advance();
        // Handles 'class' name as an identifier.
        writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
        tokenizer.advance();
        //  opening '{'.
        writer.println(spaceChecker() + "<symbol> { </symbol>");
        tokenizer.advance();
        // While loops for compiling the class as needed with the relevant compilers.
        while (tokenizer.tokenType().equals("KEYWORD") && (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field"))) {
            compileClassVarDec();
        }
        while (tokenizer.tokenType().equals("KEYWORD") && (tokenizer.keyWord().equals("constructor") || tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method"))) {
            compileSubroutine();
        }
        // closing '}'.
        writer.println(spaceChecker() + "<symbol> } </symbol>");
        spaceCheckerLevel--; // Decrease indentation for closing tags.
        // Closing tag as needed.
        writer.println(spaceChecker() + "</class>");
    }

    /**
     * Compiles a static variable declaration, or a field declaration.
     */
     public void compileClassVarDec() {
         writer.println(spaceChecker() + "<classVarDec>");
         spaceCheckerLevel++; // Increase indentation.
         // 'static | field' handling.
         writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
         tokenizer.advance();
         // Write type (keyword or identifier)
         if (tokenizer.tokenType().equals("KEYWORD")) {
             // Handle keywords: int, char, boolean
             writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
         } else {
             // Handle identifiers like SquareGame
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         }
         tokenizer.advance();
         // VarName handling.
         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         tokenizer.advance();
         // (',' VarName)* handling.
         while (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
             writer.println(spaceChecker() + "<symbol> , </symbol>");
             tokenizer.advance();
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
             tokenizer.advance();
         }
         // ';' handling.
         writer.println(spaceChecker() + "<symbol> ; </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Decrease indentation.
         writer.println(spaceChecker() + "</classVarDec>");
     }

    /**
     * Compiles a complete method, function or a constructor.
     */
     public void compileSubroutine() {
         writer.println(spaceChecker() + "<subroutineDec>");
         spaceCheckerLevel++; // Increase indentation.
         // ('constructor' | 'function' | 'method') handling.
         writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
         tokenizer.advance();
         // ('void' | type) handling.
         if (tokenizer.tokenType().equals("KEYWORD")) {
             writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
         } else if (tokenizer.tokenType().equals("IDENTIFIER")) {
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         }
         tokenizer.advance();
         // subroutine name handling.
         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         tokenizer.advance();
         // '(' handling/
         writer.println(spaceChecker() + "<symbol> ( </symbol>");
         tokenizer.advance();
         // Parameter list handling with the relevant compile method.
         compileParameterList();
         // ')' handling/
         writer.println(spaceChecker() + "<symbol> ) </symbol>");
         tokenizer.advance();
         // subroutine body handling with the relevant compile method.
         compileSubroutineBody();
         spaceCheckerLevel--; // Decrease indentation.
         writer.println(spaceChecker() + "</subroutineDec>");
     }

    /**
     * Compiles a (possibly empty) parameter list. Does not handle the enclosing parentheses tokens '(' and ')'.
     */
     public  void compileParameterList() {
         writer.println(spaceChecker() + "<parameterList>");
         spaceCheckerLevel++; // Ensuring spacing.
         // Checks if the list is not empty.
         if (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ')')) {
             // Type handling.
             writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
             tokenizer.advance();
             // Variable name handling.
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
             tokenizer.advance();
             // (',' type VarName)* handling.
             while (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == ',')) {
                 writer.println(spaceChecker() + "<symbol> , </symbol>");
                 tokenizer.advance();
                 // Type handling.
                 writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
                 tokenizer.advance();
                 // Variable name handling.
                 writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
                 tokenizer.advance();
             }
         }
         spaceCheckerLevel--; // Ensuring spacing.
         writer.println(spaceChecker() + "</parameterList>"); // closing the tokenizing paragraph.
     }

    /**
     * Compiles a subroutine's body.
     */
     public void compileSubroutineBody() {
         writer.println(spaceChecker() + "<subroutineBody>");
         spaceCheckerLevel++; // Ensuring spacing.
         // '{' handling.
         writer.println(spaceChecker() + "<symbol> { </symbol>");
         tokenizer.advance();
         // Variable declarations occurrences (*) handling.
         while (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().equals("var")) {
             compileVarDec();
         }
         // handling statements with relevant compiler.
         compileStatements();
         // '}' handling.
         writer.println(spaceChecker() + "<symbol> } </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring spacing.
         writer.println(spaceChecker() + "</subroutineBody>"); // closing the tokenizing paragraph.
     }

    /**
     * Compiles a var declaration.
     */
     public void compileVarDec() {
         writer.println(spaceChecker() + "<varDec>");
         spaceCheckerLevel++; // Ensuring spacing.
         // 'var' handling.
         writer.println(spaceChecker() + "<keyword> var </keyword>");
         tokenizer.advance();
         // Write type (keyword or identifier)
         if (tokenizer.tokenType().equals("KEYWORD")) {
             // Handle keywords: int, char, boolean
             writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
         } else {
             // Handle identifiers like SquareGame
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         }
         tokenizer.advance();
         // variable name handling.
         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         tokenizer.advance();
         // (',' VarName occurrences) handling.
         while (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
             writer.println(spaceChecker() + "<symbol> , </symbol>");
             tokenizer.advance();
             // VarName handling.
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
             tokenizer.advance();
         }
         writer.println(spaceChecker() + "<symbol> ; </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring spacing.
         writer.println(spaceChecker() + "</varDec>");
     }

    /**
     * Compiles a sequence of statements. does not handle the enclosing curly bracket tokens '{' and '}'.
     */
    public void compileStatements() {
        writer.println(spaceChecker() + "<statements>");
        spaceCheckerLevel++; // Ensuring spacing.
        // Process each statement based on its keyword.
        while (tokenizer.tokenType().equals("KEYWORD") && (
                tokenizer.keyWord().equals("let") || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while") || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return"))) {
            switch (tokenizer.keyWord()) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
            }
        }
        spaceCheckerLevel--; // Ensuring spacing.
        writer.println(spaceChecker() + "</statements>");
    }

    /**
     * Compiles a let statement.
     */
     public void compileLet() {
         writer.println(spaceChecker() + "<letStatement>");
         spaceCheckerLevel++; // Ensuring spacing.
         // Handling 'let' keyword.
         writer.println(spaceChecker() + "<keyword> let </keyword>");
         tokenizer.advance();
         // Var name handling.
         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         tokenizer.advance();
         // Case of array.
         if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '[') {
             writer.println(spaceChecker() + "<symbol> [ </symbol>");
             tokenizer.advance();
             // Handle the expression inside the brackets with the relevant compiler method.
             compileExpression();
             // Closing the brackets as needed.
             writer.println(spaceChecker() + "<symbol> ] </symbol>");
             tokenizer.advance();
         }
         // Handling '=' sign of a let statement. notice we'll get here in any case whether it's an array or whether it's not.
         writer.println(spaceChecker() + "<symbol> = </symbol>");
         tokenizer.advance();
         // Handle the expression after '='.
         compileExpression();
         // Close the line with ';'.
         writer.println(spaceChecker() + "<symbol> ; </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring spacing.
         writer.println(spaceChecker() + "</letStatement>"); // Closing as needed.
     }

    /**
     * Compiles an if statement, possibly with a trailing else clause.
     */
     public void compileIf() {
         writer.println(spaceChecker() + "<ifStatement>");
         spaceCheckerLevel++; // Ensuring spacing.
         // Handling 'if' keyword.
         writer.println(spaceChecker() + "<keyword> if </keyword>");
         tokenizer.advance();
         // '(' handling.
         writer.println(spaceChecker() + "<symbol> ( </symbol>");
         tokenizer.advance();
         // Expression handling with relevant compile method for the condition inside the brackets.
         compileExpression();
         // ')' handling.
         writer.println(spaceChecker() + "<symbol> ) </symbol>");
         tokenizer.advance();
         // '{' handling.
         writer.println(spaceChecker() + "<symbol> { </symbol>");
         tokenizer.advance();
         // Statements handling with the relevant compile method for the 'if' block.
         compileStatements();
         // '}' handling.
         writer.println(spaceChecker() + "<symbol> } </symbol>");
         tokenizer.advance();
         // Case of 'else'.
         if (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().equals("else")) {
             // 'else' handling.
             writer.println(spaceChecker() + "<keyword> else </keyword>");
             tokenizer.advance();
             // '{' handling.
             writer.println(spaceChecker() + "<symbol> { </symbol>");
             tokenizer.advance();
             // Statements handling with the relevant compile method for the 'else' block.
             compileStatements();
             // '}' handling.
             writer.println(spaceChecker() + "<symbol> } </symbol>");
             tokenizer.advance();
         }
         spaceCheckerLevel--; // Ensuring spaces;
         writer.println(spaceChecker() + "</ifStatement>"); // Closing as needed.
     }

    /**
     * Compiles a while statement.
     */
     public void compileWhile() {
         writer.println(spaceChecker() + "<whileStatement>");
         spaceCheckerLevel++; // Ensuring spaces.
         // Handling 'while' keyword.
         writer.println(spaceChecker() + "<keyword> while </keyword>");
         tokenizer.advance();
         // '(' handling.
         writer.println(spaceChecker() + "<symbol> ( </symbol>");
         tokenizer.advance();
         // Expression handling with relevant compile method for the condition inside the brackets.
         compileExpression();
         // ')' handling.
         writer.println(spaceChecker() + "<symbol> ) </symbol>");
         tokenizer.advance();
         // '{' handling.
         writer.println(spaceChecker() + "<symbol> { </symbol>");
         tokenizer.advance();
         // Statements handling with the relevant compile method for the 'while' block.
         compileStatements();
         // '}' handling.
         writer.println(spaceChecker() + "<symbol> } </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring spaces.
         writer.println(spaceChecker() + "</whileStatement>"); // Closing as needed.
     }

    /**
     * Compile a do statement.
     */
     public void compileDo() {
         writer.println(spaceChecker() + "<doStatement>");
         spaceCheckerLevel++; // Ensuring spaces.
         // Handling 'do' keyword.
         writer.println(spaceChecker() + "<keyword> do </keyword>");
         tokenizer.advance();
         // Subroutine handling.
         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
         tokenizer.advance();
         // '.' when calling method.
         if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '.') {
             writer.println(spaceChecker() + "<symbol> . </symbol>");
             tokenizer.advance();
             // Handling the subroutine name.
             writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
             tokenizer.advance();
         }
         // '(' handling.
         writer.println(spaceChecker() + "<symbol> ( </symbol>");
         tokenizer.advance();
         // Use relevant compiler for compiling list of expressions.
         compileExpressionList();
         // ')' handling.
         writer.println(spaceChecker() + "<symbol> ) </symbol>");
         tokenizer.advance();
         // Closing with ';'.
         writer.println(spaceChecker() + "<symbol> ; </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring correct spacing.
         writer.println(spaceChecker() + "</doStatement>"); // Closing as needed.
     }

    /**
     * Compiles a return statement.
     */
     public void compileReturn() {
         writer.println(spaceChecker() + "<returnStatement>");
         spaceCheckerLevel++; // Ensuring correct spacing.
         // Handling 'return' keyword.
         writer.println(spaceChecker() + "<keyword> return </keyword>");
         tokenizer.advance();
         // Covers an expression case.
         if (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';')) {
             compileExpression();
         }
         // Closing with ';'.
         writer.println(spaceChecker() + "<symbol> ; </symbol>");
         tokenizer.advance();
         spaceCheckerLevel--; // Ensuring correct spacing.
         writer.println(spaceChecker() + "</returnStatement>"); // Closing as needed.
     }

    /**
     * @param symbol needed to be checked.
     * @return true if the symbol is a valid operator, else otherwise.
     */
     private boolean isOperator(char symbol) {
         return "+-*/&|<>=".indexOf(symbol) != -1;
     }

    /**
     * Compiles an expression.
     */
    public void compileExpression() {
        writer.println(spaceChecker() + "<expression>");
        spaceCheckerLevel++; // Ensuring correct spacing.
        // Compile the first term
        compileTerm();
        // Handles occurrences of (op term)
        while (tokenizer.tokenType().equals("SYMBOL") && isOperator(tokenizer.symbol())) {
            // Get the current operator
            String operator = tokenizer.getCurrentToken();
            // Escape special characters for XML
            if (operator.equals("<")) {
                operator = "&lt;";
            } else if (operator.equals(">")) {
                operator = "&gt;";
            } else if (operator.equals("&")) {
                operator = "&amp;";
            }
            // Write the operator to the XML
            writer.println(spaceChecker() + "<symbol> " + operator + " </symbol>");
            tokenizer.advance();
            // Compile the next term
            compileTerm();
        }
        spaceCheckerLevel--; // Ensuring spacing.
        writer.println(spaceChecker() + "</expression>");
    }

    /**
     * Compiles a term. if the current token is an identifier, the routine must resolve it into a variable, an array entry, or a subroutine call.
     * A single lookahead token, which may be '[' , '(' or '.' , suffices to distinguish between the possibilities.
     * any other token is not part pf this term and should not be advance over.
     */
     public void compileTerm() {
         writer.println(spaceChecker() + "<term>");
         spaceCheckerLevel++; // Spacing purposes.
         // Use switch case for the different token types.
         switch (tokenizer.tokenType()) {
             case "INT_CONST":
                 writer.println(spaceChecker() + "<integerConstant> " + tokenizer.getCurrentToken() + " </integerConstant>");
                 tokenizer.advance();
                 break;
             case "STRING_CONST":
                 writer.println(spaceChecker() + "<stringConstant> " + tokenizer.stringVal() + " </stringConstant>");
                 tokenizer.advance();
                 break;
             case "KEYWORD":
                 writer.println(spaceChecker() + "<keyword> " + tokenizer.getCurrentToken() + " </keyword>");
                 tokenizer.advance();
                 break;
             case "SYMBOL":
                 if (tokenizer.symbol() == '(') {
                     // Case of expression inside brackets.
                     writer.println(spaceChecker() + "<symbol> ( </symbol>");
                     tokenizer.advance();
                     compileExpression();
                     writer.println(spaceChecker() + "<symbol> ) </symbol>");
                     tokenizer.advance();
                 } else if (tokenizer.symbol() == '-' || tokenizer.symbol() == '~') {
                     // Case of unary operator and term.
                     writer.println(spaceChecker() + "<symbol> " + tokenizer.getCurrentToken() + " </symbol>");
                     tokenizer.advance();
                     compileTerm();
                 }
                 break;
             case "IDENTIFIER":
                 writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
                 tokenizer.advance();
                 // Checks for accessing to an array.
                 if (tokenizer.symbol() == '[') {
                     writer.println(spaceChecker() + "<symbol> [ </symbol>");
                     tokenizer.advance();
                     compileExpression();
                     writer.println(spaceChecker() + "<symbol> ] </symbol>");
                     tokenizer.advance();
                     // Handling some Subroutine call.
                 } else if (tokenizer.symbol() == '(' || tokenizer.symbol() == '.') {
                     if (tokenizer.symbol() == '.') {
                         writer.println(spaceChecker() + "<symbol> . </symbol>");
                         tokenizer.advance();
                         writer.println(spaceChecker() + "<identifier> " + tokenizer.getCurrentToken() + " </identifier>");
                         tokenizer.advance();
                     }
                     writer.println(spaceChecker() + "<symbol> ( </symbol>");
                     tokenizer.advance();
                     compileExpressionList();
                     writer.println(spaceChecker() + "<symbol> ) </symbol>");
                     tokenizer.advance();
                 }
                 break;
             default:
                 break;
         }
         spaceCheckerLevel--; // Spacing purposes.
         writer.println(spaceChecker() + "</term>");
     }

    /**
     * Compiles a (possibly empty) comma seperated list of expressions.
     * @return the number of expressions in the list.
     */
    public int compileExpressionList() {
        writer.println(spaceChecker() + "<expressionList>");
        spaceCheckerLevel++; // Spacing.
        int expressionCount = 0;
        // Check if the list is not empty.
        if (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ')')) {
            compileExpression();
            expressionCount++;
            // Handle ',' separated expressions.
            while (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                writer.println(spaceChecker() + "<symbol> , </symbol>");
                tokenizer.advance();
                compileExpression();
                expressionCount++;
            }
        }
        spaceCheckerLevel--; // Spacing.
        writer.println(spaceChecker() + "</expressionList>");
        return expressionCount;
    }

    /**
     * Helper function for tracking the spaces needed in the code.
     * @return the space as needed in XML.
     */
    private String spaceChecker() {
        return "  ".repeat(spaceCheckerLevel); // Two spaces per level
    }

    public void close() {
        writer.close();
    }
}
