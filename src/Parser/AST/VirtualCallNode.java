package Parser.AST;

import Parser.Collection;

public class VirtualCallNode extends Node {
    private Node value;
    private String methodName;
    private Collection<Node> arguments;
    private boolean strict;

    public VirtualCallNode(Node value, String methodName, Collection<Node> arguments, boolean strict) {
        this.value = value;
        this.methodName = methodName;
        this.arguments = arguments;
        this.strict = strict;
    }

    public boolean isStrict() {
        return strict;
    }

    public Collection<Node> getArguments() {
        return arguments;
    }

    public Node getValue() {
        return value;
    }

    public String getMethodName() {
        return methodName;
    }
}
