package org.example.SINTAX;

import org.example.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Syntax {
    public static Node run() throws IOException {
        Instant start = Instant.now();
        ParserTree parserTree = new ParserTree("src/main/resources/lexem.txt");
        Node parentNode = parserTree.parse();

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        String outputFilePath = "src/main/resources/ast_output.txt";
        parserTree.saveAstToFile(parentNode, outputFilePath);

        // Вывод AST в консоль
        System.out.println("AST:");
        parentNode.printTree(0);

        System.out.println("Синтаксис выполнен за "+duration.toMillis() +" милисекунд");
        return parentNode;
    }
}
