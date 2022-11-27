package main.models.common.llvm.ir;

import main.models.common.llvm.IrList;

public class ZextIr implements IR {
    private final int t1;
    private final int t2;
    private final String op;
    private final String result;

    public ZextIr(int t1, int t2, String op) {
        this.t1 = t1;
        this.t2 = t2;
        this.op = op;
        result = IrList.newReg();
    }

    @Override
    public String toString() {
        return "\t" + result + " = zext i" + t1 + " " + op + " to i" + t2 + "\n";
    }
}
