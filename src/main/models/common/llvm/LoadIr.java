package main.models.common.llvm;

public class LoadIr implements IR {
    private final String op1;
    private final String resultReg;

    public LoadIr(String op1) {
        this.op1 = op1;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        return "\t" + resultReg + " = load i32, i32* " + op1 + "\n";
    }
}
