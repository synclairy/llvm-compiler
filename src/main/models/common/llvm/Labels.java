package main.models.common.llvm;

import java.util.ArrayList;

public class Labels {
    private String ifLabel;
    private String elseLabel;
    private String ifEnd;

    private String condLabel;
    private String bodyLabel;
    private String whileEnd;

    private String retLabel;
    private String retReg;

    private final Labels father;

    private final ArrayList<String> orsLabel;

    private boolean interrupted = false;

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public String getOrLabel(String s) {
        return orsLabel.get(Integer.parseInt(s));
    }

    public void updateOrsLabel() {
        IrList.getInstance().newBlock();
        orsLabel.add(IrList.lastOp());
    }

    public Labels(Labels father) {
        this.father = father;
        orsLabel = new ArrayList<>();
    }

    public String getRetLabel() {
        if (retLabel == null && father != null) {
            return father.getRetLabel();
        }
        return retLabel;
    }

    public String getRetReg() {
        if (retReg == null && father != null) {
            return father.getRetReg();
        }
        return retReg;
    }

    public String getIfLabel() {
        if (ifLabel == null && father != null) {
            return father.getIfLabel();
        }
        return ifLabel;
    }

    public String getElseLabel() {
        if (elseLabel == null && father != null) {
            return father.getElseLabel();
        }
        return elseLabel;
    }

    public String getIfEnd() {
        if (ifEnd == null && father != null) {
            return father.getIfEnd();
        }
        return ifEnd;
    }

    public String getCondLabel() {
        if (condLabel == null && father != null) {
            return father.getCondLabel();
        }
        return condLabel;
    }

    public String getBodyLabel() {
        if (bodyLabel == null && father != null) {
            return father.getBodyLabel();
        }
        return bodyLabel;
    }

    public String getWhileEnd() {
        if (whileEnd == null && father != null) {
            return father.getWhileEnd();
        }
        return whileEnd;
    }

    public void updateIfLabel() {
        IrList.getInstance().newBlock();
        this.ifLabel = IrList.lastOp();
    }

    public void updateElseLabel() {
        IrList.getInstance().newBlock();
        this.elseLabel = IrList.lastOp();
    }

    public void updateIfEnd() {
        IrList.getInstance().newBlock();
        this.ifEnd = IrList.lastOp();
    }

    public void updateCondLabel() {
        IrList.getInstance().newBlock();
        this.condLabel = IrList.lastOp();
    }

    public void updateBodyLabel() {
        IrList.getInstance().newBlock();
        this.bodyLabel = IrList.lastOp();
    }

    public void updateWhileEnd() {
        IrList.getInstance().newBlock();
        this.whileEnd = IrList.lastOp();
    }

    public void updateRetLabel() {
        IrList.getInstance().newBlock();
        this.retLabel = IrList.lastOp();
    }

    public void updateRetReg() {
        this.retReg = IrList.newReg();
    }
}
