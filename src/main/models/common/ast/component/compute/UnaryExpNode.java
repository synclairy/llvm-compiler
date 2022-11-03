package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.SubIr;

import java.util.ArrayList;

public class UnaryExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.UnaryExp;
    }

    @Override
    public void llvm() {
        if (getChildren().size() == 1) {
            getRootByIndex(0).llvm();
        } else if (getChildren().size() == 2) {
            getRootByIndex(1).llvm();
            if (getFirstToken().getCode().equals(TCode.MINU)) {
                addIr(new SubIr("0", lastOp()));
            }
        } else {
            ArrayList<String> params = new ArrayList<>();
            if (getChildren().size() != 3) {
                ArrayList<TreeNode> temp = ((TreeRoot) getChildren().get(2)).getChildren();
                for (TreeNode node : temp) {
                    if (node instanceof TreeRoot) {
                        ((TreeRoot) node).llvm();
                        params.add(lastOp());
                    }
                }
            }
            if (getFirstToken().getCode().equals(TCode.GETINTTK)) {
                getGlobal().callFunc("getint", params);
            } else {
                getGlobal().callFunc(getFirstToken().getValue(), params);
            }
        }
    }

    @Override
    public int synthesize() {
        if (getChildren().size() == 1) {
            return getRootByIndex(0).synthesize();
        } else if (getChildren().size() == 2) {
            int n = getRootByIndex(1).synthesize();
            if (getFirstToken().getCode().equals(TCode.MINU)) {
                n = -1 * n;
            }
            return n;
        } else {
            return 0;
        }
    }
}
