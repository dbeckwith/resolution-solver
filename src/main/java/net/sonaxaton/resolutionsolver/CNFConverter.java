package net.sonaxaton.resolutionsolver;

public class CNFConverter {

    private static final int SIMPLIFICATION_ITER_LIMIT = 100;

    private static CNFConverter instance = new CNFConverter();

    public static CNFConverter getInstance() {
        return instance;
    }

    private boolean printSteps;

    private CNFConverter() {
        printSteps = false;
    }

    public void printSteps(boolean printSteps) {
        this.printSteps = printSteps;
    }

    public CNFSentence convert(LogicExpression expression) {
        if (expression instanceof LogicSymbol) {
            CNFSentence.Clause clause = new CNFSentence.Clause();
            clause.add((LogicSymbol) expression);
            CNFSentence sentence = new CNFSentence();
            sentence.add(clause);
            return sentence;
        }
        else {
            LogicExpression newExpr;
            for (int i = 0; i < SIMPLIFICATION_ITER_LIMIT; i++) {
                newExpr = simplify(expression);
                if (newExpr.equals(expression)) break;
                expression = newExpr;
                if (printSteps)
                    Main.log(expression);
                if (i == SIMPLIFICATION_ITER_LIMIT - 1) {
                    throw new IllegalArgumentException("Simplification process took too many steps");
                }
            }
            return extractConjuncts(expression);
        }
    }

    private LogicExpression simplify(LogicExpression expression) {
        if (expression instanceof LogicClause) {
            LogicClause clause = (LogicClause) expression;
            switch (clause.getOperator()) {
                case AND:
                    if (clause.isNegated()) {
                        return new LogicClause(LogicOperator.OR,
                                simplify(clause.getLeft().negate()),
                                simplify(clause.getRight().negate()),
                                false);
                    }
                    else {
                        return new LogicClause(LogicOperator.AND,
                                simplify(clause.getLeft()),
                                simplify(clause.getRight()),
                                false);
                    }
                case OR:
                    if (clause.isNegated()) {
                        return new LogicClause(LogicOperator.AND,
                                simplify(clause.getLeft().negate()),
                                simplify(clause.getRight().negate()),
                                false);
                    }
                    else if (clause.getLeft() instanceof LogicClause && ((LogicClause) clause.getLeft()).getOperator() == LogicOperator.AND) {
                        LogicClause leftClause = (LogicClause) clause.getLeft();
                        return new LogicClause(LogicOperator.AND,
                                new LogicClause(LogicOperator.OR,
                                        leftClause.getLeft(),
                                        clause.getRight(),
                                        false),
                                new LogicClause(LogicOperator.OR,
                                        leftClause.getRight(),
                                        clause.getRight(),
                                        false),
                                clause.isNegated());
                    }
                    else if (clause.getRight() instanceof LogicClause && ((LogicClause) clause.getRight()).getOperator() == LogicOperator.AND) {
                        LogicClause rightClause = (LogicClause) clause.getRight();
                        return new LogicClause(LogicOperator.AND,
                                new LogicClause(LogicOperator.OR,
                                        clause.getLeft(),
                                        rightClause.getLeft(),
                                        false),
                                new LogicClause(LogicOperator.OR,
                                        clause.getLeft(),
                                        rightClause.getRight(),
                                        false),
                                clause.isNegated());
                    }
                    else {
                        return new LogicClause(LogicOperator.OR,
                                simplify(clause.getLeft()),
                                simplify(clause.getRight()),
                                false);
                    }
                case IMPLIES:
                    return new LogicClause(LogicOperator.OR,
                            simplify(clause.getLeft()).negate(),
                            simplify(clause.getRight()),
                            clause.isNegated());
                case EQUIVALENT:
                    return new LogicClause(LogicOperator.AND,
                            new LogicClause(LogicOperator.IMPLIES,
                                    simplify(clause.getLeft()),
                                    simplify(clause.getRight()),
                                    false),
                            new LogicClause(LogicOperator.IMPLIES,
                                    simplify(clause.getRight()),
                                    simplify(clause.getLeft()),
                                    false),
                            clause.isNegated());
            }
            return null;
        }
        else return expression;
    }

    private CNFSentence extractConjuncts(LogicExpression expression) {
        if (!(expression instanceof LogicClause && ((LogicClause) expression).getOperator() == LogicOperator.AND)) {
            throw new IllegalArgumentException("Expression is not a conjunct");
        }
        CNFSentence sentence = new CNFSentence();
        LogicExpression left = ((LogicClause) expression).getLeft();
        LogicExpression right = ((LogicClause) expression).getRight();
        boolean leftConjunct = left instanceof LogicClause && ((LogicClause) left).getOperator() == LogicOperator.AND;
        boolean rightConjunct = right instanceof LogicClause && ((LogicClause) right).getOperator() == LogicOperator.AND;
        boolean leftDisjunct = !leftConjunct && left instanceof LogicClause && ((LogicClause) left).getOperator() == LogicOperator.OR;
        boolean rightDisjunct = !rightConjunct && right instanceof LogicClause && ((LogicClause) right).getOperator() == LogicOperator.OR;
        boolean leftLiteral = !leftDisjunct && left instanceof LogicSymbol;
        boolean rightLiteral = !rightDisjunct && right instanceof LogicSymbol;

        if ((!leftConjunct && !leftDisjunct && !leftLiteral) || (!rightConjunct && !rightDisjunct && !rightLiteral)) {
            throw new IllegalArgumentException("Conjunct had a non-disjunct, non-literal element");
        }

        if (leftConjunct) {
            sentence.addAll(extractConjuncts(left));
        }
        else if (leftLiteral) {
            CNFSentence.Clause clause = new CNFSentence.Clause();
            clause.add((LogicSymbol) left);
            sentence.add(clause);
        }
        else {
            sentence.add(extractDisjuncts(left));
        }

        if (rightConjunct) {
            sentence.addAll(extractConjuncts(right));
        }
        else if (rightLiteral) {
            CNFSentence.Clause clause = new CNFSentence.Clause();
            clause.add((LogicSymbol) right);
            sentence.add(clause);
        }
        else {
            sentence.add(extractDisjuncts(right));
        }
        return sentence;
    }

    private CNFSentence.Clause extractDisjuncts(LogicExpression expression) {
        if (!(expression instanceof LogicClause && ((LogicClause) expression).getOperator() == LogicOperator.OR)) {
            throw new IllegalArgumentException("Expression is not a disjunct");
        }
        CNFSentence.Clause clause = new CNFSentence.Clause();
        LogicExpression left = ((LogicClause) expression).getLeft();
        LogicExpression right = ((LogicClause) expression).getRight();
        boolean leftDisjunct = left instanceof LogicClause && ((LogicClause) left).getOperator() == LogicOperator.OR;
        boolean rightDisjunct = right instanceof LogicClause && ((LogicClause) right).getOperator() == LogicOperator.OR;
        boolean leftLiteral = !leftDisjunct && left instanceof LogicSymbol;
        boolean rightLiteral = !rightDisjunct && right instanceof LogicSymbol;

        if ((!leftDisjunct && !leftLiteral) || (!rightDisjunct && !rightLiteral)) {
            throw new IllegalArgumentException("Disjunct had a non-literal element");
        }

        if (leftDisjunct) {
            clause.addAll(extractDisjuncts(left));
        }
        else {
            clause.add((LogicSymbol) left);
        }

        if (rightDisjunct) {
            clause.addAll(extractDisjuncts(right));
        }
        else {
            clause.add((LogicSymbol) right);
        }
        return clause;
    }
}
