package net.sonaxaton.resolutionsolver;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class Resolver {

    private static Resolver instance = new Resolver();

    public static Resolver getInstance() {
        return instance;
    }

    private boolean printSteps;

    private Resolver() {
        printSteps = false;
    }

    public void printSteps(boolean printSteps) {
        this.printSteps = printSteps;
    }

    public boolean isUnsatisfiable(CNFSentence cnf) {
        if (cnf.isEmpty()) return false;
        if (cnf.stream().anyMatch(Set::isEmpty)) return true;

        cnf = cnf.copy();
        CNFSentence newClauses = new CNFSentence();
        while (true) {
            Iterator<CNFSentence.Clause> ita = cnf.iterator();
            for (int i = 0; i < cnf.size(); i++) {
                CNFSentence.Clause clauseA = ita.next();
                Iterator<CNFSentence.Clause> itb = cnf.iterator();
                for (int j = 0; j < cnf.size(); j++) {
                    CNFSentence.Clause clauseB = itb.next();
                    if (j > i) {
                        CNFSentence resolvents = Resolver.getInstance().resolve(clauseA, clauseB);
                        if (resolvents.stream().anyMatch(Set::isEmpty)) {
//                            System.out.println("empty clause generated");
                            return true;
                        }
                        newClauses.addAll(resolvents);
                    }
                }
            }
            if (newClauses.stream().allMatch(cnf::contains)) {
//                System.out.println("no new clauses were generated");
                return false;
            }
            cnf.addAll(newClauses);
            if (printSteps)
                Main.log(cnf);
        }
    }

    private CNFSentence resolve(CNFSentence.Clause ca, CNFSentence.Clause cb) {
//        System.out.println("resolving " + ca + " and " + cb);
        CNFSentence newClauses = new CNFSentence();
        for (LogicSymbol sa : ca) {
            for (LogicSymbol sb : cb) {
                if (Objects.equals(sa.getRepr(), sb.getRepr()) && sa.isNegated() != sb.isNegated()) {
                    CNFSentence.Clause newClause = new CNFSentence.Clause();
                    newClause.addAll(ca);
                    newClause.addAll(cb);
                    newClause.remove(sa);
                    newClause.remove(sb);
                    if (!newClause.isTautology()) {
                        newClauses.add(newClause);
                    }
                }
            }
        }
//        System.out.println("result of revolution: " + newClauses);
        return newClauses;
    }
}
