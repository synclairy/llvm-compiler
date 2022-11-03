package main.models.common.ast.component.flow;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class CompUnitNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.CompUnit;
    }

    @Override
    public void llvm() {
        travelSal2(NCode.VarDecl, NCode.ConstDecl);
        getTable().addLibFunc();
        travelSal2(NCode.FuncDef, NCode.MainFuncDef);
    }
}
