package main.models.common.ast.component.flow;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.Token;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.llvm.ir.ReturnIr;
import main.utils.CharClassifier;

import java.util.ArrayList;

public class StmtNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Stmt;
    }

    @Override
    public void llvm() {
        Token token = getFirstToken();
        switch (token.getCode()) {
            case RETURNTK:
                if (getChildren().size() == 3) {
                    getRootByIndex(1).llvm();
                    addIr(new ReturnIr(lastOp()));
                    getTable().setNeedRet(1);
                } else {
                    addIr(new ReturnIr());
                    getTable().setNeedRet(0);
                }
                break;
            case IDENFR:
                if (TCode.ASSIGN.equals(getCodeByIndex(1))) {
                    if (getTokenByIndex(2) != null) {
                        getGlobal().callFunc("getint", new ArrayList<>());
                    } else {
                        getRootByIndex(2).llvm();
                    }
                    getTable().storeSymbol(token.getValue(), lastOp());
                } else {
                    travelSal1(NCode.Exp);
                }
                break;
            case PRINTFTK:
                ArrayList<String> chars = CharClassifier
                        .fixFormatString(getTokenByIndex(2).getValue());
                ArrayList<String> params = new ArrayList<>();
                for (TreeNode node : getChildren()) {
                    if (node instanceof TreeRoot) {
                        ((TreeRoot) node).llvm();
                        params.add(lastOp());
                    }
                }
                //TODO
                int count = 0;
                for (String c : chars) {
                    ArrayList<String> temp = new ArrayList<>();
                    if (c == null) {
                        temp.add(params.get(count++));
                        getGlobal().callFunc("putint", temp);
                    } else {
                        temp.add(c);
                        getGlobal().callFunc("putch", temp);
                    }
                }
                break;
            case LBRACE:
                travelSal1(NCode.Block);
                break;
            default:
        }
    }
}
