package main.models.common.symbol;

import main.models.common.llvm.IrList;
import main.models.common.llvm.define.GlobalDefine;
import main.models.common.llvm.ir.AddIr;
import main.models.common.llvm.ir.AllocaIr;
import main.models.common.llvm.ir.GepIr;
import main.models.common.llvm.ir.MulIr;
import main.models.common.llvm.ir.StoreIr;

import java.util.ArrayList;

public class ConstArraySymbol implements SymbolItem {
    private final String name;
    private final int dCount;
    private final int d1;
    private final int d2;
    private ArrayList<Integer> values;
    private String addrReg;
    private String originReg;

    @Override
    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }

    public ConstArraySymbol(String name, int count, int d1, int d2) {
        this.name = name;
        this.dCount = count;
        this.d1 = d1;
        this.d2 = d2;
    }

    public int getLen() {
        return values.size();
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

    public int getValue(int i1, int i2) {
        if (dCount == 1) {
            return values.get(i1);
        } else {
            return values.get(i1 * d2 + i2);
        }
    }

    private void gep() {
        addIr(new GepIr(getLen(), originReg));
        addrReg = IrList.lastOp();
    }

    public void gep(String d1, String d2) {
        gep();
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
        if ("common".equals(type)) {
            addrReg = IrList.newReg();
            originReg = addrReg;
            addIr(new AllocaIr(addrReg, getLen(), name));
            gep();
            for (int i = 0; i < values.size(); i++) {
                addIr(new GepIr(addrReg, i));
                addIr(
                        new StoreIr(String.valueOf(values.get(i)), IrList.lastOp()));
            }
        } else {
            addrReg = "@" + name;
            originReg = addrReg;
            IrList.getInstance().addGlobal(new GlobalDefine(this));
        }
    }

    @Override
    public String getReg() {
        return originReg;
    }

    public int getD() {
        return dCount;
    }

    @Override
    public String getName() {
        return name;
    }

}
