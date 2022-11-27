package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class PrimaryExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.PrimaryExp;
    }

    @Override
    public void llvm() {
        if (getChildren().size() == 1) {
            getRootByIndex(0).llvm();
        } else {
            getRootByIndex(1).llvm();
        }
    }

    @Override
    public int synthesize() {
        if (getChildren().size() == 1) {
            return getRootByIndex(0).synthesize();
        } else {
            return getRootByIndex(1).synthesize();
        }
    }

    @Override
    public int getLevel() {
        if (getChildren().size() == 3) {
            return getRootByIndex(1).getLevel();
        } else {
            return getRootByIndex(0).getLevel();
        }
    }
}
