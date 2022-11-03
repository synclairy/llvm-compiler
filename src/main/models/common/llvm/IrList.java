package main.models.common.llvm;

import main.models.common.symbol.SymbolItem;

import java.util.ArrayList;

public class IrList {
    private static final IrList IR_LIST = new IrList();
    private static int regCount = 0;
    private static String lastOp;
    private FunctionDefine func;
    private final ArrayList<Define> defines = new ArrayList<>();

    public static IrList getInstance() {
        return IR_LIST;
    }

    public void addIr(IR ir) {
        func.addIr(ir);
    }

    public void addGlobal(GlobalDefine define) {
        defines.add(define);
    }

    public void addFuncDef(FunctionDefine define) {
        defines.add(define);
        func = define;
    }

    public void addFuncDecl(FunctionDeclare define) {
        defines.add(define);
    }

    public void print() {
        for (Define define : defines) {
            define.print();
        }
    }

    public static void clearCount() {
        IrList.regCount = 0;
    }

    public void newBlock(String n) {
        func.newBlock(n);
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
