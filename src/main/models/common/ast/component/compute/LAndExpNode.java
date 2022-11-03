package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class LAndExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.LAndExp;
    }

    @Override
    public void llvm() {

    }
}
