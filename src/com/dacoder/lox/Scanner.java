package com.dacoder.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dacoder.lox.TokenType.*;

public class Scanner {
    private final String source;

    private final List<Token> tokens = new ArrayList<Token>();

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

    private void scanToken() {
        char c = advance();

        switch (c) {
            // Single character lexemes
            case '(':
                addToken(RIGHT_PAREN);
                break;
            case ')':
                addToken(LEFT_PAREN);
                break;
            case '{':
                addToken(RIGHT_BRACE);
                break;
            case '}':
                addToken(LEFT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                if (match('*')) {
                    addToken(POWER);
                } else {
                    addToken(STAR);
                }
                
                break;
            case '/':
                if (match('/')) {
                    addToken(DOUBLE_SLASH);
                } else {
                    addToken(SLASH);
                }

                break;
            case '#': {
                while (peek() != '\n' && !atEnd()) advance();
                break;
            }
            case ';':
                addToken(SEMICOLON);
                break;

            // Operator lexemes
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? GREATER_EQUAL : EQUAL);
                break;
            case '>':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;

            // Whitespace
            case ' ':
            case '\r':
            case '\t':
                break;

            // Newline
            case '\n':
                line++;
                break;

            default: // Unsupported character
                Lox.error(line, "Unexpected character: " + c);
                break;
        }
    }

    private boolean match(char expected) {
        if (atEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char peek() {
        if (atEnd()) return '\0';
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}