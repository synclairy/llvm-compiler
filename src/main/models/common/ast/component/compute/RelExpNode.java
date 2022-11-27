package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.ICmpIr;
import main.models.common.llvm.ir.ZextIr;

public class RelExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.RelExp;
    }

    @Override
    public void llvm() {
        getRootByIndex(0).llvm();
        if (getChildren().size() != 1) {
            String op1 = lastOp();
            getRootByIndex(2).llvm();
            String op2 = lastOp();
            switch (getTokenByIndex(1).getCode()) {
                case LSS:
                    addIr(new ICmpIr("slt", op1, op2));
                    break;
                case LEQ:
                    addIr(new ICmpIr("sle", op1, op2));
                    break;
                case GRE:
                    addIr(new ICmpIr("slt", op2, op1));
                    break;
                case GEQ:
                    addIr(new ICmpIr("sle", op2, op1));
                    break;
                default:
            }
            addIr(new ZextIr(1, 32, lastOp()));
        }
    }
}
