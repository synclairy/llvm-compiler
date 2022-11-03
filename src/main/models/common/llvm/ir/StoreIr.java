package main.models.common.llvm.ir;

public class StoreIr implements IR {
    private final String op1;
    private final String op2;

    public StoreIr(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toString() {
        return "\tstore i32 " + op1 + ", i32* " + op2 + "\n";
    }
}
