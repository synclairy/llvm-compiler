package main.models.common.llvm.ir;

public class AllocaIr implements IR {
    private String reg;
    private int d1 = -1;
    private int d2 = -1;
    private String name;

    public AllocaIr(String reg, String name) {
        this.reg = reg;
        this.name = name;
    }

    public AllocaIr(String reg, int d1, String name) {
        this.reg = reg;
        this.d1 = d1;
        this.name = name;
    }

    public AllocaIr(String reg, int d1, int d2, String name) {
        this.reg = reg;
        this.d1 = d1;
        this.d2 = d2;
        this.name = name;
    }

    @Override
    public String toString() {
        if (d2 != -1) {
            return "\t" + reg + " = alloca " + "[" + d1 + " x " +
                    "[" + d2 + " x i32]] ;" + name + "\n";
        } else if (d1 != -1) {
            return "\t" + reg + " = alloca " + "[" + d1 + " x " +
                     " x i32] ;" + name + "\n";
        } else {
            return "\t" + reg + " = alloca i32 ;" + name + "\n";
        }
    }
}
