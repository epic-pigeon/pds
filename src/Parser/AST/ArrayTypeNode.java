package Parser.AST;

public class ArrayTypeNode extends TypeNode {
    private TypeNode elementType;

    public ArrayTypeNode(TypeNode elementType) {
        this.elementType = elementType;
    }

    public TypeNode getElementType() {
        return elementType;
    }

    public void setElementType(TypeNode elementType) {
        this.elementType = elementType;
    }
}
