package org.example;

import org.example.LEXER.Lexer;
import org.example.SEMAMTIC.Semantic;
import org.example.SINTAX.Node;
import org.example.SINTAX.Syntax;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.example.LEXER.Lexer.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Instant start = Instant.now();
        try {
            Lexer.run();
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println("\n\n\n");
            System.out.printf("Лексика выполнена успешно за %.3f миллисекунд%n", duration.toNanos() / 1_000_000.0);
//            System.out.println("TN:"+Lexer.TN + "\n");
            System.out.println("Числа: ");
            for (Map.Entry<String, Integer> entry : TN.entrySet()) {
                System.out.println("("+entry.getKey() + ", " + entry.getValue() + ") ");
            }
            System.out.print("Идентификаторы: ");
            for (Map.Entry<String, Integer> entry : TI.entrySet()) {
                System.out.println("("+entry.getKey() + ", " + entry.getValue() + ") ");
            }
            //System.out.println("Цифры в машиннов коде: ");
//            Lexer.TN.forEach((key, value) -> {
//                StringBuilder binaryKey = new StringBuilder();
//                for (char ch : key.toCharArray()) {
//                    String binaryChar = Integer.toBinaryString(ch);
//                    binaryKey.append(binaryChar).append(" ");
//                }
//                System.out.printf("%s, Номер в таблице: %d%n", binaryKey.toString().trim(), value);
//            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Node parsertree = Syntax.run(lexemes);
        Semantic.run(parsertree, variable_types);
    }
}