package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

import java.util.ArrayList;

public class LAndExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.LAndExp;
    }

    @Override
    public void llvm() {

    }

    public void getAllEqExp(ArrayList<EqExpNode> list) {
        int n;
        if (getChildren().size() == 1) {
            n = 0;
        } else {
            ((LAndExpNode) getRootByIndex(0)).getAllEqExp(list);
            n = 2;
        }
        list.add((EqExpNode) getRootByIndex(n));
    }
}
