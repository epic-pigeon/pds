package Parser.Compilation;

import Parser.AST.TypeNode;

public class Variable {
    private int id;
    private TypeNode type;
    private boolean active = true;

    public Variable(int id, TypeNode type) {
        this.id = id;
        this.type = type;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public TypeNode getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }
}