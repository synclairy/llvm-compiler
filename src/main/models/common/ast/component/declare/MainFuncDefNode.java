package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.IrList;
import main.models.common.llvm.define.FunctionDefine;

import java.util.ArrayList;

public class MainFuncDefNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.MainFuncDef;
    }

    @Override
    public void llvm() {
        IrList.clearCount();
        IrList.getInstance().addFuncDef(
                new FunctionDefine(false, "main", new ArrayList<>()));
        getRootByIndex(4).llvm();
    }
}
