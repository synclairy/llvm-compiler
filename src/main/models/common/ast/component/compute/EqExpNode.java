package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class EqExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.EqExp;
    }

    @Override
    public void llvm() {

    }
}
