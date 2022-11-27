package main.models.common.symbol;

import java.util.ArrayList;

public class ConstArraySymbol implements SymbolItem {
    private String name;
    private int dCount;
    private int d1;
    private int d2;
    private ArrayList<Integer> values;

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

    public int getValue(int i1, int i2) {
        return 0;
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
