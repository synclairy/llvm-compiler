package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class VarDeclNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.VarDecl;
    }

    @Override
    public void llvm() {
        travelSal1(NCode.VarDef);
    }
}
