package com.dacoder.lox;

import static com.dacoder.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;

    private final List<Token> tokens = new ArrayList<Token>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = new HashMap<String, TokenType>();

    // Keywords
    static {
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

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
                if (match('*')) {
                    blockComment();
                } else if (match('/')) {
                    addToken(DOUBLE_SLASH);
                } else {
                    addToken(SLASH);
                }

                break;
            case '%':
                addToken(MODULO);
                break;

            // MISC
            case '#': {
                while (peek() != '\n' && !atEnd())
                    advance();
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

            // Literals
            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else { // Unsupported character
                    Lox.error(line, "Unexpected character: " + c);
                    break;
                }
        }
    }

    private void blockComment() {
        int nestingLevel = 1; // To track nested comments
    
        while (nestingLevel > 0 && !atEnd()) {
            char currentChar = advance();
    
            if (currentChar == '/' && peek() == '*') {
                advance(); // Consume '*'
                nestingLevel++;
            } else if (currentChar == '*' && peek() == '/') {
                advance(); // Consume '/'
                nestingLevel--;
            } else if (currentChar == '\n') {
                line++;
            }
        }
    }
    
    

    private void string() {
        while (peek() != '"' && !atEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (atEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        advance();

        addToken(STRING, source.substring(start + 1, current - 1));
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekTwo())) {
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String identifier = source.substring(start, current);
        TokenType type = keywords.get(identifier);

        if (type == null)
            type = IDENTIFIER;

        addToken(type);
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
        if (atEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekTwo() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
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