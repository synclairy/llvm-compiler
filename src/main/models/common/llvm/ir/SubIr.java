package main.models.common.llvm.ir;

import main.models.common.llvm.IrList;

public class SubIr implements IR {
    private final String op1;
    private final String op2;
    private final String resultReg;

    public SubIr(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        String s = op1 + ", ";
        s += op2 + "\n";
        return "\t" + resultReg + " = sub i32 " + s;
    }
}
