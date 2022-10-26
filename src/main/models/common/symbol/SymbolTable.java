package main.models.common.symbol;

import main.models.common.NCode;
import main.models.common.TCode;
import main.models.common.grammer.GTreeNode;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, SymbolItem> items;
    private final ArrayList<SymbolTable> children;
    private HashMap<String, SymbolItem> params;
    private SymbolTable father;
    private TCode code;
    private String name;
    private GTreeNode d1;
    private GTreeNode d2;
    private int count;
    private String paramName;

    public SymbolTable(HashMap<String, SymbolItem> items, SymbolTable father) {
        if (items == null) {
            this.items = new HashMap<>();
        } else {
            this.items = items;
        }
        this.children = new ArrayList<>();
        params = new HashMap<>();
        this.father = father;
    }

    public void fillType(TCode code) {
        this.code = code;
    }

    public void fillName(String name) {
        this.name = name;
    }

    public void fillDimension(GTreeNode n) {
        if (count == 0) {
            d1 = n;
        } else {
            d2 = n;
        }
        count++;
    }

    public void fillOver(NCode code) {
        if (code.equals(NCode.VarDef)) {
            if (count == 0) {
                items.put(name, new VariableSymbol(name));
            } else {
                items.put(name, new ArraySymbol(name, count, d1, d2));
                clearD();
            }
        } else if (code.equals(NCode.ConstDef)) {
            if (count == 0) {
                items.put(name, new ConstVariableSymbol(name));
            } else {
                items.put(name, new ConstArraySymbol(name, count, d1, d2));
                clearD();
            }
        } else if (code.equals(NCode.FuncDef)) {
            items.put(name, new FuncSymbol(this.code, name, params));
        } else if (code.equals(NCode.FuncFParam)) {
            if (count == 0) {
                params.put(paramName, new VariableSymbol(paramName));
            } else {
                params.put(paramName, new ArraySymbol(paramName, count, d1, d2));
                clearD();
            }
        }
    }

    public void fillParamName(String s) {
        paramName = s;
    }

    public void clearD() {
        count = 0;
        d1 = null;
        d2 = null;
    }

    public SymbolTable createChildTable() {
        SymbolTable child = new SymbolTable(params, this);
        children.add(child);
        params = new HashMap<>();
        return child;
    }

    public void print() {
        System.out.println("new table");
        for (String name : items.keySet()) {
            System.out.println(name + ":" + items.get(name).toString());
        }
        for (SymbolTable table : children) {
            table.print();
        }
    }


}
