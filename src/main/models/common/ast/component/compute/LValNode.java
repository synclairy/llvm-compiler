package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class LValNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.LVal;
    }

    @Override
    public void llvm() {
        fillLineByIndex(0);
        if (getChildren().size() == 1) {
            getTable().loadSymbol(getFirstToken().getValue());
        } else if (getChildren().size() >= 4) {
            getRootByIndex(2).llvm();
            String op1 = lastOp();
            String op2 = null;
            if (getChildren().size() == 7) {
                getRootByIndex(5).llvm();
                op2 = lastOp();
            }
            getTable().loadArray(getFirstToken().getValue(), op1, op2);
        }
    }

    @Override
    public int synthesize() {
        fillLineByIndex(0);
        if (getChildren().size() == 1) {
            return getTable().getSymbolValue(getFirstToken().getValue(), 0, 0);
        } else if (getChildren().size() >= 4) {
            int d1 = getRootByIndex(2).synthesize();
            int d2 = -1;
            if (getChildren().size() == 7) {
                d2 = getRootByIndex(5).synthesize();
            }
            return getTable().getSymbolValue(getFirstToken().getValue(), d1, d2);
        }
        return 0;
    }

    @Override
    public int getLevel() {
        int l = getTable().getLevel(getFirstToken().getValue());
        switch (getChildren().size()) {
            case 1:
                return l;
            case 4:
                if (l > 1) {
                    return 1;
                }
                return 0;
            default:
                return 0;
        }
    }

    public void store(String lo) {
        String d1 = null;
        String d2 = null;
        if (getChildren().size() >= 4) {
            getRootByIndex(2).llvm();
            d1 = lastOp();
            if (getChildren().size() == 7) {
                getRootByIndex(5).llvm();
                d2 = lastOp();
            }
        }
        fillLineByIndex(0);
        getTable().storeSymbol(getFirstToken().getValue(), lo, d1, d2);
    }
}
