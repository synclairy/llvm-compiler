package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class UnaryOpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.UnaryOp;
    }

    @Override
    public void llvm() {

    }
}
