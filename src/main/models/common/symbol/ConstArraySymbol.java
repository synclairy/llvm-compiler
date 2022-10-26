package main.models.common.symbol;

import main.models.common.grammer.GTreeNode;

public class ConstArraySymbol implements SymbolItem {
    private String name;
    private int dCount;
    private GTreeNode d1;
    private GTreeNode d2;

    public ConstArraySymbol(String name, int count, GTreeNode d1, GTreeNode d2) {
        this.name = name;
        this.dCount = count;
        this.d1 = d1;
        this.d2 = d2;
    }
}
