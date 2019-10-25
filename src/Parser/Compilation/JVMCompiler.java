package Parser.Compilation;

import Parser.AST.*;
import Parser.Collection;
import javafx.util.Pair;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import javax.jws.soap.SOAPBinding;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class JVMCompiler extends Compiler {
    private Map<String, TypeNode> aliases = new HashMap<>();
    private Map<String, Variable> variables = new HashMap<>();

    private ClassLoader classLoader;

    {
        variables.put("argv",
                new Variable(
                        0,
                        new ArrayTypeNode(
                                new JavaTypeNode(
                                        new Collection<>("java", "lang", "String")
                                )
                        )
                )
        );
    }

    public JVMCompiler(ProgramNode node) {
        super(node);
    }

    private Collection<AbstractInsnNode> compileNode(Node node) {
        if (node instanceof UseNode) {
            return compileUse((UseNode) node);
        } else if (node instanceof NewNode) {
            return compileNew((NewNode) node);
        } else if (node instanceof VariableDefNode) {
            return compileVariableDef((VariableDefNode) node);
        } else if (node instanceof ValueNode) {
            return compileValue((ValueNode) node);
        } else if (node instanceof VariableNode) {
            return compileVariable((VariableNode) node);
        } else if (node instanceof VirtualCallNode) {
            return compileVirtualCall((VirtualCallNode) node);
        } else if (node instanceof StaticFieldNode) {
            return compileStaticField((StaticFieldNode) node);
        }
        throw new RuntimeException("Unknown node " + node);
    }

    private Class getPrimitiveClass(char name) {
        switch (name) {
            case 'I': return     int.class;
            case 'L': return    long.class;
            case 'D': return  double.class;
            case 'F': return   float.class;
            case 'B': return    byte.class;
            case 'V': return    void.class;
            case 'Z': return boolean.class;
            case 'C': return    char.class;
            default: throw new RuntimeException("Unknown type " + name);
        }
    }

    private Class getClass(String descriptor) throws ClassNotFoundException {
        if (descriptor.length() == 1) {
            return getPrimitiveClass(descriptor.charAt(1));
        } else {
            return classLoader.loadClass(descriptor);
        }
    }

    private Collection<AbstractInsnNode> compileStrictVirtualCall(VirtualCallNode node) {
        //return invokeMethod(node.getMethodName(), node.getValue(), node.getArguments());
        System.out.println(Type.getObjectType("").getClassName());
        Collection<AbstractInsnNode> result = new Collection<>();
        result.addAll(compileNode(node.getValue()));
        result.addAll(prepareArgs(node.getArguments()));
        result.add(new MethodInsnNode(INVOKEVIRTUAL, typeToJava(toType(node.getValue()), false), node.getMethodName(), buildSignature(node.getArguments().map(this::toType), JavaTypeNode.from("java.lang.Object"))));
        return result;
    }

    private Collection<AbstractInsnNode> compileVirtualCall(VirtualCallNode node) {
        Collection<AbstractInsnNode> result = new Collection<>();
        if (node.isStrict()) {
            result.addAll(compileStrictVirtualCall(node));
        } else {
            LabelNode l0 = new LabelNode(), l1 = new LabelNode();
            result.addAll(compileNode(node.getValue()));
            result.add(new JumpInsnNode(IFNONNULL, l0));
            result.add(new InsnNode(ACONST_NULL));
            result.add(new JumpInsnNode(GOTO, l1));
            result.add(l0);
            result.addAll(compileStrictVirtualCall(node));
            result.add(l1);
        }
        return result;
    }

    /*private Collection<AbstractInsnNode> invokeMethod(String methodName, Node self, Collection<Node> args) {
        return invokeMethod(typeToJava(toType(self), false), methodName, args.map(this::toType).map(arg -> typeToJava(arg, false)), compileNode(self), args.map(this::compileNode));
    }

    private Collection<AbstractInsnNode> methodForName(String className, String methodName, Collection<String> argTypes) {
        Collection<AbstractInsnNode> result = new Collection<>();
        result.addAll(classForName(new Collection<>(className.split("/")).join(".")));
        result.add(new LdcInsnNode(methodName));
        result.add(new IntInsnNode(BIPUSH, argTypes.size()));
        result.add(new TypeInsnNode(ANEWARRAY, "java/lang/Class"));
        for (int i = 0; i < argTypes.size(); i++) {
            result.add(new InsnNode(DUP));
            result.add(new IntInsnNode(BIPUSH, i));
            result.addAll(classForName(new Collection<>(argTypes.get(i).split("/")).join(".")));
            result.add(new InsnNode(AASTORE));
        }
        result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));
        return result;
    }

    private Collection<AbstractInsnNode> invokeMethod(String className, String methodName, Collection<String> argTypes, Collection<AbstractInsnNode> self, Collection<Collection<AbstractInsnNode>> args) {
        Collection<AbstractInsnNode> result = new Collection<>();
        result.addAll(methodForName(className, methodName, argTypes));
        result.addAll(self);
        result.add(new IntInsnNode(BIPUSH, args.size()));
        result.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
        for (int i = 0; i < args.size(); i++) {
            result.add(new InsnNode(DUP));
            result.add(new IntInsnNode(BIPUSH, i));
            result.addAll(args.get(i));
            result.add(new InsnNode(AASTORE));
        }

        result.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false));
        return result;
    }

    private Collection<AbstractInsnNode> classForName(String type) {
        if (type.length() == 1) {
            switch (type.charAt(0)) {
                case 'I': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;")
                );
                case 'B': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;")
                );
                case 'F': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;")
                );
                case 'D': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;")
                );
                case 'L': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;")
                );
                case 'V': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;")
                );
                case 'C': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;")
                );
                case 'Z': return new Collection<>(
                        new FieldInsnNode(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;")
                );
            }
        }
        return new Collection<>(
                new LdcInsnNode(Type.getType(type))
        );
    }*/

    private Collection<AbstractInsnNode> compileStaticField(StaticFieldNode node) {
        return new Collection<>(
                new FieldInsnNode(GETSTATIC, typeToJava(node.getType(), false), node.getName(), "Ljava/io/PrintStream;")
        );
    }

    private Collection<AbstractInsnNode> compileValue(ValueNode node) {
        if (node instanceof StringValueNode) {
            return new Collection<>(new LdcInsnNode(((StringValueNode) node).getValue()));
        } else if (node instanceof NullValueNode) {
            return new Collection<>(new InsnNode(Opcodes.ACONST_NULL));
        }
        throw new RuntimeException("Unknown node " + node);
    }

    private Collection<AbstractInsnNode> compileUse(UseNode node) {
        String name = node.getAlias().getIdentifier();
        TypeNode typeNode = node.getType();
        if (name == null) {
            if (typeNode instanceof JavaTypeNode) {
                name = ((JavaTypeNode) typeNode).getChain().last();
            }
            throw new RuntimeException("Could not predict alias for " + typeNode);
        }
        aliases.put(name, typeNode);
        return new Collection<>();
    }

    private boolean variableAccessible(String name) {
        return variables.containsKey(name) && variables.get(name) != null && variables.get(name).isActive();
    }

    private Collection<AbstractInsnNode> compileVariable(VariableNode node) {
        if (!variableAccessible(node.getName())) throw new RuntimeException("Undefined variable " + node.getName());
        return new Collection<>(
                new VarInsnNode(ALOAD, variables.get(node.getName()).getId())
        );
    }

    private int createVariable(String name, TypeNode type) {
        variables.put(name, new Variable(variables.size(), type));
        return variables.size() - 1;
    }

    private Collection<AbstractInsnNode> compileVariableDef(VariableDefNode node) {
        Collection<AbstractInsnNode> result = new Collection<>();
        TypeNode typeNode = node.getType();
        if (typeNode == null) {
            typeNode = toType(node.getInitializer());
        }
        int id = createVariable(node.getName(), typeNode);
        if (node.getInitializer() != null) {
            result.addAll(compileNode(node.getInitializer()));
            result.add(new VarInsnNode(Opcodes.ASTORE, id));
        }
        return result;
    }

    private Collection<AbstractInsnNode> prepareArgs(Collection<Node> args) {
        Collection<AbstractInsnNode> result = new Collection<>();
        for (Node node : args) result.addAll(compileNode(node));
        return result;
    }

    private Collection<AbstractInsnNode> compileNew(NewNode node) {
        Collection<AbstractInsnNode> result = new Collection<>();
        result.add(new TypeInsnNode(Opcodes.NEW, typeToJava(node.getType(), false)));
        result.add(new InsnNode(Opcodes.DUP));
        result.addAll(prepareArgs(node.getArguments()));
        result.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeToJava(node.getType(), false), "<init>", buildSignature(node.getArguments().map(this::toType), PrimitiveTypeNode.VOID), false));
        return result;
    }

    private String buildSignature(Collection<TypeNode> types, TypeNode returnType) {
        return "(" + types.map(type -> typeToJava(type, true)).join("") + ")" + typeToJava(returnType, true);
    }

    private String typeToJava(TypeNode type, boolean withCover) {
        if (type instanceof JavaTypeNode) {
            return (withCover ? "L" : "") + ((JavaTypeNode) type).getChain().join("/") + (withCover ? ";" : "");
        } else if (type instanceof PrimitiveTypeNode) {
            return ((PrimitiveTypeNode) type).getJavaName();
        } else if (type instanceof IdentifierTypeNode) {
            String identifier = ((IdentifierTypeNode) type).getIdentifier();
            if (aliases.containsKey(identifier)) {
                return typeToJava(aliases.get(identifier), withCover);
            } else {
                return identifier;
            }
        } else if (type instanceof ArrayTypeNode) {
            TypeNode element = ((ArrayTypeNode) type).getElementType();
            return "[" + typeToJava(element, true);
        }
        throw new RuntimeException("Cannot convert unknown type " + type + " to Java");
    }

    private TypeNode toType(Node node) {
        if (node instanceof ValueNode) {
            if (node instanceof NullValueNode) {
                return JavaTypeNode.OBJECT;
            } else if (node instanceof StringValueNode) {
                return JavaTypeNode.STRING;
            } else if (node instanceof BooleanValueNode) {
                return PrimitiveTypeNode.BOOLEAN;
            }
        } else if (node instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) node;
            if (!variableAccessible(variableNode.getName())) throw new RuntimeException("Undefined variable " + variableNode.getName());
            return variables.get(variableNode.getName()).getType();
        } else if (node instanceof StaticFieldNode) {
            return JavaTypeNode.from("java.io.PrintStream");
        } else if (node instanceof VirtualCallNode) {
            return JavaTypeNode.from("java.io.PrintStream");
        }
        throw new RuntimeException("Cannot identify type for " + node);
    }

    @Override
    public byte[] compile(Config config) {
        ClassNode classNode = new ClassNode();
        classNode.version = V1_8;
        classNode.access = ACC_SUPER + ACC_PUBLIC;
        classNode.name = "Kar";
        classNode.superName = "java/lang/Object";
        MethodNode methodNode = new MethodNode(ACC_PUBLIC + ACC_STATIC + ACC_VARARGS, "main", "([Ljava/lang/String;)V", null, null);
        for (Node node : this.node.getNodes()) {
            for (AbstractInsnNode insnNode : compileNode(node)) {
                methodNode.instructions.add(insnNode);
            }
            //methodNode.instructions.add(new InsnNode(POP));
        }
        methodNode.instructions.add(new InsnNode(RETURN));
        /*for (Map.Entry<String, Pair<Pair<Integer, Label>, TypeNode>> entry : variables.entrySet()) {
            System.out.println(typeToJava(entry.getValue().getValue(), true));
            methodNode.visitLocalVariable(entry.getKey(), typeToJava(entry.getValue().getValue(), true), null, entry.getValue().getKey().getValue(), end, entry.getValue().getKey().getKey());
        }*/
        classNode.methods.add(methodNode);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
