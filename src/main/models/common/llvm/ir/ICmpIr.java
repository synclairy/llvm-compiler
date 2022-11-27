package main.models.common.llvm.ir;

import main.models.common.llvm.IrList;

public class ICmpIr implements IR {
    private final String type;
    private final String op1;
    private final String op2;
    private final String result;

    public ICmpIr(String type, String op1, String op2) {
        this.type = type;
        this.op1 = op1;
        this.op2 = op2;
        result = IrList.newReg();
    }

    @Override
    public String toString() {
        return "\t" + result + " = icmp " + type + " i32 " + op1 + ", " + op2 + "\n";
    }
}
