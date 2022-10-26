package main.models.common.grammer;

import main.models.common.Token;
import main.utils.Printer;

public class GTreeLeaf implements GTreeNode {
    private final Token token;

    @Override
    public void print() {
        Printer.getInstance().print(token.toString());
    }

    @Override
    public GTreeNode getLastChild() {
        return this;
    }

    public GTreeLeaf(Token token) {
        this.token = token;
    }
}
