package Parser.AST;

public class IdentifierTypeNode extends TypeNode {
    private String identifier;

    public IdentifierTypeNode(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
