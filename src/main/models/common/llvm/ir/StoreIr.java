package main.models.common.llvm.ir;

import main.models.common.llvm.Labels;

public class StoreIr implements IR {
    private final String op1;
    private final String op2;
    private Labels labels;

    public StoreIr(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    public StoreIr(String op1, Labels labels) {
        this.op1 = op1;
        this.op2 = labels.getRetReg();
    }

    @Override
    public String toString() {
        return "\tstore i32 " + op1 + ", i32* " + op2 + "\n";
    }
}
