package net.sonaxaton.resolutionsolver;

public class LogicSymbol extends LogicExpression {

    private final String repr;

    public LogicSymbol(String repr) {
        this(repr, false);
    }

    public LogicSymbol(String repr, boolean negated) {
        super(negated);
        this.repr = repr;
    }

    public String getRepr() {
        return repr;
    }

    @Override
    public LogicExpression negate() {
        return new LogicSymbol(repr, !isNegated());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean latex) {
        return (isNegated() ? (latex ? "\\neg " : "~") : "") + getRepr();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicSymbol)) return false;

        LogicSymbol that = (LogicSymbol) o;

        if (isNegated() != that.isNegated()) return false;
        return repr != null ? repr.equals(that.repr) : that.repr == null;
    }

    @Override
    public int hashCode() {
        int result = repr != null ? repr.hashCode() : 0;
        result = 31 * result + (isNegated() ? 1 : 0);
        return result;
    }
}
