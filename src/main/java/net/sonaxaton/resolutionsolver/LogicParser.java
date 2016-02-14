package net.sonaxaton.resolutionsolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LogicParser {

    private static LogicParser instance = new LogicParser();

    public static LogicParser getInstance() {
        return instance;
    }

    private LogicParser() {}

    public LogicExpression parseLogicExpression(String sentence) throws LogicParseException {
        sentence = sentence.trim();

        if (sentence.isEmpty()) {
            throw new LogicParseException("Empty input", 0);
        }

        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < sentence.length(); i++) {
            char c = sentence.charAt(i);
            Token.Type type = null;
            boolean foundToken = true;

            if (c == '(') {
                type = Token.Type.LPAREN;
            }
            else if (c == ')') {
                type = Token.Type.RPAREN;
            }
            else if (c == '~') {
                type = Token.Type.NEGATION;
            }
            else if (c >= 'A' && c <= 'Z') {
                type = Token.Type.SYMBOL;
            }
            else if (Character.isWhitespace(c)) {
                foundToken = false;
            }
            else {
                foundToken = false;
                LogicOperator operator = LogicOperator.getOperator(c);
                if (operator != null) {
                    type = Token.Type.OPERATOR;
                    foundToken = true;
                }

                if (!foundToken) {
                    throw new LogicParseException("Illegal character", i);
                }
            }

            if (foundToken) {
                tokens.add(new Token(type, Character.toString(c)));
            }
        }

        int parenLevel = 0;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token prevToken = i == 0 ? null : tokens.get(i - 1);
            if (prevToken != null) {
                if (token.type == prevToken.type &&
                        (token.type == Token.Type.SYMBOL ||
                                token.type == Token.Type.OPERATOR)) {
                    throw new LogicParseException("Cannot have two " + token.type.toString().toLowerCase() + "s in a row", i);
                }
                if (token.type == Token.Type.RPAREN &&
                        !(prevToken.type == Token.Type.SYMBOL ||
                                prevToken.type == Token.Type.RPAREN)) {
                    throw new LogicParseException("Cannot have " + prevToken.type.toString().toLowerCase() + " before right parenthesis", i);
                }
                if (prevToken.type == Token.Type.LPAREN &&
                        !(token.type == Token.Type.SYMBOL ||
                                token.type == Token.Type.LPAREN ||
                                token.type == Token.Type.NEGATION)) {
                    throw new LogicParseException("Cannot have " + token.type.toString().toLowerCase() + " after left parenthesis", i);
                }
            }
            switch (token.type) {
                case LPAREN:
                    parenLevel++;
                    break;
                case RPAREN:
                    parenLevel--;
                    if (parenLevel < 0) {
                        throw new LogicParseException("Parentheses mismatch", i);
                    }
                    break;
            }
            token.parenLevel = parenLevel;

            if (prevToken != null && prevToken.type == Token.Type.NEGATION) {
                prevToken.parenLevel = token.parenLevel;
            }
        }

        if (parenLevel != 0) {
            throw new LogicParseException("Parentheses mismatch", sentence.length());
        }

//        tokens.forEach(System.out::println);

        return buildTree(0, tokens.stream()
                .filter(token -> !(token.type == Token.Type.LPAREN || token.type == Token.Type.RPAREN))
                .collect(Collectors.toList()));
    }

    private LogicExpression buildTree(int depth, List<Token> tokens) {
        tokens = new ArrayList<>(tokens);
//        for (int i = 0; i < depth; i++) System.out.print('\t');
//        System.out.println(tokens);
        Optional<Token> rootOperatorOptional = tokens.stream()
                .filter(token -> token.type == Token.Type.OPERATOR)
                .min(Comparator.<Token, Integer>comparing(token -> token.parenLevel)
                        .<Integer>thenComparing(token -> {
                            LogicOperator operator = LogicOperator.getOperator(token.content.charAt(0));
                            if (operator == null) throw new IllegalArgumentException("Unknown operator token");
                            return -operator.ordinal();
                        }));
        if (!rootOperatorOptional.isPresent()) {
            boolean negated = tokens.get(0).type == Token.Type.NEGATION;
            return new LogicSymbol(tokens.get(negated ? 1 : 0).content, negated);
        }
        else {
            Token rootOperator = rootOperatorOptional.get();

            boolean negated = tokens.get(0).type == Token.Type.NEGATION &&
                    rootOperator.parenLevel == tokens.get(0).parenLevel;
            if (negated) {
                tokens.remove(0);
            }

            int split = tokens.indexOf(rootOperator);
//            for (int i = 0; i < depth; i++) System.out.print('\t');
//            System.out.println(rootOperator + "@" + split + (negated ? " (negated)" : ""));

            return new LogicClause(LogicOperator.getOperator(rootOperator.content.charAt(0)),
                    buildTree(depth + 1, tokens.subList(0, split)),
                    buildTree(depth + 1, tokens.subList(split + 1, tokens.size())),
                    negated);
        }
    }

    private static class Token {

        public enum Type {
            SYMBOL, OPERATOR, LPAREN, RPAREN, NEGATION
        }

        public final Type type;
        public final String content;
        public int parenLevel;

        public Token(Type type, String content) {
            this.type = type;
            this.content = content;
            this.parenLevel = 0;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", content='" + content + '\'' +
                    ", parenLevel=" + parenLevel +
                    '}';
        }
    }
}
