package org.example;

import org.example.LEXER.Lexer;
import org.example.SINTAX.Syntax;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try {
            Lexer.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Syntax.run();
    }
}