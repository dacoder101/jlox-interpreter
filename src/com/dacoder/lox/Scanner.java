package com.dacoder.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dacoder.lox.TokenType.*; 

public class Scanner {
    private final String source;

    private final List<Token> tokens = new ArrayList<Token> ();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!atEnd()) {
            // Beginning of next lexeme

            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));

        return tokens;
    }

    private boolean atEnd() {
        return current >= source.length();
    }
}