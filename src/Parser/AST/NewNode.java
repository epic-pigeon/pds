package Parser.AST;

import Parser.Collection;

public class NewNode extends Node {
    private TypeNode type;
    private Collection<Node> arguments;

    public NewNode(TypeNode type, Collection<Node> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public TypeNode getType() {
        return type;
    }

    public Collection<Node> getArguments() {
        return arguments;
    }
}
