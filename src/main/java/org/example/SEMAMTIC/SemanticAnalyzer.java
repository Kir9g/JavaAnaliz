package org.example.SEMAMTIC;

import org.example.SINTAX.Node;
import org.example.Token;

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

    public void analyze() throws Exception {
        visit(parseTree);
//        System.out.println(initializedVariables);
//        System.out.println(TypesOfVariables);
    }

    private void visit(Node node) throws Exception {
        String nodeType = node.getNodeType();
//        System.out.println(nodeType);
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
            case "inputOperator":
                inputOperator(node);
                break;
            default:
                throw new Exception("Семантическая ошибка: Unknown node type: " + nodeType);
        }
    }

    private void handleConditionalloop(Node node) throws Exception {
        if (node.getChildren().size()!=2){
            throw new Exception("Семантическая ошибка: Неправильная конструкция while"+ node);
        }

        String expression = evaluate_expression(node.getChildren().get(0));
        if (!expression.equals("bool")){
            throw new Exception("Семантическая ошибка: В while должно быть выражение типа bool, а не "+expression);
        }
        handleOperator(node.getChildren().get(1));
    }

    private void handleFixedOperator(Node node) throws Exception {

        List<Node> childrens = node.getChildren();

        String operation = handleAssignment(childrens.get(0));

        String expression = evaluate_expression(childrens.get(1));

        if (operation!=expression){
            throw new Exception("Семантическая ошибка: Несоответствие типов в фиксированном цикле: операции "+operation+", выражения "+expression+"");
        }

        handleOperator(childrens.get(2));

    }

    private void handleConditionalOperator(Node node) throws Exception {

        String Expression = evaluate_expression(node.getChildren().get(0));

        if (Expression!= "bool"){
            throw new Exception("Семантическая ошибка: Выражение не bool, a "+Expression);
        }
        for(int i = 1;i<node.getChildren().size();i++){
            visit(node.getChildren().get(i));
        }
    }

    private void handleOutputOperator(Node node) throws Exception {

        for (int i=0;i<node.getChildren().size();i++){
           evaluate_expression(node.getChildren().get(i));
        }
    }

    private void handleCompoundOperator(Node node) throws Exception {

        for (Node child : node.getChildren()) {
            visit(child);
        }
    }

    private void handleOperator(Node node) throws Exception {

        for (Node child : node.getChildren()) {
            visit(child);
        }
    }
    //<присваивания>::= <идентификатор> as <выражение>
    private String handleAssignment(Node node) throws Exception {

        List<Node> children = node.getChildren();

        Node identifierNode = children.get(0);
        Node expressionNode = children.get(1);


        if (!declaration_identifier.contains(identifierNode.getValue())){
            throw new Exception("Семантическая ошибка: Идентификатор не был объявлен "+identifierNode.getValue());
        }
        String left = checkType(identifierNode);
        String right = evaluate_expression(expressionNode);

        if (left!=right){
            throw new Exception("Семантическая ошибка: Несоответствие типов: слева "+left+", справа "+right+"");
        }

        return left;
    }
    //programm
    private void handleProgram(Node node) throws Exception {

        for (Node child : node.getChildren()) {
            visit(child);
        }
    }

    //описание
    private void handleDescription(Node node) throws Exception {

        for (Node child : node.getChildren()) {
            if (child.getNodeType().equals("Identifier")){
                if (!declaration_identifier.contains(child.getValue())) {
                    declaration_identifier.add(child.getValue());
                }else {
                    throw new Exception("Семантическая ошибка: Данный идентификатор уже был объявлен"+child.getValue());
                }
            }else if(child.getNodeType().equals("Type")){
                type = child.getChildren().get(0).getNodeType();
            }
        }
    }
    private String checkType(Node node) throws Exception {
        if (declaration_identifier.contains(node.getValue())) {
            String value = node.getValue();
            initializedVariables.add(value);
            return type;
        }else {
            throw new Exception("Семантическая ошибка: Переменная не объявлена "+ node.getValue());
        }
    }

    private void inputOperator(Node node) throws Exception {
        for (Node child : node.getChildren()) {
            checkType(child);
        }
    }

    private String evaluate_expression(Node node) throws Exception {
        if (node.getNodeType().equals("Expression")&&node.getValue()==null
                ||node.getNodeType().equals("Operand")&&node.getValue()==null
                ||node.getNodeType().equals("Term")&&node.getValue()==null
                ||node.getNodeType().equals("Factor")&&node.getValue()==null){

            return evaluate_expression(node.getChildren().get(0));

        }else if(node.getNodeType().equals("Expression")&&node.getValue()!=null
                ||node.getNodeType().equals("Operand")&&node.getValue()!=null
                ||node.getNodeType().equals("Term")&&node.getValue()!=null
                ||node.getNodeType().equals("Factor")&&node.getValue()!=null){

            return evaluate_operation(node);
        }
        else if (node.getNodeType().equals("AdditionOperator")
                ||node.getNodeType().equals("RelationOperator")
                ||node.getNodeType().equals("MultiplicationOperator")){

            return evaluate_operation(node);
        }else if (node.getNodeType().equals("bool")){
            return "bool";
        }else {
            return getType(node);
        }
    }
    private String evaluate_operation(Node node) throws Exception {
        String OperandType;
        if (!isRelationOperator(node)) {
            if (node.getNodeType().equals("MultiplicationOperator")&&node.getValue().equals("(2,11")||
                    node.getNodeType().equals("Term") &&node.getValue().equals("(2,11)")){
                return "float";
            }
            OperandType = evaluate_expression(node.getChildren().get(0));
        }else {
            Node childdd= node.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
            if (childdd.getNodeType().equals("Identifier")) {
                if(declaration_identifier.contains(childdd.getValue())&&initializedVariables.contains(childdd.getValue())){
                    OperandType = "bool";
                }else{
                    throw new Exception("Семантическая ошибка: Переменная не объявлена "+ childdd.getValue());
                }
            }else {
                OperandType = "bool";
            }
        }
        for (int i = 1;i<node.getChildren().size();i++){
            String childType = evaluate_expression(node.getChildren().get(i));
            if (node.getNodeType().equals("MultiplicationOperator")&&node.getValue().equals("(2,11")||
                    node.getNodeType().equals("Term") &&node.getValue().equals("(2,11")){
                return "float";
            }
            if (childType!=OperandType){
                throw new Exception("Семантическая ошибка: Несоответствие типов в операции "+ node.getNodeType() +": "+OperandType+" и "+ childType+"");
            }
        }
        return node.getNodeType().equals("RelationOperator")
                ||(node.getNodeType().equals("Expression")&& node.getValue()!=null)
                ? "bool" : OperandType;
    }

    private String getType(Node node) throws Exception {
        if (node.getNodeType().equals("Identifier")){
            if (declaration_identifier.contains(node.getValue())) {
                if (initializedVariables.contains(node.getValue())) {
                    return type;
                }else {
                    throw new Exception("Семантическая ошибка: идентификатор не был инциализирован"+node.getValue());}
            }else {
                throw new Exception("Семантическая ошибка: идентификатор не был объявлен"+node.getValue());
            }
        }else if (node.getNodeType().equals("Number")){
            String value = node.getValue();
            int key;
            try {
                key = Integer.parseInt(value.substring(value.lastIndexOf(',') + 1, value.length() - 1));
            } catch (NumberFormatException e) {
                throw new Exception("Семантическая ошибка: Не удалось извлечь номер ключа из значения: " + value, e);
            }
            // Проверяем, есть ли этот ключ в TypesOfVariables
            String variableType = TypesOfVariables.get(key);
            if (variableType == null) {
                throw new Exception("Семантическая ошибка: Тип для переменной с ключом " + key + " не найден.");
            }


            return variableType;
        }
        return null;
    }
    private boolean isRelationOperator(Node node) {
        return node.getValue().equals("(2,1)") || // NEQ
                node.getValue().equals("(2,2)") || // EQV
                node.getValue().equals("(2,3)") || // LOWT
                node.getValue().equals("(2,4)") || // LOWE
                node.getValue().equals("(2,5)") || // GRT
                node.getValue().equals("(2,6)");   // GRE
    }

}
