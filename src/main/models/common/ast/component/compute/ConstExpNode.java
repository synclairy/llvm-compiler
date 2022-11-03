package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class ConstExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.ConstExp;
    }

    @Override
    public void llvm() {

    }

    @Override
    public int synthesize() {
        return getRootByIndex(0).synthesize();
    }
}
