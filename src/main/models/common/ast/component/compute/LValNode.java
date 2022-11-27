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
        //} else if (TCode.LPARENT.equals(getCodeByIndex(1))) {
        }
    }

    @Override
    public int synthesize() {
        fillLineByIndex(0);
        if (getChildren().size() == 1) {
            return getTable().getSymbolValue(getFirstToken().getValue(), 0, 0);
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

    public void store() {
        String lo = lastOp();
        int d1 = 0;
        int d2 = 0;
        if (getChildren().size() == 4) {
            d1 = ((TreeRoot) getChildren().get(2)).synthesize();
        } else if (getChildren().size() == 7) {
            d2 = ((TreeRoot) getChildren().get(5)).synthesize();
        }
        fillLineByIndex(0);
        getTable().storeSymbol(getFirstToken().getValue(), lo, d1, d2);
        // TODO
    }
}
