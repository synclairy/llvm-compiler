package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class FuncFParamsNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncFParams;
    }

    @Override
    public void llvm() {
        travelSal1(NCode.FuncFParam);
    }
}
