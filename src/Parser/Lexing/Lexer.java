package Parser.Lexing;


import Parser.Collection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private Collection<Token> tokens;
    private String code;
    private Collection<Rule> rules;
    private Rule toSkip;
    private int position;

    public TokenHolder lex(String code, Collection<Rule> rules, Rule toSkip) {
        position = 0;
        tokens = new Collection<>();
        this.code = code;
        this.rules = rules;
        this.toSkip = toSkip;
        while (position < code.length()) {
            readNext();
        }
        return new TokenHolder(tokens);
    }

    private Token readNext() {
        for (Pattern pattern : toSkip.getPatterns()) {
            Matcher skipMatcher = pattern.matcher(code.substring(position));
            if (skipMatcher.find() && skipMatcher.start() == 0) {
                position += skipMatcher.group().length();
                break;
            }
        }
        if (position >= code.length()) return null;

        for (Rule rule : rules) {
            for (Pattern pattern : rule.getPatterns()) {
                Matcher matcher = pattern.matcher(code.substring(position));
                if (matcher.find() && matcher.start() == 0) {
                    position += matcher.group().length();
                    if (rule.getName().endsWith("COMMENT")) {
                        return readNext();
                    } else if (rule.isKeyword()) {
                        position--;
                        Token token = new Token(rule.getName(), matcher.group().substring(0, matcher.group().length() - 1), rule, position, matcher.group().split("\\r?\\n").length - 1);
                        tokens.add(token);
                        return token;
                    } else {
                        Token token = new Token(rule.getName(), matcher.group(), rule, position, matcher.group().split("\\r?\\n").length - 1);
                        tokens.add(token);
                        return token;
                    }
                }
            }
        }

        throw new LexingException(tokens, position);
    }
}