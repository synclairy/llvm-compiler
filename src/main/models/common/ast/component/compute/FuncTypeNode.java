package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class FuncTypeNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncType;
    }

    @Override
    public void llvm() {

    }
}
