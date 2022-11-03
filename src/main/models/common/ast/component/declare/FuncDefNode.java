package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.IrList;

public class FuncDefNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncDef;
    }

    @Override
    public void llvm() {
        getTable().fillType(getFirstToken().getCode());
        getTable().fillName(getTokenByIndex(1).getValue());
        IrList.clearCount();
        travelSal1(NCode.FuncFParams);
        getTable().fillOver(NCode.FuncDef);
        TreeRoot b = (TreeRoot) getChildren().get(getChildren().size() - 1);
        getTable().setChildTableParams(b.getTable());
        travelSal1(NCode.Block);
    }
}
