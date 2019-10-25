package Parser.AST;

public class VariableDefNode extends Node {
    private String name;
    private TypeNode type;
    private Node initializer;
    private boolean _const;

    public boolean isConst() {
        return _const;
    }

    public VariableDefNode(String name, TypeNode type, Node initializer, boolean _const) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
        this._const = _const;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public Node getInitializer() {
        return initializer;
    }

    public void setInitializer(Node initializer) {
        this.initializer = initializer;
    }
}
