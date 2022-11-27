package main.utils;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;

public class CodeClassifier {
    public static boolean isSeparator(NCode n, TCode t) {
        switch (n) {
            case LOrExp:
                return t.equals(TCode.OR);
            case LAndExp:
                return t.equals(TCode.AND);
            case EqExp:
                return t.equals(TCode.EQL) || t.equals(TCode.NEQ);
            case RelExp:
                return t.equals(TCode.LSS) || t.equals(TCode.GRE)
                        || t.equals(TCode.LEQ) || t.equals(TCode.GEQ);
            case AddExp:
                return t.equals(TCode.PLUS) || t.equals(TCode.MINU);
            case MulExp:
                return t.equals(TCode.MULT) || t.equals(TCode.DIV) || t.equals(TCode.MOD);
            default:
                return false;
        }
    }

    public static boolean isFirstOfExp(TCode t) {
        switch (t) {
            case IDENFR:
            case INTCON:
            case LPARENT:
            case PLUS:
            case MINU:
            case NOT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBr(TCode t) {
        if (t == null) {
            return false;
        }
        switch (t) {
            case BREAKTK:
            case CONTINUETK:
            case RETURNTK:
                return true;
            default:
                return false;
        }
    }
}
