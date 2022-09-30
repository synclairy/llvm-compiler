package main.models.common;

import main.utils.Printer;

public class GTreeLeaf implements GTreeNode {
    private final Token token;

    @Override
    public void print() {
        Printer.getInstance().print(token.toString());
    }

    public GTreeLeaf(Token token) {
        this.token = token;
    }
}
