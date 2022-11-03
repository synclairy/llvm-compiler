package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class FuncRParamsNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncRParams;
    }

    @Override
    public void llvm() {

    }
}
