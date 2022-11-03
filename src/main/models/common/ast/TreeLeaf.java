package main.models.common.ast;

import main.utils.Printer;

public class TreeLeaf implements TreeNode {
    private final Token token;

    @Override
    public void print() {
        Printer.getInstance().print(token.toString());
    }

    @Override
    public void llvm() {

    }

    public TreeLeaf(Token token) {
        this.token = token;
    }

    public String getIdent() {
        if (token.getCode().equals(TCode.IDENFR)) {
            return token.getValue();
        }
        return null;
    }

    public Token getToken() {
        return token;
    }

    public int getNum() {
        if (token.getCode().equals(TCode.INTCON)) {
            return Integer.parseInt(token.getValue());
        }
        return 0;
    }
}
