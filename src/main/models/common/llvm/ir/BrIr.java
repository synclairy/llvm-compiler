package main.models.common.llvm.ir;

import main.models.common.llvm.IrList;
import main.models.common.llvm.Labels;

public class BrIr implements IR {
    private String cond;
    private String ifTrue;
    private String ifFalse;

    private Labels labels;
    private final String type;

    private String op1;
    private String op2;

    private String dest;

    public BrIr(String type, Labels labels, String op1, String op2) {
        this.type = type;
        this.labels = labels;
        cond = IrList.lastOp();
        if (op1.charAt(0) == '%') {
            ifTrue = op1;
        }
        this.op1 = op1;
        this.op2 = op2;
    }

    public BrIr(String cond, Labels labels, String type) {
        this.cond = cond;
        this.labels = labels;
        this.type = type;
    }

    public BrIr(String dest) {
        this.dest = dest;
        this.type = "dest";
    }

    public BrIr(Labels labels, String type) {
        this.labels = labels;
        this.type = type;
    }

    @Override
    public String toString() {
        switch (type) {
            case "dest":
                return "\tbr label " + dest + "\n";
            case "ret":
                return "\tbr label " + labels.getRetLabel() + "\n";
            case "ifEnd":
            case "elseEnd":
                return "\tbr label " + labels.getIfEnd() + "\n";
            case "whileBegin":
            case "continue":
                return "\tbr label " + labels.getCondLabel() + "\n";
            case "break":
                return "\tbr label " + labels.getWhileEnd() + "\n";
            case "whileJudge":
                if (op1.equals("body")) {
                    ifTrue = labels.getBodyLabel();
                }
                if (op2.equals("end")) {
                    ifFalse = labels.getWhileEnd();
                } else {
                    ifFalse = labels.getOrLabel(op2);
                }
                break;
            case "ifElseJudge":
                if (op1.equals("body")) {
                    ifTrue = labels.getIfLabel();
                }
                if (op2.equals("end")) {
                    ifFalse = labels.getElseLabel();
                } else {
                    ifFalse = labels.getOrLabel(op2);
                }
                break;
            case "ifJudge":
                if (op1.equals("body")) {
                    ifTrue = labels.getIfLabel();
                }
                if (op2.equals("end")) {
                    ifFalse = labels.getIfEnd();
                } else {
                    ifFalse = labels.getOrLabel(op2);
                }
                break;
            default:
        }
        return "\tbr i1 " + cond + ", label " + ifTrue + ", label " + ifFalse + "\n";
    }
}
