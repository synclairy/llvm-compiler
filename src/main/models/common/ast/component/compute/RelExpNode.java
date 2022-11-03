package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class RelExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.RelExp;
    }

    @Override
    public void llvm() {

    }
}
