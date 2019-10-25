package Parser.AST;

public class BooleanValueNode extends ValueNode {
    private Boolean value;

    public BooleanValueNode(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
