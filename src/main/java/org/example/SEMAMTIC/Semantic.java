package org.example.SEMAMTIC;

import org.example.SINTAX.Node;

import java.util.HashMap;

public class Semantic {
    public static void run(Node parentNode, HashMap<Integer, String> varible_types){
       SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(parentNode,varible_types);
       semanticAnalyzer.analyze();
       System.out.println("УРА");
    }
}
