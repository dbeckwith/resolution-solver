package net.sonaxaton.resolutionsolver;

public abstract class LogicExpression implements Latexable {

    private boolean negated;

    public LogicExpression(boolean negated) {
        this.negated = negated;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public abstract LogicExpression negate();
}
