package jackanalyzer;

import java.io.*;
/**
 * The JackAnalyzer class serves as the entry point for analyzing Jack programs.
 * Responsibilities:
 * - Accepts a single input: either a .jack file or a directory containing .jack files.
 * - For a .jack file, it generates a corresponding .xml file in the same folder.
 * - For a directory, it processes all .jack files in the directory and generates .xml files for each.
 * Usage:
 * - Run the program with a single command-line argument: the path to the file or directory.
 * - Example:
 *   - JackAnalyzer <path-to-file>.jack -> Creates <path-to-file>.xml
 *   - JackAnalyzer <path-to-directory>/ -> Creates .xml files for all .jack files in the directory.
 * Error Handling:
 * - Provides informative messages if the input path does not exist or is invalid.
 * - Skips non-.jack files and subdirectories when processing directories.
 */

public class JackAnalyzer {
    public static void main(String[] args) throws IOException {
        // Ensure exactly one input parameter is provided
        if (args.length != 1) {
            System.out.println("Please provide exactly one jack file path or a directory path");
            return;
        }
        // Takes args[0] as the relevant path.
        File path = new File(args[0]);
        // Checks if the input exists.
        if (!path.exists()) {
            System.out.println("Error: The specified path does not exist.");
            return;
        }
        // Process input based on whether it's a file or a directory regarding the instructions.
        if (path.isFile() && path.getName().endsWith(".jack")) {
            // Handles a single .jack file
            jackToXML(path);
        } else if (path.isDirectory()) {
            // Process all .jack files in the directory
            File[] jackFiles = path.listFiles((dir, name) -> name.endsWith(".jack"));
            if (jackFiles != null && jackFiles.length > 0) {
                for (File jackFile : jackFiles) { // Iterates the folder and 'JackAnalyze' it.
                    jackToXML(jackFile);
                }
            } else {
                System.out.println("No .jack files found in the specified directory.");
            }
        } else {
            System.out.println("Error: Input must be a .jack file or a directory containing .jack files.");
        }
    }

    /**
     * Handling a single .jack file by tokenizing, compiling, and outputting XML. will be used for any 'JackAnalyzing' purposes.
     * @param jackFile the .jack file to process.
     */
    private static void jackToXML(File jackFile) throws IOException {
        System.out.println("Processing: " + jackFile.getName());
        // Create a tokenizer for the input file using the relevant class.
        JackTokenizer tokenizer = new JackTokenizer(jackFile);
        // Determine the output file path as the same folder and '.jack' replaced by '.xml'.
        String XMLFileName = jackFile.getAbsolutePath().replace(".jack", ".xml");
        File XMLFile = new File(XMLFileName);
        // Create and run the compilation engine.
        CompilationEngine engine = new CompilationEngine(tokenizer, XMLFile);
        engine.compileClass();
        engine.close();
        System.out.println("Output written to: " +  XMLFileName);
    }
}
