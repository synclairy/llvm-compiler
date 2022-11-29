package main.models.common.ast.component.flow;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.Labels;
import main.utils.CodeClassifier;

public class BlockNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Block;
    }

    public void llvmWithLabels(Labels labels) {
        getTable().declareParams(labels);
        labels.setInterrupted(false);
        for (TreeNode node : getChildren()) {
            if (node instanceof StmtNode) {
                ((StmtNode) node).llvmWithLabels(labels);
                TCode code = ((StmtNode) node).getFirstToken().getCode();
                if (CodeClassifier.isBr(code)) { // delete when error
                    break;
                }
            } else {
                node.llvm();
            }
        }
    }

    @Override
    public void llvm() {

    }
}
