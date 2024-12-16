package org.example;

import org.example.LEXER.Lexer;
import org.example.SEMAMTIC.Semantic;
import org.example.SINTAX.Node;
import org.example.SINTAX.Syntax;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.example.LEXER.Lexer.lexemes;
import static org.example.LEXER.Lexer.variable_types;

public class Main {
    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        try {
            Lexer.run();
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.printf("Семантика выполнена за %.3f миллисекунд%n", duration.toNanos() / 1_000_000.0);
            System.out.println("TN:"+Lexer.TN + "\n");
            System.out.println("TI:"+Lexer.TI + "\n");
            System.out.println("types"+Lexer.variable_types +"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Node parsertree = Syntax.run(lexemes);
        Semantic.run(parsertree, variable_types);
    }
}