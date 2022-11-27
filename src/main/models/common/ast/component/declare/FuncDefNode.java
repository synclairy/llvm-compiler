package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.ast.component.flow.BlockNode;
import main.models.common.llvm.IrList;
import main.models.common.llvm.Labels;
import main.models.common.llvm.ir.LoadIr;
import main.models.common.llvm.ir.ReturnIr;

import java.util.ArrayList;

public class FuncDefNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncDef;
    }

    @Override
    public void llvm() {
        getTable().fillType(getFirstToken().getCode());
        fillLineByIndex(1);
        getTable().fillName(getTokenByIndex(1).getValue());
        IrList.clearCount();
        travelSal1(NCode.FuncFParams);
        getTable().fillOver(NCode.FuncDef);

        TreeRoot b = (TreeRoot) getChildren().get(getChildren().size() - 1);
        getTable().setChildTableParams(b.getTable());
        TreeRoot block = getRootByIndex(getChildren().size() - 1);
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
        if (getFirstToken().getCode().equals(TCode.INTTK)) {
            addIr(new LoadIr(labels.getRetReg()));
            addIr(new ReturnIr(lastOp()));
        } else {
            addIr(new ReturnIr());
        }

    }
}
