package main.utils;

import main.models.common.ast.TCode;

import java.util.ArrayList;

public class CharClassifier {
    public static boolean isIdentNonDigit(char c) {
        return Character.isLetter(c) || c == '_';
    }

    public static boolean isIdent(char c) {
        return Character.isDigit(c) || isIdentNonDigit(c);
    }

    public static boolean isSpace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    public static ArrayList<String> fixFormatString(String s) {
        int len = s.length();
        ArrayList<String> chars = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '\"') {
                continue;
            }
            if (s.charAt(i) == '%') {
                if (s.charAt(i + 1) == 'd') {
                    i++;
                    chars.add(null);
                }
            } else if (s.charAt(i) == '\\') {
                if (s.charAt(i + 1) == 'n') {
                    i++;
                    chars.add("10");
                }
            } else {
                int n = s.charAt(i);
                chars.add(String.valueOf(n));
            }
        }
        return chars;
    }

    public static int countArgs(String s) {
        int len = s.length();
        int cnt = 0;
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '%') {
                cnt++;
            }
        }
        return cnt;
    }

    public static TCode reserve(String s) {
        switch (s) {
            case "main":
                return TCode.MAINTK;
            case "while":
                return TCode.WHILETK;
            case "const":
                return TCode.CONSTTK;
            case "getint":
                return TCode.GETINTTK;
            case "int":
                return TCode.INTTK;
            case "printf":
                return TCode.PRINTFTK;
            case "break":
                return TCode.BREAKTK;
            case "return":
                return TCode.RETURNTK;
            case "continue":
                return TCode.CONTINUETK;
            case "if":
                return TCode.IFTK;
            case "else":
                return TCode.ELSETK;
            case "void":
                return TCode.VOIDTK;
            case "!":
                return TCode.NOT;
            case "&&":
                return TCode.AND;
            case "||":
                return TCode.OR;
            case "+":
                return TCode.PLUS;
            case "-":
                return TCode.MINU;
            case "*":
                return TCode.MULT;
            case "/":
                return TCode.DIV;
            case "%":
                return TCode.MOD;
            case "<":
                return TCode.LSS;
            case "<=":
                return TCode.LEQ;
            case ">":
                return TCode.GRE;
            case ">=":
                return TCode.GEQ;
            case "==":
                return TCode.EQL;
            case "!=":
                return TCode.NEQ;
            case "=":
                return TCode.ASSIGN;
            case ";":
                return TCode.SEMICN;
            case ",":
                return TCode.COMMA;
            case "(":
                return TCode.LPARENT;
            case ")":
                return TCode.RPARENT;
            case "[":
                return TCode.LBRACK;
            case "]":
                return TCode.RBRACK;
            case "{":
                return TCode.LBRACE;
            case "}":
                return TCode.RBRACE;
            default:
                return null;
        }
    }
}