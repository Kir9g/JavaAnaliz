package org.example.SINTAX;

import org.example.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Syntax {
    public static void run() throws IOException {
        ParserTree parserTree = new ParserTree("src/main/resources/lexem.txt");
        Node parentNode = ParserTree.parse();
    }
}
