package main.models.common.llvm.define;

import main.models.common.llvm.ir.BrIr;
import main.models.common.llvm.ir.IR;
import main.models.common.llvm.ir.ReturnIr;
import main.utils.Printer;

import java.util.ArrayList;

public class BasicBlock {
    private final String blockNum;
    private final ArrayList<IR> irs;

    public BasicBlock(String blockNum) {
        this.blockNum = blockNum;
        irs = new ArrayList<>();
    }

    public void addIr(IR ir) {
        irs.add(ir);
    }

    public void print() {
        Printer.getInstance().print("; <label>:" + blockNum.substring(1) + ":\n");
        for (IR ir : irs) {
            Printer.getInstance().print(ir.toString());
        }
    }

    public boolean isEmpty() {
        return irs.isEmpty();
    }

    public boolean isValid() {
        if (irs.size() != 0) {
            IR last = irs.get(irs.size() - 1);
            return last instanceof BrIr || last instanceof ReturnIr;
        }
        return false;
    }
}
