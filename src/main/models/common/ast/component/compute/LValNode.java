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
        if (getChildren().size() == 1) {
            getTable().loadSymbol(getFirstToken().getValue());
        //} else if (TCode.LPARENT.equals(getCodeByIndex(1))) {
        }
    }

    @Override
    public int synthesize() {
        if (getChildren().size() == 1) {
            return getTable().getSymbolValue(getFirstToken().getValue(), 0, 0);
        }
        return 0;
    }
}
