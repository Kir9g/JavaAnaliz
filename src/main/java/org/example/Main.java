package org.example;

import org.example.LEXER.Lexer;
import org.example.SINTAX.Syntax;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        try {
            Lexer.run();
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println("Время выполнения: " + duration.toMillis() + " миллисекунд");

            System.out.println("TN:"+Lexer.TN);
            System.out.println("TN:"+Lexer.TI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Syntax.run();
    }
}