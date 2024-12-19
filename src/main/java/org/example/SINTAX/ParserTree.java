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

    public Node parse() throws Exception {
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
//            throw new Exception("Ошибка при чтении потока: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            throw new Exception("Ошибка при преобразовании числа: " + e.getMessage());
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
            throw new IndexOutOfBoundsException("Синтаксическая ошибка:Достигнут конец списка токенов.");
        }

    }

    public Node Programm() throws Exception {
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
                throw new Exception("Синтаксическая ошибка: Ожидался var "+tokens.get(currentIndex).toString());
            }
            while (!tokens.get(currentIndex).toString().equals("(1,1)")) {
                if (currentToken.equals("(2,18)")) {
                    Node operatorNode = compoundOperator();
                    programmNode.addChild(operatorNode);
                }else {
                    throw new Exception("Синтаксическая ошибка: Ожидалось ; , а получили " + previousToken+currentToken);
                }
            }
            if (tokens.get(currentIndex).toString().equals("(1,1)")){

            }else {
                throw new Exception("Синтаксическая ошибка: Ожидался  End");
            }
        } else {
            throw new Exception("Синтаксическая ошибка: Ожидался begin");
        }
        return programmNode;
    }
    public Node description() throws Exception {
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
            throw new Exception("Синтаксическая ошибка: Вмессто dim "+ tokens.get(currentIndex).toString());
        }
        return descriptionNode;
    }
    public Node identificator() throws Exception {
        //<идентификатор>::= <буква> {<буква> | <цифра>}
        if(tokens.get(currentIndex).getTableid().equals("4")){
            if (TI.containsValue(Integer.valueOf(tokens.get(currentIndex).getValue()))){
                return new Node("Identifier", currentToken.toString());
            }else {
                throw new Exception ("Синтаксическая ошибка: Нет такого идентификатора " + tokens.get(currentIndex).toString());
            }
        }else {
            throw new Exception ("Синтаксическая ошибка: Не идентификатор"+currentToken);
        }
    }
    public Node type() throws Exception {
        //<тип>::= int | float | bool
        Node typeNode = new Node("Type");
        if(tokens.get(currentIndex).toString().equals("(1,5)")){
            typeNode.addChild(new Node("int"));
        }else if(tokens.get(currentIndex).toString().equals("(1,6)")){
            typeNode.addChild(new Node("float"));
        }else if(tokens.get(currentIndex).toString().equals("(1,7)")){
            typeNode.addChild(new Node("bool"));
        }else {
            throw new Exception ("Синтаксическая ошибка: Не тип данных"+currentToken);
        }
        return typeNode;
    }
    public Node operator() throws Exception {
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
            throw new Exception ("Синтаксическая ошибка: Неизвестный оператор: " + tokens.get(currentIndex));
        }
        return operatorNode;
    }
    public Node fixedOperator() throws Exception {//фикссированный
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
                Node operatorNode = compoundOperator();
                fixedOperatorNode.addChild(operatorNode);
            }else {
                throw new Exception ("Синтаксическая ошибка: Ожидалось do, а получили "+ tokens.get(currentIndex).toString());
            }
        }else {
            throw new Exception ("Синтаксическая ошибка: Ожидалось to, а получили "+ tokens.get(currentIndex).toString());
        }
        return fixedOperatorNode;
    }

    public Node compoundOperator() throws Exception {
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
    public Node conditionalOperator() throws Exception {//условный
        //<условный>::= if <выражение> then <оператор> [ else <оператор>]
        gl();
        Node conditionalOperatornNode = new Node("conditionalOperator");
        Node expressionNode = expression();
        conditionalOperatornNode.addChild(expressionNode);
        if(currentToken.equals("(1,9)")){//then
            Node compoundOperatorNode = compoundOperator();
            conditionalOperatornNode.addChild(compoundOperatorNode);
            if(currentToken.equals("(1,10)")){//else
                Node compoundoperatorNode = compoundOperator();
                conditionalOperatornNode.addChild(compoundoperatorNode);
            }
        }else {
            throw new Exception ("Синтаксическая ошибка: Ожидался then, " + currentToken);
        }
        return conditionalOperatornNode;
    }
    public Node assignmentOperator() throws Exception {//оператора присваивания
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
            throw new Exception("Синтаксическая ошибка: Ожидалось as, а получили "+tokens.get(currentIndex).toString());
        }
        return assignmentOperatorNode;
    }
    public Node conditionalloop() throws Exception {
        Node conditionalloopNode = new Node("Conditionalloop");
        gl();
        Node expressionNode = expression();
        conditionalloopNode.addChild(expressionNode);
        if(currentToken.equals("(1,15)")) {//do
            Node operatorNode = compoundOperator();
            conditionalloopNode.addChild(operatorNode);
        }else {
            throw new Exception ("Синтаксическая ошибка: Ожидалось do" + tokens.get(currentIndex).toString());
        }
        return conditionalloopNode;
    }
    public Node outputOperator() throws Exception {//вывод
        Node outNode  =  new Node("outputOperator");
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            Node expressionNode  = expression();
            outNode.addChild(expressionNode);
            if (currentToken.equals("(2,15)")){
                while (currentToken.equals("(2,15)")){
                    gl();
                    expressionNode  = expression();
                    outNode.addChild(expressionNode);
                }
                if(tokens.get(currentIndex).toString().equals("(2,20)")){
                    gl();
                }else {
                    throw new Exception("Синтаксическая ошибка: Ожидался ), а приняли "+currentToken);
                }
            }else {
                if(tokens.get(currentIndex).toString().equals("(2,20)")){
                    gl();
                }else {
                    throw new Exception("Синтаксическая ошибка: Ожидался ), а приняли "+currentToken);
                }
            }

        }else {
            throw new Exception("Синтаксическая ошибка: Ожидался (, а приняли"+currentToken);
        }
        return outNode;
    }
    public Node inputOperator() throws Exception {
        Node inputNode = new Node("inputOperator");
        gl();
        if(currentToken.equals("(2,19)")){//(
            gl();
            Node identificatorNode = identificator();
            inputNode.addChild(identificatorNode);
            gl();
            if (currentToken.equals("(2,15)")){//,
                while (currentToken.equals("(2,15)")) {
                    gl();
                    identificatorNode = identificator();
                    gl();
                    inputNode.addChild(identificatorNode);
                }if(tokens.get(currentIndex).toString().equals("(2,20)")){//)
                    gl();
                }else {
                    throw new Exception("Синтаксическая ошибка: Ожидался ), а приняли "+currentToken);
                }
            }else {
                if(tokens.get(currentIndex).toString().equals("(2,20)")){//)
                    gl();
                }else {
                    throw new Exception("Синтаксическая ошибка: Ожидался ), а приняли "+currentToken);
                }
            }
        }else {
                throw new Exception("Синтаксическая ошибка: Ожидался (, а приняли"+currentToken);
        }
        return inputNode;
    }
    public Node expression() throws Exception {
        //выражение
        Node expressionNode = new Node("Expression");
        Node operandNode = operand();
        expressionNode.addChild(operandNode);
        if (isRelationOperator(tokens.get(currentIndex))) {
            expressionNode.setValue(tokens.get(currentIndex).toString());
            while (isRelationOperator(tokens.get(currentIndex))) {
                Node RelationOperator = new Node("RelationOperator",currentToken);
                gl();
                Node Operandd = operand();
                RelationOperator.addChild(Operandd);
                expressionNode.addChild(RelationOperator);
            }
        }
        return expressionNode;
    }
    public Node operand() throws Exception {
        //операнд
        Node operandNode = new Node("Operand");
        Node termNode = term();
        operandNode.addChild(termNode);//left
        if(isAdditionOperator(tokens.get(currentIndex))) {
            operandNode.setValue(currentToken);
            while (isAdditionOperator(tokens.get(currentIndex))) {
                Node AdditionOperator = new Node("AdditionOperator",currentToken);
                gl();
                termNode = term();
                AdditionOperator.addChild(termNode);
                operandNode.addChild(AdditionOperator);
            }
        }
        return operandNode;
    }
    public Node term() throws Exception {
        //слагаемое
        Node termNode = new Node("Term");
        Node factorNode = factor();
        termNode.addChild(factorNode);//первое слагаемое
        if(isMultiplicationOperator(tokens.get(currentIndex))) {
            termNode.setValue(currentToken);
            while (isMultiplicationOperator(tokens.get(currentIndex))) {
                Node MultiplicationOperator = new Node("MultiplicationOperator", currentToken);
                gl();
                factorNode = factor();
                MultiplicationOperator.addChild(factorNode);
                termNode.addChild(MultiplicationOperator);
            }
        }
        return termNode;
    }
    public Node factor() throws Exception {
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
            Node booleanNode = new Node("bool");
            booleanNode.setValue(currentToken);
            factorNode.addChild(booleanNode);
            gl();
        } else if (tokens.get(currentIndex).toString().equals("(2,14)")) { // ^
            factorNode.setValue(currentToken);
            gl();
            factorNode.addChild(factor());// Рекурсивный вызов для обработки множителя
        } else if (tokens.get(currentIndex).toString().equals("(2,19)")) { // (
            gl(); // Пропускаем скобку
            factorNode.addChild(expression());// Рекурсивный вызов для выражения
            if (tokens.get(currentIndex).toString().equals("(2,20)")) { // )
                gl(); // Пропускаем закрывающую скобку
            } else {
                throw new Exception("Синтаксическая ошибка: Ошибка: ожидается ')', а получили"+currentToken);
            }
        } else {
            throw new Exception ("Синтаксическая ошибка: Ошибка: неизвестный множитель " + tokens.get(currentIndex));
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
    public void saveAstToFile(Node rootNode, String filePath) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(rootNode.getTreeAsString(0));
        } catch (IOException e) {
            throw new Exception ("Синтаксическая ошибка: Ошибка при сохранении AST в файл: " + e.getMessage());
        }
    }


}
