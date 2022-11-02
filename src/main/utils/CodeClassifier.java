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
}
