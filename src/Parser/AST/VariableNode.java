package Parser.AST;

public class VariableNode extends Node {
    private String name;

    public VariableNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
