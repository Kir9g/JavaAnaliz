package org.example.SEMAMTIC;

import org.example.SINTAX.Node;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class Semantic {
    public static void run(Node parentNode, HashMap<Integer, String> varible_types){
        Instant start = Instant.now();

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(parentNode,varible_types);
        semanticAnalyzer.analyze();

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.printf("Семантика выполнена за %.3f миллисекунд%n", duration.toNanos() / 1_000_000.0);

    }
}
