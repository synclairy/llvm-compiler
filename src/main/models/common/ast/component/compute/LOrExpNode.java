package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class LOrExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.LOrExp;
    }

    @Override
    public void llvm() {

    }
}
