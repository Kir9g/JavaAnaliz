package org.example.SEMAMTIC;

import org.example.SINTAX.Node;

import java.util.*;

public class SemanticAnalyzer {
    private final Node parseTree;
    private final Set<String> initializedVariables = new HashSet<>(); // Множество инициализированных переменных
    private HashMap<Integer, String> TypesOfVariables = null;// Сопоставление переменных и их типов
    private String type;
    private final Set<String> declaration_identifier = new HashSet<>();
    public SemanticAnalyzer(Node parseTree, HashMap<Integer, String> varible_types) {
        this.parseTree = parseTree;
        this.TypesOfVariables =varible_types;
    }

    public void analyze() {
        visit(parseTree);
    }

    private void visit(Node node) {
        String nodeType = node.getNodeType();
        System.out.println(nodeType);
        switch (nodeType) {
            case "Programm":
                handleProgram(node);
                break;
            case "Description":
                handleDescription(node);
                break;
            case "AssignmentOperator":
                handleAssignment(node);
                break;
            case "compoundOperator":
                handleCompoundOperator(node);
                break;
            case "operator":
                handleOperator(node);
                break;
            case "outputOperator":
                handleOutputOperator(node);
                break;
            case "conditionalOperator":
                handleConditionalOperator(node);
                break;
            case "FixedOperator":
                handleFixedOperator(node);
                break;
            case "Conditionalloop":
                handleConditionalloop(node);
                break;
            default:
                throw new RuntimeException("Unknown node type: " + nodeType);
        }
    }

    private void handleConditionalloop(Node node) {
        if (node.getChildren().size()!=2){
            throw new IllegalStateException("Неправильная конструкция while"+ node);
        }

        String expression = evaluate_expression(node.getChildren().get(0));
        if (!expression.equals("bool")){
            throw new IllegalStateException("В while должно быть выражение типа bool, а не "+expression);
        }
        handleOperator(node.getChildren().get(1));
    }

    private void handleFixedOperator(Node node) {
        List<Node> childrens = node.getChildren();
        for (int i = 0;i<node.getChildren().size();i++) {
            if (i==0) {
                System.out.println("Fixed1");
                handleAssignment(childrens.get(0));
            } else if (i==1) {
                System.out.println("Fixed2");
                String expression = evaluate_expression(childrens.get(1));
                if (expression != "bool") {
                    throw new IllegalStateException("Выражение в фиксированном операторе не bool, а " + expression);
                }
            } else if (i==2) {
                System.out.println("Fixed3");
                handleOperator(childrens.get(2));
            }
        }
    }

    private void handleConditionalOperator(Node node) {
        String Exression = evaluate_expression(node.getChildren().get(0));
        if (Exression!= "bool"){
            throw new IllegalStateException("Выражение не bool, a "+Exression);
        }
        for(int i = 1;i<node.getChildren().size();i++){
            visit(node.getChildren().get(i));
        }
    }

    private void handleOutputOperator(Node node) {
        System.out.println("Выводной оператор ");
        for (int i=0;i<node.getChildren().size();i++){
           evaluate_expression(node.getChildren().get(i));
        }
    }

    private void handleCompoundOperator(Node node) {
        for (Node child : node.getChildren()) {
            visit(child);
        }
    }

    private void handleOperator(Node node) {
        for (Node child : node.getChildren()) {
            visit(child);
        }
    }
    //<присваивания>::= <идентификатор> as <выражение>
    private void handleAssignment(Node node) {
        List<Node> children = node.getChildren();
        if (children.size()!=2){
            throw new IllegalStateException("Ошибка синтаксиса: оператор присваивания должен иметь ровно 2 дочерних узла, но найдено: " + children.size());
        }
        Node identifierNode = children.get(0);
        Node expressionNode = children.get(1);

        if (!declaration_identifier.contains(identifierNode.getValue())){
            throw new IllegalStateException("Идентификатор не был объявлен "+identifierNode.getValue());
        }
        String left = checkType(identifierNode);
        String right = evaluate_expression(expressionNode);
        System.out.println("Левая сторона: " + left);
        System.out.println("Правая сторона: " + right);
        if (left!=right){
            throw new IllegalStateException("Несоответствие типов: слева "+left+", справа "+right+"");
        }
        System.out.println("Одинковые типы "+left+" и "+right);
    }
    //programm
    private void handleProgram(Node node) {
        for (Node child : node.getChildren()) {
            visit(child);
        }
    }

    //описание
    private void handleDescription(Node node) {
        for (Node child : node.getChildren()) {
            if (child.getNodeType().equals("Identifier")){
                if (!declaration_identifier.contains(child.getValue())) {
                    declaration_identifier.add(child.getValue());
                }else {
                    throw new IllegalStateException("Данный идентификатор уже был инициализирован"+child.getValue());
                }
            }else if(child.getNodeType().equals("Type")){
                type = child.getChildren().get(0).getNodeType();
                System.out.println("type = "+type);
            }
        }
    }
    private String checkType(Node node){

        String value = node.getValue();
        initializedVariables.add(value);
        System.out.println(initializedVariables);

        return type;
    }
    private String evaluate_expression(Node node){
        System.out.println(node.getNodeType());
        if (node.getNodeType().equals("Expression")){
            System.out.println(node.getNodeType());
            //isRelationOperator
            if (node.getValue()==null){
                return evaluate_expression(node.getChildren().get(0));
            }else {
                return evaluate_operation(node);
            }

        }else if (node.getNodeType().equals("Operand")){
            //MultiplicativeOperation
            if (node.getValue()==null){
                return evaluate_expression(node.getChildren().get(0));
            }else {
                return evaluate_operation(node);
            }
        }else if(node.getNodeType().equals("Term")){
            if (node.getValue()==null){
                return evaluate_expression(node.getChildren().get(0));
            }else {
                return evaluate_operation(node);
            }
        }else if (node.getNodeType().equals("Factor")){
            return evaluate_expression(node.getChildren().get(0));
        } else {
            return getType(node);
        }
    }
    private String evaluate_operation(Node node){
        String OperandType = evaluate_expression(node.getChildren().get(0));
        for (int i = 1;i<node.getChildren().size();i++){
            String childType = evaluate_expression(node.getChildren().get(i));
            if (node.getNodeType().equals("Operand") &&node.getValue().equals("(2,11")){
                return "float";
            }
            if (childType!=OperandType){
                throw new IllegalStateException("Несоответствие типов в операции "+ node.getNodeType() +": "+OperandType+" и "+ childType+"");
            }
        }
        return node.getNodeType().equals("Expression")&& node.getValue()!=null ? "bool" : OperandType;
    }

    private String getType(Node node){
        if (node.getNodeType().equals("Identifier")){
            return type;
        }else if (node.getNodeType().equals("Number")){
            String value = node.getValue();
            int key;
            try {
                key = Integer.parseInt(value.substring(value.lastIndexOf(',') + 1, value.length() - 1));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Не удалось извлечь номер ключа из значения: " + value, e);
            }
            // Проверяем, есть ли этот ключ в TypesOfVariables
            String variableType = TypesOfVariables.get(key);
            if (variableType == null) {
                throw new IllegalStateException("Тип для переменной с ключом " + key + " не найден.");
            }

            System.out.println(variableType +" variableType");
            System.out.println(value);
            return variableType;
        }
        return null;
    }

}
