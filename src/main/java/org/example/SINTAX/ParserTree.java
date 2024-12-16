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
    public ParserTree(List<Token> token) throws IOException {
        this.tokens = token;
        this.currentIndex = 0;
        this.currentToken = null;
        this.previousToken = null;
        this.TW = Lexer.TW;
        this.TI = Lexer.TI;
        this.TL = Lexer.TL;
        this.TN = Lexer.TN;

    }

    public Node parse() throws IOException {
//        tokens = gc(fileInputStream);
        currentIndex = 0;
        Node programmNode = Programm();//Вызываем процедуру программы

        return programmNode;
    }

//    public static List<Token> gc(FileInputStream filePath) {
//        List<Token> tokens = new ArrayList<>();
//        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)"); // Регулярное выражение для поиска пар вида (a,b)
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePath))) {
//            String line = reader.readLine(); // Считываем строку из потока
//            if (line != null) {
//                Matcher matcher = pattern.matcher(line);
//
//                // Ищем все совпадения в строке
//                while (matcher.find()) {
//                    int first = Integer.parseInt(matcher.group(1)); // Первая группа (a)
//                    int second = Integer.parseInt(matcher.group(2)); // Вторая группа (b)
//                    tokens.add(new Token(first, second));
//                }
//            }
//            filePath.close();
//            System.out.println(tokens);
//        } catch (IOException e) {
//            System.err.println("Ошибка при чтении потока: " + e.getMessage());
//            System.exit(1);
//        } catch (NumberFormatException e) {
//            System.err.println("Ошибка при преобразовании числа: " + e.getMessage());
//            System.exit(1);
//        }
//
//        return tokens;
//    }

    public void gl() {

        if (currentIndex < tokens.size() - 1) {
            previousToken = tokens.get(currentIndex).toString();
            currentIndex++;
            currentToken = tokens.get(currentIndex).toString();

        } else {
            throw new IndexOutOfBoundsException("Достигнут конец списка токенов.");
        }

    }

    public Node Programm() {
        //<программа>::= begin var <описание> {; <оператор>} end
        Node programmNode = new Node("Programm");
        if (tokens.get(currentIndex).toString().equals("(1,2)")) { // begin
            gl(); // Переход к следующему токену
            if (tokens.get(currentIndex).toString().equals("(1,3)")) { // var
                gl();
                Node description = description();
                programmNode.addChild(description);
                gl();
            } else {
                System.out.println("Нету токена var"+tokens.get(currentIndex).toString());
            }
            while (!tokens.get(currentIndex).toString().equals("(1,1)")) {
                if (currentToken.equals("(2,18)")) {
                    Node operatorNode = compoundOperator();
                    programmNode.addChild(operatorNode);
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
        return programmNode;
    }
    public Node description(){
        //<описание>::= dim <идентификатор> {, <идентификатор> } <тип>
        Node descriptionNode = new Node("Description");
        if(tokens.get(currentIndex).toString().equals("(1,4)")){//dim
            gl();
            Node identifierNode = identificator();
            descriptionNode.addChild(identifierNode);
            gl();
            while (tokens.get(currentIndex).toString().equals("(2,15)")){// ,
                if(tokens.get(currentIndex).toString().equals("(2,15)")){
                    gl();
                }
                identifierNode = identificator();
                descriptionNode.addChild(identifierNode);
                gl();
            }
            Node typeNode = type();
            descriptionNode.addChild(typeNode);
        }else {
            System.err.println("Вмессто dim "+ tokens.get(currentIndex).toString());
            System.exit(1);
        }
        return descriptionNode;
    }
    public Node identificator(){
        //<идентификатор>::= <буква> {<буква> | <цифра>}
        Node identificatorNode = new Node("Identifier");
        if(tokens.get(currentIndex).getTableid().equals("4")){
            if (TI.containsValue(Integer.valueOf(tokens.get(currentIndex).getValue()))){
                return new Node("Identifier", currentToken.toString());
            }else {
                System.err.println("Такого нет идентификатораа " + tokens.get(currentIndex).toString());
                System.exit(1);
            }
        }else {
            System.err.println("Не идентификатор");
            System.exit(1);
        }
        return null;
    }
    public Node type(){
        //<тип>::= int | float | bool
        Node typeNode = new Node("Type");
        if(tokens.get(currentIndex).toString().equals("(1,5)")){
            typeNode.addChild(new Node("int"));
        }else if(tokens.get(currentIndex).toString().equals("(1,6)")){
            typeNode.addChild(new Node("float"));
        }else if(tokens.get(currentIndex).toString().equals("(1,7)")){
            typeNode.addChild(new Node("bool"));
        }else {
            System.err.println("Не тип данных");
            System.exit(1);
        }
        return typeNode;
    }
    public Node operator() {
            /*<оператор>::= <составной> | <присваивания> | <условный> |
            <фиксированного_цикла> | <условного_цикла> | <ввода> |
            <вывода>*/
        Node operatorNode = new Node("operator");
        gl();
        if (tokens.get(currentIndex).toString().equals("(1,8)")) { // if
            Node conditionalOperatorNode = conditionalOperator();// Разбор условного оператора
            operatorNode.addChild(conditionalOperatorNode);
        }else if(tokens.get(currentIndex).toString().equals("(1,13)")){//for
            Node fixedOperarNode = fixedOperator();
            operatorNode.addChild(fixedOperarNode);
        }
        else if(tokens.get(currentIndex).toString().equals("(1,14)")){//while
            Node conditionalloopNode = conditionalloop();
            operatorNode.addChild(conditionalloopNode);
        }
        else if (tokens.get(currentIndex).getTableid().equals("4")) { // идентификатор
            Node assignmentOperatorNode = assignmentOperator(); // Разбор оператора присваивания
            operatorNode.addChild(assignmentOperatorNode);
        } else if (currentToken.equals("(1,12)")) { // write
            Node outputOperatorNode = outputOperator(); // Разбор оператора вывода
            operatorNode.addChild(outputOperatorNode);
        } else if (currentToken.equals("(1,11)")) { // read
            Node inputOperatorNode = inputOperator(); // Разбор оператора ввода
            operatorNode.addChild(inputOperatorNode);
        } else {
            System.err.println("Неизвестный оператор: " + tokens.get(currentIndex));
            System.exit(1);
        }
        return operatorNode;
    }
    public Node fixedOperator(){//фикссированный
        //<фиксированного_цикла>::= for <присваивания> to <выражение> do
        //<оператор>
        gl();
        Node fixedOperatorNode = new Node("FixedOperator");
        Node assignmentOperatorNode = assignmentOperator();
        fixedOperatorNode.addChild(assignmentOperatorNode);
        if (currentToken.equals("(1,16)")){//to
            gl();
            Node expressionNode = expression();
            fixedOperatorNode.addChild(expressionNode);
            if(currentToken.equals("(1,15)")){//do
                Node operatorNode = operator();
                fixedOperatorNode.addChild(operatorNode);
            }else {
                System.err.println("Ожидалось do, а получили "+ tokens.get(currentIndex).toString());
                System.exit(1);
            }
        }else {
            System.err.println("Ожидалось to, а получили "+ tokens.get(currentIndex).toString());
            System.exit(1);
        }
        return fixedOperatorNode;
    }

    public Node compoundOperator() {
        //<составной>::= <оператор> { ( : | перевод строки) <оператор> }
        Node compoundOperatorNode = null;
        Node operatorNode = operator();// Обработка первого оператора
        if((currentToken.equals("(2,16)") || // ":" (код 16)
                currentToken.equals("(2,13)"))) { // перевод строки (код 13))
            compoundOperatorNode = new Node("compoundOperator");
            compoundOperatorNode.addChild(operatorNode);
            while (currentToken.equals("(2,16)") || // ":" (код 16)
                    currentToken.equals("(2,13)")) { // перевод строки (код 13)
                operatorNode = operator(); // Обрабатываем следующий оператор
                compoundOperatorNode.addChild(operatorNode);
            }
            return compoundOperatorNode;
        }else {
            return operatorNode;
        }
    }
    public Node conditionalOperator(){//условный
        //<условный>::= if <выражение> then <оператор> [ else <оператор>]
        gl();
        Node conditionalOperatornNode = new Node("conditionalOperator");
        Node expressionNode = expression();
        conditionalOperatornNode.addChild(expressionNode);
        if(currentToken.equals("(1,9)")){//then
            Node operatorNode = operator();
            conditionalOperatornNode.addChild(operatorNode);
            if(currentToken.equals("(1,10)")){//else
                operatorNode = operator();
                conditionalOperatornNode.addChild(operatorNode);
            }
        }else {
            System.err.println("Ожидался then, " + currentToken);
            System.exit(1);
        }
        return conditionalOperatornNode;
    }
    public Node assignmentOperator(){//оператора присваивания
        //<присваивания>::= <идентификатор> as <выражение>
        Node assignmentOperatorNode = new Node("AssignmentOperator");
        Node identificatorNode = identificator();
        assignmentOperatorNode.addChild(identificatorNode);
        gl();
        if(currentToken.equals("(2,17)")){//as
            gl();
            Node expressionNode = expression();
            assignmentOperatorNode.addChild(expressionNode);
        }else {
            System.err.println("Ожидалось as, а получили "+tokens.get(currentIndex).toString());
            System.exit(1);
        }
        return assignmentOperatorNode;
    }
    public Node conditionalloop(){
        Node conditionalloopNode = new Node("Conditionalloop");
        gl();
        Node expressionNode = expression();
        conditionalloopNode.addChild(expressionNode);
        if(currentToken.equals("(1,15)")) {//do
            Node operatorNode = operator();
            conditionalloopNode.addChild(operatorNode);
        }else {
            System.err.println("Ожидалось do" + tokens.get(currentIndex).toString());
            System.exit(1);
        }
        return conditionalloopNode;
    }
    public Node outputOperator(){//вывод
        Node outNode  =  new Node("outputOperator");
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            Node exressionNode  = expression();
            outNode.addChild(exressionNode);
            if (currentToken.equals("(2,15)")){
                while (currentToken.equals("(2,15)")){
                    gl();
                    exressionNode  = expression();
                    outNode.addChild(exressionNode);
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
        return outNode;
    }
    public Node inputOperator(){
        Node inputNode = new Node("inputOperator");
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            Node expressionNode = expression();
            inputNode.addChild(expressionNode);
            gl();
            if (currentToken.equals("(2,15)")){//,
                while (currentToken.equals("(2,15)")){
                    expressionNode = expression();
                    inputNode.addChild(expressionNode);
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
        return inputNode;
    }
    public Node expression(){
        Node expressionNode = new Node("Expression");
        Node operandNode = operand();
        expressionNode.addChild(operandNode);
        if (isRelationOperator(tokens.get(currentIndex))) {
            while (isRelationOperator(tokens.get(currentIndex))) {
                expressionNode.setValue(tokens.get(currentIndex).toString());
                gl();
                operandNode = operand();
                expressionNode.addChild(operandNode);
            }
        }
        return expressionNode;
    }
    public Node operand(){
        Node operandNode = new Node("Operand");
        Node termNode = term();
        operandNode.addChild(termNode);
        if(isAdditionOperator(tokens.get(currentIndex))) {
            while (isAdditionOperator(tokens.get(currentIndex))) {
                operandNode.setValue(tokens.get(currentIndex).toString());
                gl();
                termNode = term();
                operandNode.addChild(termNode);
            }
        }
        return operandNode;
    }
    public Node term(){
        Node termNode = new Node("Term");
        Node factorNode = factor();
        termNode.addChild(factorNode);
        if(isMultiplicationOperator(tokens.get(currentIndex))) {
            while (isMultiplicationOperator(tokens.get(currentIndex))) {
                termNode.setValue(tokens.get(currentIndex).toString());
                gl();
                factorNode = factor();
                termNode.addChild(factorNode);
            }
        }
        return termNode;
    }
    public Node factor() {
        //<множитель>::= <идентификатор> | <число> | <логическая_константа> | <унарная_операция> <множитель> | « (»<выражение>«)»
        Node factorNode = new Node("Factor");
        if (tokens.get(currentIndex).getTableid().equals("4")) { // Идентификатор
            Node identificatorNode = identificator();
            factorNode.addChild(identificatorNode);
            gl();
        } else if (tokens.get(currentIndex).getTableid().equals("3")) { // Число
            Node NumberNode = new Node("Number",currentToken);
            factorNode.addChild(NumberNode);
            gl();
        } else if (currentToken.equals("(1,17)")||currentToken.equals("(1,18)")){ // Логическая константа
            Node booleanNode = new Node("boolean");
            booleanNode.setValue(currentToken);
            factorNode.addChild(booleanNode);
            gl();
        } else if (tokens.get(currentIndex).toString().equals("(2,14)")) { // ^
            System.out.println("Унарная операция: ^");
            factorNode.setValue(currentToken);
            gl();
            factorNode.addChild(factor());// Рекурсивный вызов для обработки множителя
        } else if (tokens.get(currentIndex).toString().equals("(2,19)")) { // (
            gl(); // Пропускаем скобку
            factorNode.addChild(expression());// Рекурсивный вызов для выражения
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
        return factorNode;
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
    public void saveAstToFile(Node rootNode, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(rootNode.getTreeAsString(0));
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении AST в файл: " + e.getMessage());
        }
    }


}
