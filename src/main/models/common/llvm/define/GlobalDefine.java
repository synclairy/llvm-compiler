package main.models.common.llvm.define;

import main.models.common.symbol.ArraySymbol;
import main.models.common.symbol.ConstArraySymbol;
import main.models.common.symbol.SymbolItem;
import main.models.common.symbol.VariableSymbol;
import main.utils.Printer;

public class GlobalDefine implements Define {
    private final SymbolItem item;

    public GlobalDefine(SymbolItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        if (item instanceof VariableSymbol) {
            return item.getReg() + " = dso_local global i32 "
                    + ((VariableSymbol) item).getValue() + "\n";
        } else if (item instanceof ConstArraySymbol) {
            return item.getReg() +  " = dso_local constant ["
                    + ((ConstArraySymbol) item).getLen() + " x i32] " +
                    ((ConstArraySymbol) item).getInitString();

        } else if (item instanceof ArraySymbol) {
            return item.getReg() +  " = dso_local global ["
                    + ((ArraySymbol) item).getLen() + " x i32] " +
                    ((ArraySymbol) item).getInitString();
        }
        return "";
    }

    @Override
    public void print() {
        Printer.getInstance().print(toString());
    }
}
