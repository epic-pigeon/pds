package Parser.AST;

public class PrimitiveTypeNode extends TypeNode {
    private String javaName;

    private PrimitiveTypeNode(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return javaName;
    }

    public static final PrimitiveTypeNode BOOLEAN = new PrimitiveTypeNode("B");
    public static final PrimitiveTypeNode VOID = new PrimitiveTypeNode("V");
}
