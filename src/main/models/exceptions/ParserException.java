package main.models.exceptions;

public class ParserException extends CompilerException {
    private final int line;

    public ParserException(int line) {
        this.line = line;
    }
    public void print() {
        System.out.println("Parser error in line " + line + ".");
    }
}
