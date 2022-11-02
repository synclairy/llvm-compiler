package main.models.common.llvm;

import main.models.common.symbol.SymbolItem;
import main.models.common.symbol.VariableSymbol;

public class GlobalDefIr implements IR {
    private final SymbolItem item;

    public GlobalDefIr(SymbolItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        if (item instanceof VariableSymbol) {
            return item.getReg() + " = dso_local global i32 "
                    + ((VariableSymbol) item).getValue() + "\n";
        }
        return "";
    }
}
