package main.lexer;

import main.models.common.handler.ErrorInfoList;
import main.models.common.ast.Token;
import main.models.common.ast.TokenSequence;
import main.models.common.ast.TCode;
import main.utils.CharClassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lexer {
    private final TokenSequence tokens;
    private FileReader fr;
    private BufferedReader br;
    private String buffer = "";
    private int pos = 0;
    private boolean end = false;

    public Lexer() {
        tokens = new TokenSequence();
        try {
            fr = new FileReader("testfile.txt");
            br = new BufferedReader(fr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void analyse() {
        while (!end) {
            generateToken();
        }
    }

    public void generateToken() {
        char curChar = next();
        StringBuilder curToken = new StringBuilder(String.valueOf(curChar));
        TCode code;
        if (CharClassifier.isSpace(curChar)) {
            return;
        }
        if (CharClassifier.isIdentNonDigit(curChar)) {
            while (CharClassifier.isIdent(peek())) {
                curToken.append(next());
            }
            code = CharClassifier.reserve(curToken.toString());
            if (code == null) {
                tokens.add(new Token(TCode.IDENFR, curToken.toString()));
            } else {
                tokens.add(new Token(code, curToken.toString()));
            }
        } else if (Character.isDigit(curChar)) {
            if (curChar != '0' || !Character.isDigit(peek())) {
                while (Character.isDigit(peek())) {
                    curToken.append(next());
                }
            }
            tokens.add(new Token(TCode.INTCON, curToken.toString()));
        } else if (curChar == '\"') {
            char p;
            while ((p = peek()) != '\"') {
                if ((40 <= p && p <= 126) || p == 32 || p == 33 || p == '%') {
                    curToken.append(next());
                    if (p == '\\' && peek() != 'n') {
                        ErrorInfoList.getInstance().addError('a', tokens.getLine());
                        if (peek() != '\"') {
                            next();
                        }
                    }
                    if (p == '%' && peek() != 'd') {
                        ErrorInfoList.getInstance().addError('a', tokens.getLine());
                        if (peek() != '\"') {
                            next();
                        }
                    }
                } else {
                    ErrorInfoList.getInstance().addError('a', tokens.getLine());
                    next();
                }
            }
            curToken.append(next());
            tokens.add(new Token(TCode.STRCON, curToken.toString()));
        } else {
            String single = curToken.toString();
            curToken.append(peek());
            String twin = curToken.toString();
            doubleSymbolManipulation(single, twin);
        }
    }

    public void doubleSymbolManipulation(String single, String twin) {
        if (twin.equals("//")) {
            do {
                next();
            } while (peek() != '\n');
        } else if (twin.equals("/*")) {
            char c1 = next(); // can't be deleted
            char c2 = next();
            do {
                c1 = c2;
                c2 = next();
            } while ((c1 != '*' || c2 != '/') && !end);
        } else {
            TCode code = CharClassifier.reserve(twin);
            if (code == null) {
                code = CharClassifier.reserve(single);
                tokens.add(new Token(code, single));
            } else {
                next();
                tokens.add(new Token(code, twin));
            }
        }
    }

    public char next() {
        if (pos == 0) {
            tokens.addLine();
        }
        if (pos == buffer.length()) {
            if (readLine()) {
                end = true;
            }
            return '\n';
        } else {
            return buffer.charAt(pos++);
        }
    }

    public char peek() {
        if (pos == buffer.length()) {
            return '\n';
        } else {
            return buffer.charAt(pos);
        }
    }

    public boolean readLine() {
        try {
            if ((buffer = br.readLine()) == null) {
                release();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pos = 0;
        return false;
    }

    public TokenSequence getTokens() {
        return tokens;
    }

    public void release() {
        try {
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
