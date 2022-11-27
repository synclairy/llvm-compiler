package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

import java.util.ArrayList;

public class LOrExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.LOrExp;
    }

    @Override
    public void llvm() {

    }

    public void getAllEqExp(ArrayList<ArrayList<EqExpNode>> list) {
        int n;
        if (getChildren().size() == 1) {
            n = 0;
        } else {
            ((LOrExpNode) getRootByIndex(0)).getAllEqExp(list);
            n = 2;
        }
        ArrayList<EqExpNode> andList = new ArrayList<>();
        ((LAndExpNode) getRootByIndex(n)).getAllEqExp(andList);
        list.add(andList);
    }
}
