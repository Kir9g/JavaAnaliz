package org.example.SINTAX;

import org.example.Token;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Syntax {
    public static Node run(List<Token> lexemes) throws IOException {
        Instant start = Instant.now();
//        ParserTree parserTree = new ParserTree("src/main/resources/lexem.txt");
        ParserTree parserTree = new ParserTree(lexemes);
        Node parentNode = parserTree.parse();

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        String outputFilePath = "src/main/resources/ast_output.txt";
        parserTree.saveAstToFile(parentNode, outputFilePath);

        // Вывод AST в консоль
        System.out.println("AST:");
        parentNode.printTree(0);

        System.out.printf("Семантика выполнена за %.3f миллисекунд%n", duration.toNanos() / 1_000_000.0);
        return parentNode;
    }
}
