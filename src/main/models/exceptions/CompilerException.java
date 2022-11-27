package main.models.exceptions;

public class CompilerException extends Exception {
    private char type;
    private int line;

    public CompilerException(char type) {
        this.type = type;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
