package org.example.LEXER;

import org.example.SINTAX.Syntax;
import org.example.Token;
import org.example.STATES.States;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.STATES.States.*;

public class Lexer {
    static States state = H;
    static FileInputStream fileInputStream;

    static {
        try {
            fileInputStream = new FileInputStream("src/main/java/org/example/Test/programm.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static char CH;
    static String S = "";
    public static List<Token> lexemes = new ArrayList<>();
    static int z = 0;
    public static HashMap<String,Integer> TW = new HashMap<>();//1
    public static HashMap<String,Integer> TL = new HashMap<>();//2
    public static HashMap<String,Integer> TI = new HashMap<>();//4
    public static HashMap<String,Integer> TN = new HashMap<>();//3

    static {
        LexerUtilis.start();
    }
    static boolean canread = true;
    public static HashMap<Integer,String> variable_types = new HashMap<>();

    public static void run() throws Exception {
        gc();
        while (state != V && state!= ER) {
            switch (state){
                case H ->{
                    if(canread && (CH ==' ' || CH == '\t') ){
                        gc();
                    }
                    else if (!canread) {
                        state = ER;
                    }
                    else if(let()){
                        nill();
                        add();
                        gc();
                        state = I;
                    }
                    else if(CH == '0' || CH=='1'){
                        nill();
                        add();
                        gc();
                        state = N2;
                    }
                    else if (CH == '8' || CH == '9') {
                        nill();
                        add();
                        gc();
                        state = N10;
                    }
                    else if(CH >='2' && CH <='7'){
                        nill();
                        add();
                        gc();
                        state = N8;
                    }
                    else if (CH == '.'){
                        nill();
                        add();
                        gc();
                        state = VESH;
                    }else if (CH =='/'){
                        gc();
                        state = C1;
                    }else if (CH=='&'){
                        gc();
                        state = AND1;
                    }else if (CH=='|'){
                        gc();
                        state = OR1;
                    }
                    else{
                        nill();
                        add();
                        look(TL);
                        if(z==0){
                            state = ER;
                        }else {
                            gc();
                            out(2,z);
                        }
                    }

                }
                case I -> {
                    while ((let()||digit())&&canread){
                        add();
                        gc();
                    }
                    look(TW);
                    if(z!=0){
                        if(z==1){
                            out(1,z);
                            state = V;
                            break;
                        }else{
                            out(1,z);
                            state = H;
                            break;
                        }
                    }else {
                        look(TL);
                        if(z!=0){
                            out(2,z);
                            state = H;
                            break;
                        } else {
                            look(TI);
                            if (z!=0){
                                out(4,z);
                                state = H;
                            }else {
                                put(TI);
                                out(4, z);
                                state = H;
                            }
                        }
                    }
                }
                case N2 ->{
                    while ((CH=='0'||CH=='1') && canread ){
                        add();
                        gc();
                    }
                    char lowerCH = Character.toLowerCase(CH);
                    if(CH>='2' && CH<='8'){
                        add();
                        gc();
                        state = N8;
                    }
                    else if(CH =='8' || CH =='9'){
                        add();
                        gc();
                        state = N10;
                    }
                    else if(lowerCH == 'e'){
                        add();
                        gc();
                        state = NEF;
                    }
                    else if(lowerCH == 'o'){
                        add();
                        gc();
                        state = O;
                    }
                    else if(CH == '.'){
                        add();
                        gc();
                        state = VESH;
                    }
                    else if(lowerCH == 'd'){
                        add();
                        gc();
                        state = D;
                    }
                    else if(lowerCH == 'a' || lowerCH =='c' || lowerCH == 'f'){
                        add();
                        gc();
                        state = N16;
                    }
                    else if(lowerCH == 'h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else if(lowerCH == 'b'){
                        add();
                        gc();
                        state = B;
                    } else if (CH == ' '|| CH == '\t' || TlCheck()) {
                        state = D;
                    } else{
                        state = ER;
                    }
                }
                case N8 -> {
                    while (CH >= '0' && CH <= '7' && canread) {
                        add();
                        gc();
                    }
                    char lowerCH = Character.toLowerCase(CH);
                    if(CH == '.'){
                        add();
                        gc();
                        state = VESH;
                    }
                    else if(lowerCH == 'e'){
                        add();
                        gc();
                        state = NEF;
                    }
                    else if(CH == '8' || CH == '9'){
                        add();
                        gc();
                        state = N10;
                    }
                    else if(lowerCH =='o'){
                        add();
                        gc();
                        state = O;
                    }
                    else if(lowerCH == 'd'){
                        add();
                        gc();
                        state = D;
                    }
                    else if(lowerCH == 'a' || lowerCH == 'b' || lowerCH == 'c' || lowerCH == 'f'){
                        add();
                        gc();
                        state = N16;
                    }
                    else if(lowerCH == 'h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else if(CH=='\t' || CH== ' '|| TlCheck()){
                        state = D;
                    }
                    else{
                        state = ER;
                    }
                }
                case N10 -> {
                    while (digit() && canread){
                        add();
                        gc();
                    }
                    char lowerCH = Character.toLowerCase(CH);
                    if(CH == '.'){
                        add();
                        gc();
                        state = VESH;
                    }
                    else if(lowerCH == 'e'){
                        add();
                        gc();
                        state = NEF;
                    }
                    else if(lowerCH == 'h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else if (lowerCH == 'a' || lowerCH == 'b' || lowerCH == 'c' || lowerCH == 'f'){
                        add();
                        gc();
                        state = N16;
                    }
                    else if (lowerCH == 'd') {
                        add();
                        gc();
                        state = D;

                    }
                    else if(CH=='\t' || CH== ' '){
                        look(TN);
                        if (z!=0){
                            out(3,z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");

                            out(3, z);
                            state = H;
                        }
                    }
                    else if(TlCheck()){
                        look(TN);
                        if (z!=0){
                            out(3,z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");
                            out(3, z);
                            state = H;
                        }
                    }
                    else {
                        state = ER;
                    }
                }
                case N16 -> {
                    char LowerCh = Character.toLowerCase(CH);
                    while (digit()||LowerCh>='a'&& LowerCh<='f'){
                        add();
                        gc();
                        if (let()){
                            LowerCh = Character.toLowerCase(CH);
                        }
                    }
                    if (LowerCh == 'h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else {
                        state = ER;
                    }
                }
                case B -> {
                    char lowerCH = Character.toLowerCase(CH);
                    if(digit() || lowerCH>='a' && lowerCH<='f'){
                        add();
                        state = N16;
                    }
                    else if(lowerCH == 'h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else if(CH=='\t' || CH== ' '){
                        gc();
                        put(TN);
                        variable_types.put(z,"int");
                        out(3,z);
                        state = H;
                    }
                    else if(TlCheck()){
                        look(TN);
                        if(z!=0) {
                            out(3, z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");;
                            out(3, z);
                            state = H;
                        }
                    }
                    else {
                        state = ER;
                    }
                }
                case D -> {
                    char lowerCH = Character.toLowerCase(CH);
                    if (lowerCH=='h'){
                        add();
                        gc();
                        state = HX;
                    }
                    else if (digit()||(lowerCH>='a'&&lowerCH<='f')) {
                        add();
                        gc();
                        state = N16;
                    }
                    else if(CH=='\t' || CH== ' '){
                        gc();
                        put(TN);
                        variable_types.put(z,"int");
                        out(3,z);
                        state = H;
                    }
                    else if(TlCheck()){
                        look(TN);
                        if(z!=0) {
                            out(3, z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");
                            out(3, z);
                            state = H;
                        }
                    }
                    else {
                        state = ER;
                    }
                }
                case O, HX -> {
                    if(CH == ' '||CH=='\t' || CH =='/'){
                        look(TN);
                        if(z!=0) {
                            out(3, z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");
                            out(3, z);
                            state = H;
                        }

                    }
                    else if(TlCheck()){
                        look(TN);
                        if(z!=0) {
                            out(3, z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"int");
                            out(3, z);
                            state = H;
                        }
                    }
                    else {
                        state = ER;
                    }
                }
                case VESH -> {
                    while ((digit()&& canread)){
                        add();
                        gc();
                    }
                    char lowerCH = Character.toLowerCase(CH);
                    if(lowerCH == 'e'){
                        add();
                        gc();
                        state = NEF;
                    }
                    else if(CH == ' '||CH=='\t'){
                        gc();
                        look(TN);
                        if(z!=0){
                            put(TN);
                            out(3,z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"float");
                            out(3,z);
                            state = H;
                        }
                    }
                    else if(TlCheck()||CH=='/'){
                        look(TN);
                        if(z!=0) {
                            out(3, z);
                            state = H;
                        }else {
                            put(TN);
                            variable_types.put(z,"float");
                            out(3, z);
                            state = H;
                        }
                    }
                    else {
                        state = ER;
                    }
                }
                case NEF1 -> {
                    char lowerCH = Character.toLowerCase(CH);
                    if(CH=='+'||CH=='-'){
                        add();
                        gc();
                        while (digit()){
                            add();
                            gc();
                        }
                        if(CH == ' '|| CH=='\t'){
                            gc();
                            put(TN);
                            variable_types.put(z,"float");
                            out(3,z);
                            state = H;
                        }else if(TlCheck()){
                            look(TN);
                            if(z!= 0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3,z);
                                state = H;
                            }
                        }else {
                            state = ER;
                        }
                    }else if(digit()){
                        while (digit()){
                            add();
                            gc();
                        }
                        if(CH == ' '&& CH=='\t'){
                            gc();
                            look(TN);
                            if(z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3, z);
                                state = H;
                            }
                        }else if(TlCheck()){
                            look(TN);
                            if(z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3, z);
                                state = H;
                            }
                            gc();
                        }else {
                            state = ER;
                        }
                    }
                }
                case NEF -> {
                    char lowerCH = Character.toLowerCase(CH);
                    if(CH=='+'||CH=='-'){
                        add();
                        gc();
                        while (digit()){
                            add();
                            gc();
                            lowerCH = Character.toLowerCase(CH);
                        }
                        lowerCH = Character.toLowerCase(CH);
                        if(CH == ' '|| CH=='\t'){
                            gc();
                            look(TN);
                            if(z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3,z);
                                state = H;
                            }
                        }else if(TlCheck()){
                            look(TN);
                            if(z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3, z);
                                state = H;
                            }
                        }else {
                            state = ER;
                        }
                    }
                    else if(digit()){
                        while (digit()){
                            add();
                            gc();
                        }
                        lowerCH = Character.toLowerCase(CH);
                        if (lowerCH=='h'){
                            add();
                            gc();
                            state = HX;
                        } else if (lowerCH>='a'&&lowerCH<='f') {
                            add();
                            gc();
                            state =N16;
                        }
                        else if (CH == ' '||CH == '\t'){
                            gc();
                            look(TN);
                            if (z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3,z);
                                state = H;
                            }
                        }else if(TlCheck()){
                            look(TN);
                            if(z!=0) {
                                out(3, z);
                                state = H;
                            }else {
                                put(TN);
                                variable_types.put(z,"float");
                                out(3, z);
                                state = H;
                            }
                        }else {
                            state = ER;
                        }

                    }
                }
                case C1 -> {
                    if(CH == '*'&&canread){
                        gc();
                        state = C2;
                    }else {
                        state = ER;
                    }
                }
                case C2 -> {
                    while (canread&& CH!='*'){
                        gc();
                    }
                    if (CH == '*'){
                        gc();
                        state = C3;
                    }
                    else if(!canread){
                        state = ER;
                    }
                }
                case C3 -> {
                    if (CH == '/'){
                        gc();
                        state = C4;
                    }
                    else if(!canread){
                        state = ER;
                    }
                    else {
                        state = C2;
                    }
                }
                case C4 -> {
                    if(canread){
                        state = H;
                    }else {
                        state = ER;
                    }
                }
                case AND1 -> {
                    if (CH =='&'){
                        state = AND2;
                    }else {
                        state = ER;
                    }
                }
                case AND2 -> {
                    out(2,12);
                    gc();
                    state = H;
                }
                case OR1 -> {
                    if (CH == '|'){
                        state = OR2;
                    }else {
                        state =ER;
                    }
                }
                case OR2 -> {
                    out(2,9);
                    gc();
                    state = H;
                }

                case ER -> {
                    fileInputStream.close();
                    throw new Exception("ER "+S);
                }
                case V ->{
                    fileInputStream.close();
                    FileOutputStream outputStream = new FileOutputStream("src/main/resources/lexem2.txt");

                    // Преобразование списка в строку без скобок и запятых
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Token token : lexemes) {
                        stringBuilder.append(token.toString()); // Добавляем токены с пробелом между ними
                    }
                    String tokenString = stringBuilder.toString().trim(); // Убираем лишний пробел в конце

                    // Запись в файл
                    outputStream.write(tokenString.getBytes());
                    outputStream.close();
                }
            }
        }
        if (state == ER){
            fileInputStream.close();
            throw new Exception("Лексическая ошибка: Встретилась ошибка в "+S+" неизвестный символ "+CH);
        }
    }


    static void gc() throws IOException {
        int read = fileInputStream.read();
        if (read != -1) {
            CH = (char) read;
            canread = true;
        } else {
            canread = false;
        }
    }
    static boolean let(){
        if(Character.isLetter(CH)){
            return true;
        }else {
            return false;
        }
    }
    static boolean digit(){
        if(Character.isDigit(CH)){
            return true;
        }else {
            return false;
        }
    }
    static void add(){
        S += CH;
    }
    static void nill(){
        S = "";
    }
    static void put(HashMap<String, Integer> table){
        if(!table.containsKey(S)){
            z = table.size() +1;
            table.put(S,z);
        }else {
            z = table.get(S);
        }
    }


    public static String findKeyByValue(HashMap<String, Integer> map, int value) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey(); // Возвращаем ключ, если значение совпадает
            }
        }
        return ""; // Возвращаем пустую строку, если значение не найдено
    }

    static void look(HashMap<String, Integer> table){
        if(table.containsKey(S)){
            z = table.get(S);
        }else {
            z = 0;
        }
    }
    static void out(Integer tableNumb, Integer valueNumb) {
        printLex(tableNumb, valueNumb);
        Token token = new Token(tableNumb.toString(),valueNumb.toString());
        lexemes.add(token);
    }

    static boolean TlCheck(){
        if(TL.containsKey(String.valueOf(CH))){
            return true;
        }else {
            return false;
        }
    }

    static void printLex(Integer t, Integer v){
        HashMap<String,Integer> table = null;
        switch (t){
            case 1 -> {
                table = TW;
                break;
            }
            case 2 -> {
                table = TL;
                break;
            }
            case 3 -> {
                table = TN;
                break;
            }
            case 4 -> {
                table = TI;
                break;
            }
        }
        String key = findKeyByValue(table,v);
        if(!key.isEmpty()){
            System.out.println("out: ("+ t + ","+ v +"):" + key);
        }else {
            System.out.println("out: "+ t + ","+ v +"):" + "key is not found");
        }
    }

}
