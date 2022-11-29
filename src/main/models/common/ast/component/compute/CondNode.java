package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.IrList;
import main.models.common.llvm.Labels;
import main.models.common.llvm.ir.BrIr;
import main.models.common.llvm.ir.ICmpIr;

import java.util.ArrayList;

public class CondNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Cond;
    }

    @Override
    public void llvm() {

    }

    public void llvmWithLabels(Labels labels, String type) {
        ArrayList<ArrayList<EqExpNode>> eqs = new ArrayList<>();
        ((LOrExpNode) getRootByIndex(0)).getAllEqExp(eqs);
        if (type.equals("whileJudge")) {
            labels.updateCondLabel();
        }
        for (int i = 0; i < eqs.size(); i++) {
            if (i != 0) {
                labels.updateOrsLabel();
            }
            for (int j = 0; j < eqs.get(i).size(); j++) {
                if (j != 0) {
                    IrList.getInstance().newBlock();
                }
                eqs.get(i).get(j).llvm();
                addIr(new ICmpIr("ne", lastOp(), "0"));
                if (i != eqs.size() - 1) {
                    if (j != eqs.get(i).size() - 1) {
                        addIr(new BrIr(type, labels, IrList.preview(), String.valueOf(i)));
                    } else {
                        addIr(new BrIr(type, labels, "body", String.valueOf(i)));
                    }
                } else {
                    if (j != eqs.get(i).size() - 1) {
                        addIr(new BrIr(type, labels, IrList.preview(), "end"));
                    } else {
                        addIr(new BrIr(type, labels, "body", "end"));
                    }
                }
            }
        }
    }
}
