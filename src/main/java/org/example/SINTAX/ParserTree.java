package org.example.SINTAX;

import org.example.LEXER.Lexer;
import org.example.Token;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserTree {

    private List<Token> tokens;
    public boolean end = false;
    private int currentIndex;
    private String currentToken;
    private String previousToken;
    private FileInputStream fileInputStream;
    public HashMap<String, Integer> TW;
    public HashMap<String, Integer> TL;
    public HashMap<String, Integer> TI;
    public HashMap<String, Integer> TN;

    public ParserTree(String tokenFile) throws IOException {
        this.tokens = new ArrayList<>();
        this.currentIndex = 0;
        this.currentToken = null;
        this.previousToken = null;
        this.TW = Lexer.TW;
        this.TI = Lexer.TI;
        this.TL = Lexer.TL;
        this.TN = Lexer.TN;
        try {
            fileInputStream = new FileInputStream(tokenFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void Parse() throws IOException {
        tokens = gc(fileInputStream);
        currentIndex = 0;
        Programm();  // Вызываем процедуру программы
    }

    public static List<Token> gc(FileInputStream filePath) {
        List<Token> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)"); // Регулярное выражение для поиска пар вида (a,b)

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePath))) {
            String line = reader.readLine(); // Считываем строку из потока
            if (line != null) {
                Matcher matcher = pattern.matcher(line);

                // Ищем все совпадения в строке
                while (matcher.find()) {
                    int first = Integer.parseInt(matcher.group(1)); // Первая группа (a)
                    int second = Integer.parseInt(matcher.group(2)); // Вторая группа (b)
                    tokens.add(new Token(first, second));
                }
            }
            filePath.close();
            System.out.println(tokens);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении потока: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка при преобразовании числа: " + e.getMessage());
            System.exit(1);
        }

        return tokens;
    }

    public void gl() {

        if (currentIndex < tokens.size() - 1) {
            previousToken = tokens.get(currentIndex).toString();
            currentIndex++;
            currentToken = tokens.get(currentIndex).toString();

        } else {
            throw new IndexOutOfBoundsException("Достигнут конец списка токенов.");
        }

    }

    public void Programm() {
        //<программа>::= begin var <описание> {; <оператор>} end
        System.out.println(tokens.get(currentIndex).toString());
        if (tokens.get(currentIndex).toString().equals("(1,2)")) { // begin
            gl(); // Переход к следующему токену
            if (tokens.get(currentIndex).toString().equals("(1,3)")) { // var
                gl();
                description();
                gl();
            } else {
                System.out.println("Нету токена var"+tokens.get(currentIndex).toString());
            }
            while (!tokens.get(currentIndex).toString().equals("(1,1)")) {
                if (currentToken.equals("(2,18)")) {
                    compoundOperator();
                }else {
                    System.err.println("Ожидалось ;, а получили " + currentToken);
                    System.err.println(previousToken+currentToken);
                    System.exit(1);
                }
            }
            if (tokens.get(currentIndex).toString().equals("(1,1)")){
                System.out.println("Закончено");
            }else {
                System.err.println("Нету end");
                System.exit(1);
            }
        } else {
            System.err.println("Нету токена begin");
            System.exit(1);
        }
    }
    public void description(){
        //<описание>::= dim <идентификатор> {, <идентификатор> } <тип>
        if(tokens.get(currentIndex).toString().equals("(1,4)")){//dim
            gl();
            identificator();
            gl();
            while (tokens.get(currentIndex).toString().equals("(2,15)")){// ,
                if(tokens.get(currentIndex).toString().equals("(2,15)")){
                    gl();
                }
                identificator();
                gl();
            }
            type();
        }else {
            System.err.println("Вмессто dim "+ tokens.get(currentIndex).toString());
            System.exit(1);
        }
    }
    public void identificator(){
        //<идентификатор>::= <буква> {<буква> | <цифра>}
        if(tokens.get(currentIndex).getTableid().equals("4")){
            if (TI.containsValue(Integer.valueOf(tokens.get(currentIndex).getValue()))){
                System.out.println("Идентификатор " +tokens.get(currentIndex).toString());
            }else {
                System.err.println("Такого нет идентификатораа " + tokens.get(currentIndex).toString());
                System.exit(1);
            }
        }else {
            System.err.println("Не идентификатор");
            System.exit(1);
        }
    }
    public void type(){
        //<тип>::= int | float | bool
        if(tokens.get(currentIndex).toString().equals("(1,5)")){
            System.out.println("int");
        }else if(tokens.get(currentIndex).toString().equals("(1,6)")){
            System.out.println("float");
        }else if(tokens.get(currentIndex).toString().equals("(1,7)")){
            System.out.println("bool");
        }else {
            System.err.println("Не тип данных");
            System.exit(1);
        }
    }
    public void operator() {
            /*<оператор>::= <составной> | <присваивания> | <условный> |
            <фиксированного_цикла> | <условного_цикла> | <ввода> |
            <вывода>*/
        gl();
        if (tokens.get(currentIndex).toString().equals("(1,8)")) { // if
            conditionalOperator(); // Разбор условного оператора
        }else if(tokens.get(currentIndex).toString().equals("(1,13)")){//for
            fixedOperator();
        }
        else if(tokens.get(currentIndex).toString().equals("(1,14)")){//while
            conditionalloop();
        }
        else if (tokens.get(currentIndex).getTableid().equals("4")) { // идентификатор
            assignmentOperator(); // Разбор оператора присваивания
        } else if (currentToken.equals("(1,12)")) { // write
            outputOperator(); // Разбор оператора вывода
        } else if (currentToken.equals("(1,11)")) { // read
            inputOperator(); // Разбор оператора ввода
        } else {
            System.err.println("Неизвестный оператор: " + tokens.get(currentIndex));
            System.exit(1);
        }
    }
    public void fixedOperator(){//фикссированный
        //<фиксированного_цикла>::= for <присваивания> to <выражение> do
        //<оператор>
        gl();
        System.out.println("Фиксированный");
        assignmentOperator();
        if (currentToken.equals("(1,16)")){//to
            gl();
            expression();
            if(currentToken.equals("(1,15)")){//do
                operator();
            }else {
                System.err.println("Ожидалось do, а получили "+ tokens.get(currentIndex).toString());
                System.exit(1);
            }
        }else {
            System.err.println("Ожидалось to, а получили "+ tokens.get(currentIndex).toString());
            System.exit(1);
        }
    }

    public void compoundOperator() {
        //<составной>::= <оператор> { ( : | перевод строки) <оператор> }
        operator(); // Обработка первого оператора
        if((currentToken.equals("(2,16)") || // ":" (код 16)
                currentToken.equals("(2,13)"))) { // перевод строки (код 13))
            while (currentToken.equals("(2,16)") || // ":" (код 16)
                    currentToken.equals("(2,13)")) { // перевод строки (код 13)
                        operator(); // Обрабатываем следующий оператор
            }
        }
    }
    public void conditionalOperator(){//условный
        //<условный>::= if <выражение> then <оператор> [ else <оператор>]
        gl();
        expression();
        if(currentToken.equals("(1,9)")){//then
            operator();
            if(currentToken.equals("(1,10)")){//else
                operator();
            }
        }else {
            System.err.println("Ожидался then, " + currentToken);
            System.exit(1);
        }
    }
    public void assignmentOperator(){//оператора присваивания
        //<присваивания>::= <идентификатор> as <выражение>
        identificator();
        gl();
        System.out.println(previousToken + " "+ currentToken);
        if(currentToken.equals("(2,17)")){//as
            gl();
            System.out.println("as "+currentToken.toString());
            expression();
        }else {
            System.err.println("Ожидалось as, а получили "+tokens.get(currentIndex).toString());
            System.exit(1);
        }
    }
    public void conditionalloop(){
        gl();
        expression();
        if(currentToken.equals("(1,15)")) {//do
            operator();
        }else {
            System.err.println("Ожидалось do" + tokens.get(currentIndex).toString());
            System.exit(1);
        }
    }
    public void outputOperator(){//вывод
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            expression();
            if (currentToken.equals("(2,15)")){
                while (currentToken.equals("(2,15)")){
                    gl();
                    expression();
                    if (currentToken.equals("(1,1)")) {
                        System.err.println("Конец программы");
                        System.exit(1);
                        break;
                    }else if(tokens.isEmpty()){
                        System.err.println("Больше токенов нет");
                        System.exit(1);
                    }
                }
            }else {
                if(tokens.get(currentIndex).toString().equals("(2,20)")){
                    gl();
                }
            }

        }else {
            gl();
        }

    }
    public void inputOperator(){
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            identificator();
            gl();
            if (currentToken.equals("(2,15)")){//,
                while (currentToken.equals("(2,15)")){
                    identificator();
                    if (currentToken.equals("(1,1)")) {
                        System.err.println("Конец программы");
                        System.exit(1);
                        break;
                    }else if(tokens.isEmpty()){
                        System.err.println("Больше токенов нет");
                        System.exit(1);
                    }
                }
            }else {
                if(tokens.get(currentIndex).toString().equals("(2,20)")){//)
                    gl();
                }
            }
        }else {
            gl();
        }
    }
    public void expression(){
        operand();
        if (isRelationOperator(tokens.get(currentIndex))) {
            while (isRelationOperator(tokens.get(currentIndex))) {
                gl();
                operand();
            }
        }
    }
    public void operand(){
        term();
        if(isAdditionOperator(tokens.get(currentIndex))) {
            while (isAdditionOperator(tokens.get(currentIndex))) {
                gl();
                term();
            }
        }
    }
    public void term(){
        factor();
        if(isMultiplicationOperator(tokens.get(currentIndex))) {
            while (isMultiplicationOperator(tokens.get(currentIndex))) {
                gl();
                factor();
            }
        }
    }
    public void factor() {
        if (tokens.get(currentIndex).getTableid().equals("4")) { // Идентификатор
            System.out.println("Множитель: идентификатор " + tokens.get(currentIndex));
            gl();
        } else if (tokens.get(currentIndex).getTableid().equals("3")) { // Число
            System.out.println("Множитель: число " + tokens.get(currentIndex));
            gl();
        } else if (tokens.get(currentIndex).toString().equals("(1,15)")) { // Логическая константа
            System.out.println("Множитель: логическая константа " + tokens.get(currentIndex));
            gl();
        } else if (tokens.get(currentIndex).toString().equals("(2,14)")) { // ^
            System.out.println("Унарная операция: ^");
            gl();
            factor(); // Рекурсивный вызов для обработки множителя
        } else if (tokens.get(currentIndex).toString().equals("(2,19)")) { // (
            gl(); // Пропускаем скобку
            expression(); // Рекурсивный вызов для выражения
            if (tokens.get(currentIndex).toString().equals("(2,20)")) { // )
                gl(); // Пропускаем закрывающую скобку
            } else {
                System.err.println("Ошибка: ожидается ')'");
                System.exit(1);
            }
        } else {
            System.err.println("Ошибка: неизвестный множитель " + tokens.get(currentIndex));
            System.exit(1);
        }
    }


    private boolean isRelationOperator(Token token) {
        return token.toString().equals("(2,1)") || // NEQ
                token.toString().equals("(2,2)") || // EQV
                token.toString().equals("(2,3)") || // LOWT
                token.toString().equals("(2,4)") || // LOWE
                token.toString().equals("(2,5)") || // GRT
                token.toString().equals("(2,6)");   // GRE
    }
    private boolean isAdditionOperator(Token token) {
        return token.toString().equals("(2,7)") || // add
                token.toString().equals("(2,8)") || // disa
                token.toString().equals("(2,9)");   // ||
    }
    private boolean isMultiplicationOperator(Token token) {
        return token.toString().equals("(2,10)") || // umn
                token.toString().equals("(2,11)") || // del
                token.toString().equals("(2,12)");   // &&
    }


}
