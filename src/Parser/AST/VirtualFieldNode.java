package Parser.AST;

public class VirtualFieldNode extends Node {
    private String name;
    private Node value;
    private boolean strict;

    public VirtualFieldNode(String name, Node value, boolean strict) {
        this.name = name;
        this.value = value;
        this.strict = strict;
    }

    public boolean isStrict() {
        return strict;
    }

    public String getName() {
        return name;
    }

    public Node getValue() {
        return value;
    }
}
