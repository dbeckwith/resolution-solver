package net.sonaxaton.resolutionsolver;

public class LogicParseException extends IllegalArgumentException {

    public LogicParseException(int position) {
        super("at " + position);
    }

    public LogicParseException(String s, int position) {
        super(s + " at " + position);
    }

    public LogicParseException(String message, int position, Throwable cause) {
        super(message + " at " + position, cause);
    }

    public LogicParseException(int position, Throwable cause) {
        super("at " + position, cause);
    }
}
