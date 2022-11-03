package main.models.common.ast;

import main.models.common.ast.component.flow.BlockNode;
import main.models.common.llvm.ir.IR;
import main.models.common.llvm.IrList;
import main.models.common.symbol.SymbolTable;
import main.utils.Printer;

import java.util.ArrayList;

public abstract class TreeRoot implements TreeNode {
    private final ArrayList<TreeNode> children;
    private SymbolTable table;
    private static SymbolTable global;

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public static SymbolTable getGlobal() {
        return global;
    }

    public void setTable(SymbolTable table) {
        this.table = table;
    }

    public SymbolTable getTable() {
        return table;
    }

    public int synthesize() {
        return 0;
    }

    public void travelSal1(NCode code) {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                if (((TreeRoot) node).getCode().equals(code)) {
                    node.llvm();
                }
            }
        }
    }

    public void travelSal2(NCode code1, NCode code2) {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                if (((TreeRoot) node).getCode().equals(code1)
                        || ((TreeRoot) node).getCode().equals(code2)) {
                    node.llvm();
                }
            }
        }
    }

    public void travelSalAll() {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                (node).llvm();
            }
        }
    }

    public void addIr(IR ir) {
        IrList.getInstance().addIr(ir);
    }

    public Token getFirstToken() {
        TreeNode node = children.get(0);
        if (node instanceof TreeLeaf) {
            return ((TreeLeaf) children.get(0)).getToken();
        }
        else if (node instanceof TreeRoot) {
            return ((TreeRoot) node).getFirstToken();
        }
        return null;
    }

    public TreeRoot getRootByIndex(int i) {
        if (i >= children.size()) {
            return null;
        }
        return ((TreeRoot) children.get(i));
    }

    public Token getTokenByIndex(int i) {
        if (i >= children.size()) {
            return null;
        }
        TreeNode node = children.get(i);
        if (node instanceof TreeLeaf) {
            return ((TreeLeaf) children.get(i)).getToken();
        }
        else {
            return null;
        }
    }

    public TCode getCodeByIndex(int i) {
        if (getTokenByIndex(i) == null) {
            return null;
        }
        return getTokenByIndex(i).getCode();
    }

    public String lastOp() {
        return IrList.lastOp();
    }

    @Override
    public void print() {
        for (TreeNode node : children) {
            node.print();
        }
        Printer.getInstance().print(getCode().toString());
    }

    public NCode getCode() {
        return null;
    }

    public TreeRoot() {
        children = new ArrayList<>();
    }

    public void addChild(TreeNode node) {
        children.add(node);
        if (node instanceof TreeRoot) {
            if (node instanceof BlockNode) {
                ((TreeRoot) node).setTable(table.createChildTable());
            } else {
                ((TreeRoot) node).setTable(table);
            }
        }
    }

    public static void setGlobal(SymbolTable global) {
        TreeRoot.global = global;
    }
}
