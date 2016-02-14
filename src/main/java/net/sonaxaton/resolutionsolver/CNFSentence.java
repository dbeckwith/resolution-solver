package net.sonaxaton.resolutionsolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CNFSentence extends HashSet<CNFSentence.Clause> implements Latexable {

    public static class Clause extends HashSet<LogicSymbol> implements Latexable {

        public Clause() {
        }

        public Clause(Collection<? extends LogicSymbol> c) {
            super(c);
        }

        public boolean isTautology() {
            return stream().anyMatch(symbol -> contains(new LogicSymbol(symbol.getRepr(), !symbol.isNegated())));
        }

        public Clause copy() {
            return new Clause(this);
        }

        @Override
        public String toString() {
            return toString(false);
        }

        @Override
        public String toString(boolean latex) {
            if (isEmpty()) {
                return latex ? "\\bot" : "F";
            }
            String str = stream()
                    .map(symbol -> symbol.toString(latex))
                    .collect(Collectors.joining(" " + LogicOperator.OR.toString(latex) + " "));
            return size() == 1 ? str : (latex ? "\\left(" : "(") + str + (latex ? "\\right)" : ")");
        }
    }

    public CNFSentence() {
    }

    public CNFSentence(Collection<? extends Clause> c) {
        super(c);
    }

    public CNFSentence copy() {
        return new CNFSentence(stream().map(Clause::copy).collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean latex) {
        return isEmpty() ? (latex ? "\\top" : "T") : stream()
                .map(clause -> clause.toString(latex))
                .collect(Collectors.joining(" " + LogicOperator.AND.toString(latex) + " "));
    }
}
