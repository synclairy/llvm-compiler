package main.models.common.symbol;

import java.util.ArrayList;

public interface SymbolItem {
    void declare(String type);

    String getReg();

    String getName();

    void setValues(ArrayList<Integer> values);
}
