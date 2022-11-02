package main.models.common.llvm;

import main.utils.Printer;

import java.util.ArrayList;

public class IrList {
    private static final IrList IR_LIST = new IrList();
    private final ArrayList<IR> irs = new ArrayList<>();
    private static int regCount = 0;
    private static String lastOp;


    public static IrList getInstance() {
        return IR_LIST;
    }

    public void addIr(IR ir) {
        irs.add(ir);
    }

    public void print() {
        for (IR ir : irs) {
            Printer.getInstance().print(ir.toString());
        }
    }

    public static void clearCount() {
        IrList.regCount = 0;
    }

    public static String newReg() {
        lastOp =  "%" + IrList.regCount;
        ++IrList.regCount;
        return lastOp;
    }

    public static String lastOp() {
        return lastOp;
    }

    public static void setLastOp(String lastOp) {
        IrList.lastOp = lastOp;
    }
}
