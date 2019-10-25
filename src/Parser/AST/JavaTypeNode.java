package Parser.AST;

import Parser.Collection;

public class JavaTypeNode extends TypeNode {
    private Collection<String> chain;

    public JavaTypeNode(Collection<String> chain) {
        this.chain = chain;
    }

    public Collection<String> getChain() {
        return chain;
    }

    public void setChain(Collection<String> chain) {
        this.chain = chain;
    }

    public static final TypeNode OBJECT = new JavaTypeNode(new Collection<>("java", "lang", "Object"));
    public static final TypeNode STRING = new JavaTypeNode(new Collection<>("java", "lang", "String"));

    public static JavaTypeNode from(String type) {
        return new JavaTypeNode(new Collection<>(type.split("\\.")));
    }
}
