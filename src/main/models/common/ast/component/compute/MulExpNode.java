package main.models.common.ast.component.compute;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.MulIr;
import main.models.common.llvm.ir.SdivIr;
import main.models.common.llvm.ir.SubIr;

public class MulExpNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.MulExp;
    }

    @Override
    public void llvm() {
        if (getChildren().size() == 1) {
            getRootByIndex(0).llvm();
        } else {
            getRootByIndex(0).llvm();
            String s1 = lastOp();
            getRootByIndex(2).llvm();
            String s2 = lastOp();
            TCode code = getCodeByIndex(1);
            if (code.equals(TCode.MULT)) {
                addIr(new MulIr(s1, s2));
            } else if (code.equals(TCode.DIV)) {
                addIr(new SdivIr(s1, s2));
            } else {
                addIr(new SdivIr(s1, s2));
                addIr(new MulIr(lastOp(), s2));
                addIr(new SubIr(s1, lastOp()));
            }
        }
    }

    @Override
    public int synthesize() {
        if (getChildren().size() == 1) {
            return getRootByIndex(0).synthesize();
        } else {
            int s1 = getRootByIndex(0).synthesize();
            int s2 = getRootByIndex(2).synthesize();
            TCode tcode = getCodeByIndex(1);
            if (tcode.equals(TCode.MULT)) {
                return s1 * s2;
            } else if (tcode.equals(TCode.DIV)) {
                return s1 / s2;
            } else {
                return s1 % s2;
            }
        }
    }
}
