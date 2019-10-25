package Parser.AST;

public class UseNode extends Node {
    private TypeNode type;
    private IdentifierTypeNode alias;

    public UseNode(TypeNode type, IdentifierTypeNode alias) {
        this.type = type;
        this.alias = alias;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public IdentifierTypeNode getAlias() {
        return alias;
    }

    public void setAlias(IdentifierTypeNode alias) {
        this.alias = alias;
    }
}
