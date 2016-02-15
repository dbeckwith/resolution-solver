package net.sonaxaton.resolutionsolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static boolean USE_LATEX = false;

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }
        args[0] = args[0].trim().toLowerCase();
        if (!(args[0].equals("yes") || args[0].equals("true") || args[0].equals("y") ||
                args[0].equals("no") || args[0].equals("false") || args[0].equals("n"))) {
            usage();
            return;
        }
        USE_LATEX = args[0].equals("yes") || args[0].equals("true") || args[0].equals("y");

        Collection<LogicExpression> knowledgeBase = new ArrayList<>();
        LogicExpression conclusion;

        if (args.length == 1 + 0) {
            String input;
            while (!(input = getInput("Enter known fact (blank to stop): ").trim()).isEmpty()) {
                log("Parsing...");
                try {
                    LogicExpression expr = LogicParser.getInstance().parseLogicExpression(input);
                    knowledgeBase.add(expr);
                    log("Parsed Expression:");
                    log(expr);
                }
                catch (LogicParseException e) {
                    log("Error parsing expression: " + e.getMessage());
                }
            }
            if (knowledgeBase.isEmpty()) {
                log("No facts were input, exiting");
                return;
            }
            while (true) {
                input = getInput("Enter conclusion to test: ").trim();
                if (input.isEmpty()) {
                    log("No conclusion was input, exiting");
                    return;
                }
                try {
                    log("Parsing...");
                    conclusion = LogicParser.getInstance().parseLogicExpression(input);
                    log("Parsed Expression:");
                    log(conclusion);
                    break;
                }
                catch (LogicParseException e) {
                    log("Error parsing expression: " + e.getMessage());
                }
            }
        }
        else if (args.length >= 1 + 2) {
            try {
                log("Parsing expressions...");
                for (int i = 1; i < args.length - 1; i++) {
                    log(args[i]);
                    LogicExpression expr = LogicParser.getInstance().parseLogicExpression(args[i]);
                    knowledgeBase.add(expr);
                }
                log(args[args.length - 1]);
                conclusion = LogicParser.getInstance().parseLogicExpression(args[args.length - 1]);
            }
            catch (LogicParseException e) {
                log("Error parsing expression: " + e.getMessage());
                return;
            }
        }
        else {
            usage();
            return;
        }

        log("Knowledge Base:");
        knowledgeBase.forEach(Main::log);
        log("Conclusion to test:");
        log(conclusion);

        log("Building test expression...");
        LogicExpression test = null;
        for (LogicExpression fact : knowledgeBase) {
            if (test == null) test = fact;
            else {
                test = new LogicClause(LogicOperator.AND, test, fact, false);
            }
        }
        test = new LogicClause(LogicOperator.AND, test, conclusion.negate(), false);

        log("Test expression:");
        log(test);
        log("Converting to CNF...");
        CNFConverter.getInstance().printSteps(true);
        CNFSentence cnf = CNFConverter.getInstance().convert(test);
        log("CNF Form:");
        log(cnf);
        log("Testing satisfiability...");
        Resolver.getInstance().printSteps(true);
        boolean unsatisfiable = Resolver.getInstance().isUnsatisfiable(cnf);
        log("Expression is " + (unsatisfiable ? "unsatisfiable" : "satisfiable") + ".");
        log("Therefore, the conclusion is " + (unsatisfiable ? "" : "not ") + "entailed by the knowledge base.");
    }

    public static void log(Latexable latexable) {
        System.out.println((USE_LATEX ? "$$" : "") + latexable.toString(USE_LATEX) + (USE_LATEX ? "$$" : ""));
    }

    public static void log(String message) {
        System.out.println(message + (USE_LATEX ? "\\\\" : ""));
    }

    static void usage() {
        System.out.println("Usage: resolution.jar use_latex_output? [fact... conclusion]");
        System.out.println();
        System.out.println(wrap("Tests if the given conclusion is entailed by the given known facts using proof by " +
                "resolution. If the expressions are not given in the program arguments, the user will be prompted for " +
                "them."));
        System.out.println();
        System.out.println("Expression syntax:");
        for (LogicOperator operator : LogicOperator.values()) {
            System.out.println(operator.name() + ": " + operator.getRepr());
        }
        System.out.println("NOT: ~");
        System.out.println("literals: A-Z");
        System.out.println();
        System.out.println("Made by Daniel Beckwith");
        System.out.println("GitHub: https://github.com/dbeckwith");
    }

    private static final int MAX_LINE_LEN = 80;

    static String wrap(String message) {
        List<String> lines = new ArrayList<>();
        while (message.length() > MAX_LINE_LEN) {
            for (int i = MAX_LINE_LEN - 1; i >= 0; i--) {
                if (Character.isWhitespace(message.charAt(i))) {
                    lines.add(message.substring(0, i));
                    message = message.substring(i + 1);
                    break;
                }
            }
        }
        lines.add(message);
        return lines.stream().collect(Collectors.joining("\n"));
    }

    static String getInput(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}
