package main.models.common.ast.component.flow;

import main.models.common.ast.NCode;
import main.models.common.ast.Token;
import main.models.common.ast.TreeNode;
import main.models.common.ast.TreeRoot;
import main.models.common.ast.component.compute.CondNode;
import main.models.common.ast.component.compute.LValNode;
import main.models.common.handler.ErrorInfoList;
import main.models.common.llvm.Labels;
import main.models.common.llvm.ir.BrIr;
import main.utils.CharClassifier;

import java.util.ArrayList;

public class StmtNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Stmt;
    }

    public void llvmWithLabels(Labels labels) {
        Token token = getFirstToken();
        switch (token.getCode()) {
            case BREAKTK:
                if (labels.getCondLabel() == null) {
                    ErrorInfoList.getInstance().addError('m', token.getLine());
                } else {
                    addIr(new BrIr(labels, "break"));
                }
                break;
            case CONTINUETK:
                if (labels.getCondLabel() == null) {
                    ErrorInfoList.getInstance().addError('m', token.getLine());
                } else {
                    addIr(new BrIr(labels, "continue"));
                }
                break;
            case IFTK:
                Labels child = new Labels(labels);
                if (getChildren().size() > 5) {
                    ((CondNode) getRootByIndex(2)).llvmWithLabels(child, "ifElseJudge");
                    child.updateIfLabel();
                    ((StmtNode) getRootByIndex(4)).llvmWithLabels(child);
                    child.updateElseLabel();
                    ((StmtNode) getRootByIndex(6)).llvmWithLabels(child);
                } else {
                    ((CondNode) getRootByIndex(2)).llvmWithLabels(child, "ifJudge");
                    child.updateIfLabel();
                    ((StmtNode) getRootByIndex(4)).llvmWithLabels(child);
                }
                labels.updateIfEnd();
                break;
            case WHILETK:
                child = new Labels(labels);
                ((CondNode) getRootByIndex(2)).llvmWithLabels(child, "whileJudge");
                child.updateBodyLabel();
                ((StmtNode) getRootByIndex(4)).llvmWithLabels(child);
                child.updateWhileEnd();
                break;
            case RETURNTK:
                fillLineByIndex(0);
                if (getChildren().size() == 3) {
                    getRootByIndex(1).llvm();
                    getTable().returnValue(lastOp(), labels);
                } else {
                    getTable().returnValue(null, labels);
                }
                break;
            case IDENFR:
                TreeNode lv = getChildren().get(0);
                if (lv instanceof LValNode) {
                    if (getTokenByIndex(2) != null) {
                        getGlobal().callFunc("getint", new ArrayList<>(), new ArrayList<>());
                    } else {
                        getRootByIndex(2).llvm();
                    }
                    ((LValNode) lv).store();
                } else {
                    fillLineByIndex(1);
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
                    ArrayList<String> temp1 = new ArrayList<>();
                    ArrayList<Integer> temp2 = new ArrayList<>();
                    temp2.add(0);
                    if (c == null) {
                        if (count < params.size()) {
                            temp1.add(params.get(count++));
                        } else {
                            temp1.add("0");
                        }
                        getGlobal().callFunc("putint", temp1, temp2);
                    } else {
                        temp1.add(c);
                        getGlobal().callFunc("putch", temp1, temp2);
                    }
                }
                break;
            case LBRACE:
                TreeRoot block = getRootByIndex(0);
                ((BlockNode) block).llvmWithLabels(labels);
                break;
            default:
        }
    }

    @Override
    public void llvm() {
    }
}
