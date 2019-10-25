package Parser.Compilation;

import Parser.AST.Node;
import Parser.AST.ProgramNode;
import Parser.Collection;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public abstract class Compiler {
    public static class Config {
        private String className;

        public Config(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    protected ProgramNode node;

    public Compiler(ProgramNode node) {
        this.node = node;
    }

    public abstract byte[] compile(Config config);
}
