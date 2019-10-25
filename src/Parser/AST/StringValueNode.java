package Parser.AST;

public class StringValueNode extends ValueNode {
    private String value;

    public StringValueNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
