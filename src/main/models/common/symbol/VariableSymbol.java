package main.models.common.symbol;

import main.models.common.llvm.ir.AllocaIr;
import main.models.common.llvm.define.GlobalDefine;
import main.models.common.llvm.IrList;
import main.models.common.llvm.ir.StoreIr;

import java.util.ArrayList;

public class VariableSymbol implements SymbolItem {
    private final String name;
    private String addrReg;
    private int value;

    @Override
    public void setValues(ArrayList<Integer> values) {
        if (values.size() == 0) {
            value = 0;
        } else {
            value = values.get(0);
        }
    }

    public VariableSymbol(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void declare(String type) {
        switch (type) {
            case "common":
                addrReg = IrList.newReg();
                addIr(new AllocaIr(addrReg, name));
                break;
            case "param":
                addrReg = IrList.newReg();
                break;
            case "paramStore":
                String temp = addrReg;
                addrReg = IrList.newReg();
                addIr(new AllocaIr(addrReg, name));
                addIr(new StoreIr(temp, addrReg));
                break;
            default:
                addrReg = "@" + name;
                IrList.getInstance().addGlobal(new GlobalDefine(this));
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getReg() {
        return addrReg;
    }

    @Override
    public String getName() {
        return name;
    }
}
