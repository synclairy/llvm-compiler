package main.models.common.llvm;

public class ReturnIr implements IR {
    private String reg;
    private final boolean isVoid;

    public ReturnIr(String reg) {
        this.reg = reg;
        isVoid = false;
    }

    public ReturnIr() {
        isVoid = true;
    }

    @Override
    public String toString() {
        if (isVoid) {
            return "\tret void\n";
        } else {
            return "\tret i32 " + reg + "\n";
        }
    }
}
