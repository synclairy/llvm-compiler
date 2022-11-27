package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.ast.component.flow.BlockNode;
import main.models.common.llvm.IrList;
import main.models.common.llvm.Labels;
import main.models.common.llvm.define.FunctionDefine;
import main.models.common.llvm.ir.LoadIr;
import main.models.common.llvm.ir.ReturnIr;
import main.models.common.symbol.SymbolTable;

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
        SymbolTable.setRetInt(true);
        TreeRoot block = getRootByIndex(4);
        Labels labels = new Labels(null);
        ((BlockNode) block).llvmWithLabels(labels);

        ArrayList<TreeNode> gs = block.getChildren();
        block.fillLineByIndex(gs.size() - 1);
        if (gs.size() >= 3) {
            TreeRoot ret = block.getRootByIndex(gs.size() - 2);
            if (!ret.getFirstToken().getCode().equals(TCode.RETURNTK)) {
                block.getTable().returnValue(null, labels);
            }
        } else {
            block.getTable().returnValue(null, labels);
        }

        labels.updateRetLabel();
        addIr(new LoadIr(labels.getRetReg()));
        addIr(new ReturnIr(lastOp()));
    }
}
