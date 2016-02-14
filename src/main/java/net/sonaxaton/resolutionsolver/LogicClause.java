package net.sonaxaton.resolutionsolver;

public class LogicClause extends LogicExpression {

    private LogicOperator operator;
    private LogicExpression left;
    private LogicExpression right;

    public LogicClause(LogicOperator operator, LogicExpression left, LogicExpression right, boolean negated) {
        super(negated);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public LogicOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicOperator operator) {
        this.operator = operator;
    }

    public LogicExpression getLeft() {
        return left;
    }

    public void setLeft(LogicExpression left) {
        this.left = left;
    }

    public LogicExpression getRight() {
        return right;
    }

    public void setRight(LogicExpression right) {
        this.right = right;
    }

    @Override
    public LogicExpression negate() {
        return new LogicClause(operator, left, right, !isNegated());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicClause)) return false;

        LogicClause clause = (LogicClause) o;

        if (operator != clause.operator) return false;
        if (left != null ? !left.equals(clause.left) : clause.left != null) return false;
        return right != null ? right.equals(clause.right) : clause.right == null;

    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean latex) {
        boolean lparen = !(left instanceof LogicSymbol ||
                (left instanceof LogicClause &&
                        (left.isNegated() ||
                                ((this.getOperator() == LogicOperator.AND ||
                                        this.getOperator() == LogicOperator.OR) &&
                                        ((LogicClause) left).getOperator() == this.getOperator()) ||
                                this.getOperator().ordinal() > ((LogicClause) left).getOperator().ordinal())));
        boolean rparen = !(right instanceof LogicSymbol ||
                (right instanceof LogicClause &&
                        (right.isNegated() ||
                                ((this.getOperator() == LogicOperator.AND ||
                                        this.getOperator() == LogicOperator.OR) &&
                                        ((LogicClause) right).getOperator() == this.getOperator()) ||
                                this.getOperator().ordinal() > ((LogicClause) right).getOperator().ordinal())));
        StringBuilder sb = new StringBuilder();
        if (isNegated()) sb.append(latex ? "\\neg\\left(" : "~(");
        if (lparen) sb.append(latex ? "\\left(" : "(");
        sb.append(latex ? left.toLatexString() : left.toString());
        if (lparen) sb.append(latex ? "\\right)" : ")");
        sb.append(' ');
        sb.append(latex ? operator.toLatexString() : operator.toString());
        sb.append(' ');
        if (rparen) sb.append(latex ? "\\left(" : "(");
        sb.append(latex ? right.toLatexString() : right.toString());
        if (rparen) sb.append(latex ? "\\right)" : ")");
        if (isNegated()) sb.append(latex ? "\\right)" : ")");
        return sb.toString();
    }
}
