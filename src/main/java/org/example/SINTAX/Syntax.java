package org.example.SINTAX;

import org.example.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class Syntax {
    public static void run() {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream("src/main/resources/lexem.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
