package main.models.common.llvm;

public class MulIr implements IR {
    private final String op1;
    private final String op2;
    private final String resultReg;

    public MulIr(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        String s = op1 + ", ";
        s += op2 + "\n";
        return "\t" + resultReg + " = mul i32 " + s;
    }
}
