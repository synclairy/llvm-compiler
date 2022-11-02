package main.models.common.ast;

public class Token {
    private final TCode code;
    private final String origin;
    private final String value;
    private int line;

    public TCode getCode() {
        return code;
    }

    public int getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return code.toString() + ' ' + origin + '\n';
    }

    public Token(TCode code, String origin) {
        this.code = code;
        this.origin = origin;
        value = origin;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
