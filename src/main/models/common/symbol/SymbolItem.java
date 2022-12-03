package main.models.common.symbol;

import main.models.common.llvm.IrList;
import main.models.common.llvm.ir.IR;

import java.util.ArrayList;

public interface SymbolItem {
    void declare(String type);

    String getReg();

    String getName();

    void setValues(ArrayList<Integer> values);

    default void addIr(IR ir) {
        IrList.getInstance().addIr(ir);
    }
}
