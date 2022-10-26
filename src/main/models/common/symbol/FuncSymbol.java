package main.models.common.symbol;

import main.models.common.TCode;

import java.util.HashMap;

public class FuncSymbol implements SymbolItem {
    private TCode returnType;
    private String name;
    private HashMap<String, SymbolItem> params;

    public FuncSymbol(TCode returnType, String name, HashMap<String, SymbolItem> params) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
    }
}
