package Parser.AST;

public class StaticFieldNode extends Node {
    private TypeNode type;
    private String name;

    public StaticFieldNode(TypeNode type, String methodName) {
        this.type = type;
        this.name = methodName;
    }

    public TypeNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
