package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.ICmpIr;
import main.models.common.llvm.ir.SubIr;
import main.models.common.llvm.ir.ZextIr;

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
            } else if (getFirstToken().getCode().equals(TCode.NOT)) {
                addIr(new ICmpIr("eq", lastOp(), "0"));
                addIr(new ZextIr(1, 32, lastOp()));
            }
        } else {
            ArrayList<String> params = new ArrayList<>();
            ArrayList<Integer> levels = new ArrayList<>();
            if (getChildren().size() != 3) {
                ArrayList<TreeNode> temp = (getRootByIndex(2)).getChildren();
                for (TreeNode node : temp) {
                    if (node instanceof ExpNode) {
                        node.llvm();
                        params.add(lastOp());
                        levels.add(((ExpNode) node).getLevel());
                    }
                }
            }
            fillLineByIndex(0);
            if (getFirstToken().getCode().equals(TCode.GETINTTK)) {
                getGlobal().callFunc("getint", params, levels);
            } else {
                getGlobal().callFunc(getFirstToken().getValue(), params, levels);
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

    @Override
    public int getLevel() {
        if (getChildren().size() > 2) {
            return getTable().getLevel(getFirstToken().getValue());
        } else if (getChildren().size() == 2) {
            return getRootByIndex(1).getLevel();
        }
        return getRootByIndex(0).getLevel();
    }
}
