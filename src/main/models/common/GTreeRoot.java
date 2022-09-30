package main.models.common;

import main.utils.Printer;

import java.util.ArrayList;

public class GTreeRoot implements GTreeNode {
    private final ArrayList<GTreeNode> children;
    private final NCode code;

    @Override
    public void print() {
        for (GTreeNode node : children) {
            node.print();
        }
        Printer.getInstance().print(code.toString());
    }

    public GTreeRoot(NCode code) {
        children = new ArrayList<>();
        this.code = code;
    }

    public GTreeRoot(NCode code, Token token) {
        children = new ArrayList<>();
        children.add(new GTreeLeaf(token));
        this.code = code;
    }

    public void addChild(GTreeNode node) {
        children.add(node);
    }
}
