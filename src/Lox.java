package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean errorOccurred = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); // Commonly used to indicate improper or lack of arguments
        } else if (args.length == 1) {
            runFile(args[0]); // Run provided file
        } else {
            runPrompt(); // Interactive mode
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path)); // Reads bytes of invoked file
        run(new String(bytes, Charset.defaultCharset())); // Invokes run() after converting bytes

        if (errorOccurred) System.exit(65); // Exit if the code has run into an error
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); // Stream reader to read from stdin
        BufferedReader reader = new BufferedReader(input); // Initialize reader

        while (true) {
            System.out.print("jlox$ "); // JLOX REPL
            String line = reader.readLine(); // Read stdin
            if (line == null)
                break; // Check for CTRL+D
            run(line);
            errorOccurred = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token); // Print tokens
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
            String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        errorOccurred = true;
    }
}