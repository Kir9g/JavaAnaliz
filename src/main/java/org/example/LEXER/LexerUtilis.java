package org.example.LEXER;

import static org.example.LEXER.Lexer.TL;
import static org.example.LEXER.Lexer.TW;

public class LexerUtilis {
    public static void loadTW(){
        TW.put("end", 1);
        TW.put("begin", 2);
        TW.put("var", 3);
        TW.put("dim", 4);
        TW.put("int", 5);
        TW.put("float", 6);
        TW.put("bool", 7);
        TW.put("if", 8);
        TW.put("then", 9);
        TW.put("else", 10);
        TW.put("read", 11);
        TW.put("write", 12);
        TW.put("for", 13);
        TW.put("while", 14);
        TW.put("do", 15);
        TW.put("to", 16);
    }
    public static void loadTL(){
        TL.put("NEQ", 1);
        TL.put("EQV", 2);
        TL.put("LOWT", 3);
        TL.put("LOWE", 4);
        TL.put("GRT", 5);
        TL.put("GRE", 6);
        TL.put("add", 7);
        TL.put("disa", 8);
        TL.put("||", 9);
        TL.put("umn", 10);
        TL.put("del", 11);
        TL.put("&&", 12);
        TL.put("\n", 13);
        TL.put("^", 14);
        TL.put(",", 15);
        TL.put(":", 16);
        TL.put("as", 17);
        TL.put(";", 18);
        TL.put("(",19);
        TL.put(")",20);
    }
    public static void start(){
        loadTW();
        loadTL();
    }
}
