package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.ICmpIr;
import main.models.common.llvm.ir.ZextIr;

public class EqExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.EqExp;
    }

    @Override
    public void llvm() {
        getRootByIndex(0).llvm();
        if (getChildren().size() != 1) {
            String op1 = lastOp();
            getRootByIndex(2).llvm();
            String op2 = lastOp();
            if (getTokenByIndex(1).getCode().equals(TCode.EQL)) {
                addIr(new ICmpIr("eq", op1, op2));
                addIr(new ZextIr(1, 32, lastOp()));
            }
        }
    }
}
