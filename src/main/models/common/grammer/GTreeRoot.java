package main.models.common.grammer;

import main.models.common.NCode;
import main.models.common.symbol.SymbolTable;
import main.utils.Printer;

import java.util.ArrayList;

public class GTreeRoot implements GTreeNode {
    private final ArrayList<GTreeNode> children;
    private final NCode code;
    private GTreeNode lastChild = null;
    private final SymbolTable table;

    @Override
    public void print() {
        for (GTreeNode node : children) {
            node.print();
        }
        Printer.getInstance().print(code.toString());
    }

    public GTreeRoot(NCode code, SymbolTable table) {
        children = new ArrayList<>();
        this.code = code;
        this.table = table;
    }

    public void addChild(GTreeNode node) {
        children.add(node);
        lastChild = node;
    }

    @Override
    public GTreeNode getLastChild() {
        return lastChild;
    }
}
