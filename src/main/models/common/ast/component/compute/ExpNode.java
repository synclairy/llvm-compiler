package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class ExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Exp;
    }

    @Override
    public void llvm() {
        getRootByIndex(0).llvm();
    }

    @Override
    public int synthesize() {
        return getRootByIndex(0).synthesize();
    }
}
