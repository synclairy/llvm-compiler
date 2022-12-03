package main.models.common.symbol;

import java.util.ArrayList;

public class ConstVariableSymbol implements SymbolItem {
    private final String name;
    private int value;

    @Override
    public void setValues(ArrayList<Integer> values) {
        if (values.size() == 0) {
            value = 0;
        } else {
            value = values.get(0);
        }
    }

    public ConstVariableSymbol(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void declare(String type) {

    }

    @Override
    public String getReg() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
