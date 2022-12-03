package main.models.common.llvm.define;

import main.models.common.llvm.ir.BrIr;
import main.models.common.llvm.ir.IR;
import main.utils.Printer;

import java.util.ArrayList;

public class FunctionDefine implements Define {
    private final boolean isVoid;
    private final String name;
    private final ArrayList<Integer> levels;
    private final ArrayList<BasicBlock> blocks;
    private BasicBlock block;

    public FunctionDefine(boolean isVoid, String name, ArrayList<Integer> levels) {
        this.isVoid = isVoid;
        this.name = name;
        this.levels = levels;
        blocks = new ArrayList<>();
    }

    public void newBlock(String n) {
        if (block != null && !block.isValid()) {
            block.addIr(new BrIr(n));
        }
        block = new BasicBlock(n);
        blocks.add(block);
    }

    public void addIr(IR ir) {
        block.addIr(ir);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\ndefine dso_local ");
        s.append(isVoid ? "void @" : "i32 @");
        s.append(name).append("(");
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) == 0) {
                s.append("i32 ");
            } else {
                s.append("i32* ");
            }
            //s.append("%").append(i);
            if (i != levels.size() - 1) {
                s.append(", ");
            }
        }
        s.append(") #0 {\n");
        return s.toString();
    }

    @Override
    public void print() {
        Printer.getInstance().print(toString());
        for (BasicBlock block : blocks) {
            block.print();
        }
        Printer.getInstance().print("}\n");
    }
}
