package main.models.common.handler;

public class ErrorInfo implements Comparable<ErrorInfo> {
    private final char type;
    private final int line;

    public ErrorInfo(char type, int line) {
        this.type = type;
        this.line = line;
    }

    @Override
    public String toString() {
        return line + " " + type + '\n';
    }

    @Override
    public int compareTo(ErrorInfo o) {
        return this.line - o.line;
    }

    public int getLine() {
        return line;
    }
}
