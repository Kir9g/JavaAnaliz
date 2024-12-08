package org.example.SINTAX;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String nodeType;
    public String value;
    private List<Node> children;


    public Node(String nodeType, String value) {
        this.nodeType = nodeType;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public Node(String nodeType){
        this(nodeType,null);
    }

    public void addChild(Node childNode) {
        if (childNode != null) {
            children.add(childNode);
        }
    }

    public void printTree(int indent) {
        String prefix = "   ".repeat(indent);
        if (value != null) {
            System.out.println(prefix + nodeType + ": " + value);
        } else {
            System.out.println(prefix + nodeType);
        }
        for (Node child : children) {
            child.printTree(indent + 2);
        }
    }


    public String getTreeAsString(int indent) {
        StringBuilder result = new StringBuilder();
        String prefix = "   ".repeat(indent);
        if (value != null) {
            result.append(prefix).append(nodeType).append(": ").append(value).append("\n");
        } else {
            result.append(prefix).append(nodeType).append("\n");
        }
        for (Node child : children) {
            result.append(child.getTreeAsString(indent + 2));
        }
        return result.toString();
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
