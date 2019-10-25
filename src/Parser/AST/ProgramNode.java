package Parser.AST;

import Parser.Collection;

public class ProgramNode extends Node {
    private Collection<Node> nodes;

    public ProgramNode(Collection<Node> nodes) {
        this.nodes = nodes;
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<Node> nodes) {
        this.nodes = nodes;
    }
}
