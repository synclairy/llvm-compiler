package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class ConstInitValNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.ConstInitVal;
    }

    @Override
    public void llvm() {
        if (getChildren().size() == 1) {
            getTable().fillInitValue(synthesize());
        } else {
            travelSal1(NCode.ConstInitVal);
        }
    }
}
