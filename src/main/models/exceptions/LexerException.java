package main.models.exceptions;

public class LexerException extends CompilerException {
    private final int line;

    public LexerException(int line) {
        this.line = line;
    }
    public void print() {
        System.out.println("Lexer error in line " + line + ".");
    }
}
