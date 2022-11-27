package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.IrList;

public class NumberNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Number;
    }

    @Override
    public void llvm() {
        IrList.setLastOp(getFirstToken().getValue());
    }

    @Override
    public int synthesize() {
        return Integer.parseInt(getFirstToken().getValue());
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
