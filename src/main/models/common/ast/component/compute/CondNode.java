package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class CondNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Cond;
    }

    @Override
    public void llvm() {

    }
}