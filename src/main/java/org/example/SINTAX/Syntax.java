package org.example.SINTAX;

import org.example.Token;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Syntax {
    public static Node run(List<Token> lexemes) throws Exception {
        Instant start = Instant.now();
//        ParserTree parserTree = new ParserTree("src/main/resources/lexem.txt");
        ParserTree parserTree = new ParserTree(lexemes);
        Node parentNode = null;
        try {
            parentNode = parserTree.parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        String outputFilePath = "src/main/java/org/example/Test/ast_output2.txt";
        parserTree.saveAstToFile(parentNode, outputFilePath);

        // Вывод AST в консоль
        System.out.println("AST:");
        parentNode.printTree(0);

        System.out.printf("Семантика выполнена за %.3f миллисекунд%n", duration.toNanos() / 1_000_000.0);
        return parentNode;
    }
}
