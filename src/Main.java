import Parser.*;
import Parser.AST.Builder;
import Parser.AST.ProgramNode;
import Parser.Compilation.Compiler;
import Parser.Compilation.JVMCompiler;
import Parser.Lexing.KeywordRule;
import Parser.Lexing.Lexer;
import Parser.Lexing.Rule;
import Parser.Lexing.TokenHolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Main {
    private static Collection<Rule> rules = new Collection<>(
            new Rule("LEFT_PAREN", Pattern.compile("\\(")),
            new Rule("RIGHT_PAREN", Pattern.compile("\\)")),
            new Rule("LEFT_CURLY_PAREN", Pattern.compile("\\{")),
            new Rule("LEFT_SQUARE_PAREN", Pattern.compile("\\[")),
            new Rule("RIGHT_SQUARE_PAREN", Pattern.compile("]")),
            new Rule("RIGHT_CURLY_PAREN", Pattern.compile("}")),
            new Rule("COMMA", Pattern.compile(",")),
            new Rule("ELVIS", Pattern.compile("\\?!")),
            new Rule("NON_STRICT_ACCESS", Pattern.compile("\\?\\?")),
            new Rule("STRICT_ACCESS", Pattern.compile("!!")),
            new Rule("AND", Pattern.compile("&&")),
            new Rule("COMMA", Pattern.compile(",")),
            new Rule("BINARY_XOR", Pattern.compile("\\^\\^")),
            new Rule("XOR", Pattern.compile("\\^")),
            new Rule("DOT", Pattern.compile("\\.")),
            new Rule("DOUBLE_PLUS", Pattern.compile("\\+\\+")),
            new Rule("PLUS", Pattern.compile("\\+")),
            new Rule("DOUBLE_MINUS", Pattern.compile("--")),
            new Rule("MINUS", Pattern.compile("-")),
            new Rule("MULTIPLICATION", Pattern.compile("\\*")),
            new Rule("SINGLE_LINE_COMMENT", Pattern.compile("//[^\n]*")),
            new Rule("DIVISION", Pattern.compile("/")),
            new Rule("REMAINDER", Pattern.compile("%")),
            new Rule("OR", Pattern.compile("\\|\\|")),
            new Rule("BINARY_OR", Pattern.compile("\\|")),
            new Rule("IDENTICAL", Pattern.compile("===")),
            new Rule("EQUALS", Pattern.compile("==")),
            new Rule("ASSIGN", Pattern.compile("=")),
            new Rule("NUMBER", Pattern.compile("\\d+(\\.\\d+)?")),
            new Rule("STRING", Pattern.compile("('([^']|(\\\\.))*')|(\"([^\"]|(\\\\.))*\")")),
            new Rule("MULTILINE_COMMENT", Pattern.compile("/\\*([^/])*\\*/")),
            new Rule("SEMICOLON", Pattern.compile(";")),
            new Rule("STATIC_ACCESS", Pattern.compile("::")),
            new Rule("COLON", Pattern.compile(":")),


            new KeywordRule("LET", "let"),
            new KeywordRule("CONST", "const"),
            new KeywordRule("AS", "as"),
            new KeywordRule("NEW", "new"),
            new KeywordRule("USE", "use"),
            new KeywordRule("CAST", "cast"),
            new KeywordRule("BOOL_TRUE", "true"),
            new KeywordRule("BOOL_FALSE", "false"),
            new KeywordRule("NULL", "null"),
            new KeywordRule("OF", "of"),


            new Rule("IDENTIFIER", Pattern.compile("([a-zA-Z$_][a-zA-Z0-9$_]*)|(`[a-zA-Z0-9.,\\- $_/\\\\]+`)"))
    );

    private static Rule toSkip = new Rule("kar", Pattern.compile("\\s+"));

    public static void main(String[] args) throws Exception {
        File file = new File("example.pds");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String code = new String(data, StandardCharsets.UTF_8);

        TokenHolder tokenHolder = new Lexer().lex(code, rules, toSkip);

        System.out.println(tokenHolder);

        ProgramNode node = new Builder(tokenHolder).parse();

        System.out.println(node);

        FileOutputStream fileOutputStream = new FileOutputStream("./out/production/pds/Kar.class");
        fileOutputStream.write(
                new JVMCompiler(node).compile(new Compiler.Config("Kar"))
                //Test_Dump.dump()
        );
    }
}
