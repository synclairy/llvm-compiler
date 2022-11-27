package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class InitValNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.InitVal;
    }

    @Override
    public void llvm() {
        if (getChildren().size() == 1) {
            if (getTable().isGlobal()) {
                getTable().fillInitValue(getRootByIndex(0).synthesize());
            } else {
                getRootByIndex(0).llvm();
                getTable().storeSymbol(null, lastOp(), 0, 0);
            }
        } else {
            travelSal1(NCode.InitVal);
        }
    }
}
