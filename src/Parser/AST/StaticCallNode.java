package Parser.AST;

import Parser.Collection;

public class StaticCallNode extends Node {
    private TypeNode type;
    private String methodName;
    private Collection<Node> arguments;

    public StaticCallNode(TypeNode type, String methodName, Collection<Node> arguments) {
        this.type = type;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public TypeNode getType() {
        return type;
    }

    public String getMethodName() {
        return methodName;
    }

    public Collection<Node> getArguments() {
        return arguments;
    }
}
