package net.sonaxaton.resolutionsolver;

public enum LogicOperator implements Latexable {
    AND('^', "\\wedge"),
    OR('v', "\\vee"),
    IMPLIES('>', "\\Rightarrow"),
    EQUIVALENT('=', "\\Leftrightarrow");

    public static LogicOperator getOperator(char repr) {
        for (LogicOperator operator : values()) {
            if (operator.repr == repr) return operator;
        }
        return null;
    }

    private final char repr;
    private final String latexRepr;

    LogicOperator(char repr, String latexRepr) {
        this.repr = repr;
        this.latexRepr = latexRepr;
    }

    public char getRepr() {
        return repr;
    }

    public String getLatexRepr() {
        return latexRepr;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean latex) {
        return latex ? latexRepr : Character.toString(repr);
    }
}
