package main.models.common;

import main.utils.Printer;
import java.util.ArrayList;

public class TokenSequence {
    private final ArrayList<Token> tokens;
    private int line = -1;
    private int pos = 0;
    private int num = 0;

    public TCode peek() {
        if (pos < num) {
            return tokens.get(pos).getCode();
        } else {
            return TCode.EOF;
        }
    }

    public TCode peek(int n) {
        if (n + pos < num) {
            return tokens.get(pos + n).getCode();
        } else {
            return TCode.EOF;
        }
    }

    public Token next() {
        if (pos == num) {
            return null;
        } else {
            return tokens.get(pos++);
        }
    }

    public int posCmp(TCode a, TCode b) {
        int temp = pos;
        while (temp < num) {
            if (tokens.get(temp).getCode().equals(a)) {
                return -1;
            }
            if (tokens.get(temp).getCode().equals(b)) {
                return 1;
            }
            temp++;
        }
        return 0;
    }

    public void addLine() {
        line += 1;
    }

    public TokenSequence() {
        this.tokens = new ArrayList<>();
    }

    public TokenSequence(Token token) {
        this.tokens = new ArrayList<>();
        tokens.add(token);
    }

    public void add(Token token) {
        token.setLine(line);
        tokens.add(token);
        num++;
    }

    public void print() {
        for (Token token : tokens) {
            Printer.getInstance().print(token.toString());
        }
    }

    public int getLine() {
        return line;
    }

    public int getLastLine() {
        if (pos == 0) {
            return 1;
        } else {
            return tokens.get(pos - 1).getLine();
        }
    }

    public int getThisLine() {
        if (pos == 0) {
            return 1;
        } else {
            return tokens.get(pos).getLine();
        }
    }
}
