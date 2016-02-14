package net.sonaxaton.resolutionsolver;

public interface Latexable {

    @Override
    String toString();

    default String toLatexString() {
        return toString(true);
    }

    String toString(boolean latex);
}
