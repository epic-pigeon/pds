package Parser.AST;

import Parser.Collection;
import Parser.Lexing.Token;
import Parser.Lexing.TokenHolder;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;

public class Builder {
    private TokenHolder tokenHolder;

    public Builder(TokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    private Token next() {
        return tokenHolder.hasNext() ? tokenHolder.next() : null;
    }

    private boolean checkToken(String... types) {
        return tokenHolder.hasNext() && new Collection<>(types).some(type -> type.equals(tokenHolder.lookUp().getName()));
    }

    private Token skipToken(String... types) {
        if (checkToken(types)) {
            return next();
        } else {
            throw new RuntimeException("Tokens " + new Collection<>(types) + " expected, got " + tokenHolder.lookUp().getName());
        }
    }

    private boolean checkAndSkip(String... types) {
        if (checkToken(types)) {
            next();
            return true;
        } else {
            return false;
        }
    }

    public ProgramNode parse() {
        Collection<Node> result = new Collection<>();
        while (tokenHolder.hasNext()) {
            result.add(parseExpression());
            checkAndSkip("SEMICOLON");
        }
        return new ProgramNode(result);
    }

    private Node parseExpression() {
        return parseAtom();
    }

    private Node parseStatic(TypeNode node) {
        if (tokenHolder.lookUp(2).getName().equals("LEFT_PAREN")) {
            return parseStaticCall(node);
        } else {
            return parseStaticField(node);
        }
    }

    private StaticCallNode parseStaticCall(TypeNode node) {
        skipToken("STATIC_ACCESS");
        return new StaticCallNode(node, parseIdentifier(), parseArguments());
    }

    private StaticFieldNode parseStaticField(TypeNode node) {
        skipToken("STATIC_ACCESS");
        return new StaticFieldNode(node, parseIdentifier());
    }

    private VirtualCallNode parseVirtualCall(Node value) {
        skipToken("DOT");
        boolean strict = checkAndSkip("STRICT_ACCESS") || !checkAndSkip("NON_STRICT_ACCESS");
        return new VirtualCallNode(value, parseIdentifier(), parseArguments(), strict);
    }

    private VirtualFieldNode parseVirtualField(Node value) {
        skipToken("DOT");
        boolean strict = checkAndSkip("STRICT_ACCESS") || !checkAndSkip("NON_STRICT_ACCESS");
        return new VirtualFieldNode(parseIdentifier(), value, strict);
    }

    private Node parseVirtual(Node value) {
        int idx = 2;
        if (tokenHolder.lookUp(1).getName().endsWith("STRICT_ACCESS")) idx++;

        if (tokenHolder.lookUp(idx).getName().equals("LEFT_PAREN")) {
            return parseVirtualCall(value);
        } else {
            return parseVirtualField(value);
        }
    }

    private void unexpectedTokenError() {
        unexpectedTokenError(tokenHolder.lookUp());
    }

    private void unexpectedTokenError(Token token) {
        throw new RuntimeException("Unexpected token " + token);
    }

    private Node parseOOP(Node node) {
        if (checkToken("DOT")) {
            return parseVirtual(node);
        } else if (checkToken("STATIC_ACCESS")) {
            return parseStatic((TypeNode) node);
        }
        unexpectedTokenError();
        return null;
    }

    private Node parseAtom() {
        Node result = null;
        if (checkToken("USE")) {
            result = parseUse();
        } else if (checkToken("STRING", "BOOL_TRUE", "BOOL_FALSE", "NULL")) {
            result = parseValue();
        } else if (checkToken("NEW")) {
            result = parseNew();
        } else if (checkToken("LET", "CONST")) {
            result = parseVariableDef();
        } else if (checkToken("IDENTIFIER")) {
            // type or var
            int pos = tokenHolder.getPosition();
            TypeNode node = parseType();
            if (checkToken("STATIC_ACCESS")) {
                result = parseStatic(node);
            } else {
                tokenHolder.setPosition(pos);
                result = parseVariable();
            }
        } else unexpectedTokenError();
        while (checkToken("DOT", "STATIC_ACCESS")) {
            result = parseOOP(result);
        }
        return result;
    }

    private String parseIdentifier() {
        return skipToken("IDENTIFIER").getValue();
    }

    private VariableNode parseVariable() {
        return new VariableNode(parseIdentifier());
    }

    private UseNode parseUse() {
        skipToken("USE");
        TypeNode type = parseType();
        IdentifierTypeNode alias = null;
        if (checkAndSkip("AS")) {
            alias = new IdentifierTypeNode(parseIdentifier());
        }
        return new UseNode(type, alias);
    }

    private StringValueNode parseString() {
        String value = skipToken("STRING").getValue().substring(1);
        value = value.substring(0, value.length() - 1);
        return new StringValueNode(value);
    }

    private ValueNode parseValue() {
        if (checkToken("STRING")) {
            return parseString();
        } else if (tokenHolder.lookUp().getName().startsWith("BOOL_")) {
            return new BooleanValueNode(skipToken("BOOL_TRUE", "BOOL_FALSE").getName().equals("BOOL_TRUE"));
        } else if (checkToken("NULL")) {
            return new NullValueNode();
        }
        throw new InternalError();
    }

    private TypeNode parseType() {
        Collection<String> chain = new Collection<>( parseIdentifier() );
        while (checkToken("DOT") && tokenHolder.lookUp(1).getName().equals("IDENTIFIER")) {
            skipToken("DOT");
            chain.add(parseIdentifier());
        }
        if (chain.size() == 1) {
            return new IdentifierTypeNode(chain.get(0));
        } else {
            return new JavaTypeNode(chain);
        }
    }

    private<T> Collection<T> delimited(String start, String delimiter, String end, Supplier<T> parser) {
        if (end == null && delimiter == null) {
            throw new RuntimeException("Either end or delimiter should be not null");
        }
        Collection<T> result = new Collection<>();
        if (start != null) {
            skipToken(start);
        }
        while (true) {
            if (end != null) {
                if (checkToken(end)) {
                    skipToken(end);
                    break;
                }
            }
            result.add(parser.get());
            if (delimiter != null) {
                if (checkToken(delimiter)) {
                    skipToken(delimiter);
                } else {
                    if (end != null) skipToken(end);
                    break;
                }
            }
        }
        return result;
    }
    private<T> Collection<T> delimited(String start, String end, Supplier<T> parser) {
        if (end == null) throw new RuntimeException("End should be not null");
        return delimited(start, null, end, parser);
    }
    private<T> Collection<T> delimited(String delimiter, Supplier<T> parser) {
        if (delimiter == null) throw new RuntimeException("Delimiter should be not null");
        return delimited(null, delimiter, null, parser);
    }

    private NewNode parseNew() {
        skipToken("NEW");
        return parseNew(parseType());
    }

    private NewNode parseNew(TypeNode type) {
        return new NewNode(type, parseArguments());
    }

    private Collection<Node> parseArguments() {
        return delimited("LEFT_PAREN", "COMMA", "RIGHT_PAREN", this::parseExpression);
    }

    private VariableDefNode parseVariableDef() {
        boolean _const = skipToken("LET", "CONST").getName().equals("CONST");
        String name = parseIdentifier();
        TypeNode type = null;
        if (checkAndSkip("COLON")) {
            type = parseType();
        }
        Node initializer = null;
        if (checkToken("LEFT_PAREN")) {
            initializer = parseNew(Objects.requireNonNull(type, "Type should be specified to use inline initialization"));
        }
        if (checkAndSkip("ASSIGN")) {
            initializer = parseExpression();
        }
        if (initializer == null) Objects.requireNonNull(type, "Type should be specified if no initializer provided");
        return new VariableDefNode(name, type, initializer, _const);
    }
}
