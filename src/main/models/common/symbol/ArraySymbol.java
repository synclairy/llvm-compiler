package main.models.common.symbol;

import main.models.common.llvm.IrList;
import main.models.common.llvm.define.GlobalDefine;
import main.models.common.llvm.ir.AddIr;
import main.models.common.llvm.ir.AllocaIr;
import main.models.common.llvm.ir.GepIr;
import main.models.common.llvm.ir.MulIr;
import main.models.common.llvm.ir.StoreIr;

import java.util.ArrayList;

public class ArraySymbol implements SymbolItem {
    private final String name;
    private final int dCount;
    private final int d1;
    private final int d2;
    private ArrayList<Integer> values;
    private String addrReg;
    private String originReg;
    private boolean isParam = false;
    private int pos = 0;

    public int getD() {
        return dCount;
    }

    @Override
    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }

    public ArraySymbol(String name, int count, int d1, int d2) {
        this.name = name;
        this.dCount = count;
        this.d1 = d1;
        this.d2 = d2;
    }

    public void setParam(boolean param) {
        isParam = param;
    }

    public int getLevel() {
        if (dCount == 1) {
            return 1;
        } else {
            return d2 + 1;
        }
    }

    private void gep() {
        addIr(new GepIr(getLen(), originReg));
        addrReg = IrList.lastOp();
    }

    public void gep(String d1, String d2) {
        if (!isParam) {
            gep();
        }
        if (dCount == 1) {
            if (d1 != null) {
                addIr(new GepIr(addrReg, d1));
            } else {
                IrList.setLastOp(addrReg);
            }
        } else {
            if (d1 != null) {
                addIr(new MulIr(d1, String.valueOf(this.d2)));
                if (d2 != null) {
                    addIr(new AddIr(d2, IrList.lastOp()));
                }
                addIr(new GepIr(addrReg, IrList.lastOp()));
            } else {
                IrList.setLastOp(addrReg);
            }
        }
    }

    @Override
    public void declare(String type) {
        switch (type) {
            case "common":
                addrReg = IrList.newReg();
                originReg = addrReg;
                addIr(new AllocaIr(addrReg, getLen(), name));
                gep();
                break;
            case "param":
                addrReg = IrList.newReg();
                break;
            case "paramStore":
                isParam = true;
                break;
            default:
                addrReg = "@" + name;
                originReg = addrReg;
                IrList.getInstance().addGlobal(new GlobalDefine(this));
        }
    }

    public int getLen() {
        if (dCount == 1) {
            return d1;
        } else {
            return d2 * d1;
        }
    }

    public String getInitString() {
        if (values.size() == 0) {
            return "zeroinitializer\n";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < values.size(); i++) {
                sb.append("i32 ").append(values.get(i));
                if (i != values.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append("]\n");
                }
            }
            return sb.toString();
        }
    }

    public void fill(String op) {
        addIr(new GepIr(addrReg, pos++));
        addIr(new StoreIr(op, IrList.lastOp()));
    }

    @Override
    public String getReg() {
        return originReg;
    }

    @Override
    public String getName() {
        return name;
    }
}
