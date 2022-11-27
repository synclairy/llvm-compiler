package main.models.common.symbol;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.handler.ErrorInfoList;
import main.models.common.llvm.Labels;
import main.models.common.llvm.ir.AllocaIr;
import main.models.common.llvm.ir.BrIr;
import main.models.common.llvm.ir.CallIr;
import main.models.common.llvm.ir.IR;
import main.models.common.llvm.IrList;
import main.models.common.llvm.ir.LoadIr;
import main.models.common.llvm.ir.StoreIr;
import main.models.common.llvm.ir.SubIr;

import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<SymbolItem> items;
    private final ArrayList<SymbolTable> children;
    private ArrayList<SymbolItem> params;
    private ArrayList<Integer> initValues;
    private final SymbolTable father;
    private TCode code;
    private String name;
    private int d1;
    private int d2;
    private int paramsNum;
    private int count;
    private static int line;
    private static boolean retInt = false;
    private String paramName;

    public static void setRetInt(boolean retInt) {
        SymbolTable.retInt = retInt;
    }

    public SymbolTable(SymbolTable father) {
        this.children = new ArrayList<>();
        this.father = father;
        items = new ArrayList<>();
        params = new ArrayList<>();
        initValues = new ArrayList<>();
    }

    public int getLevel(String name) {
        SymbolItem item = getSymbol(name);
        if (item instanceof VariableSymbol || item instanceof ConstVariableSymbol) {
            return 0;
        } else if (item instanceof ArraySymbol) {
            return ((ArraySymbol) item).getLevel();
        } else if (item instanceof FuncSymbol) {
            if (((FuncSymbol) item).getReturnType().equals(TCode.VOIDTK)) {
                return -1;
            }
        }
        return 0;
    }

    public void callFunc(String name, ArrayList<String> p, ArrayList<Integer> levels) {
        SymbolItem item = getSymbol(name);
        if (item instanceof FuncSymbol) {
            ArrayList<Integer> requires = ((FuncSymbol) item).getLevels();
            if (levels.size() != requires.size()) {
                addError('d');
            } else {
                for (int i = 0; i < levels.size(); i++) {
                   if (!levels.get(i).equals(requires.get(i))) {
                       addError('e');
                       return;
                   }
                }
                addIr(new CallIr((FuncSymbol) item, p));
            }
        }
    }

    public void declareParams(Labels labels) {
        for (int i = 0; i < paramsNum; i++) {
            items.get(i).declare("param");
        }
        if (father.isGlobal()) {
            IrList.getInstance().newBlock();
            labels.updateRetReg();
            addIr(new AllocaIr(IrList.lastOp(), "result_a0"));
        }
        for (int i = 0; i < paramsNum; i++) {
            items.get(i).declare("paramStore");
        }
    }

    public void returnValue(String reg, Labels labels) {
        if (reg == null) {
            if (retInt) {
                addError('g');
            } else {
                addIr(new BrIr(labels, "ret"));
            }
        } else {
            if (!retInt) {
                addError('f');
            } else {
                addIr(new StoreIr(reg, labels));
                addIr(new BrIr(labels, "ret"));
            }
        }
    }

    public void storeSymbol(String name, String reg, int d1, int d2) {
        if (name != null) {
            SymbolItem item = getSymbol(name);
            if (item instanceof ConstVariableSymbol || item instanceof ConstArraySymbol) {
                addError('h');
            } else if (item instanceof VariableSymbol) {
                addIr(new StoreIr(reg, item.getReg()));
            }
            //TODO: array
        } else {
            addIr(new StoreIr(reg, getSymbol(this.name).getReg()));
        }
    }

    public void loadSymbol(String name) {
        SymbolItem sym = getSymbol(name);
        if (sym == null) {
            //TODO
        } else {
            //TODO
            if (sym instanceof VariableSymbol) {
                addIr(new LoadIr(sym.getReg()));
            } else if (sym instanceof ConstVariableSymbol) {
                int v = ((ConstVariableSymbol) sym).getValue();
                if (v >= 0) {
                    IrList.setLastOp("" + v);
                } else {
                    addIr(new SubIr("0", "" + (-1 * v)));
                }
            }
        }
    }

    public int getSymbolValue(String name, int d1, int d2) {
        SymbolItem sym = getSymbol(name);
        if (sym == null) {
            //TODO
        } else {
            if (sym instanceof ConstArraySymbol) {
                //TODO
            } else if (sym instanceof ConstVariableSymbol) {
                return ((ConstVariableSymbol) sym).getValue();
            }
        }
        return 0;
    }

    public int getVarValue(String name) {
        SymbolItem item = getSymbol(name);
        if (item != null) {
            return ((ConstVariableSymbol) item).getValue();
        } else if (father != null) {
            return father.getVarValue(name);
        } else {
            return 0;
        }
    }

    public int getArrayValue(String name, int i1, int i2) {
        SymbolItem item = getSymbol(name);
        if (item != null) {
            return ((ConstArraySymbol) item).getValue(i1, i2);
        } else if (father != null) {
            return father.getArrayValue(name, i1, i2);
        } else {
            return 0;
        }
    }

    public void fillType(TCode code) {
        this.code = code;
    }

    public void fillName(String name) {
        this.name = name;
    }

    public static void fillLine(int line) {
        SymbolTable.line = line;
    }

    public void fillDimension(int n) {
        if (count == 0) {
            d1 = n;
        } else {
            d2 = n;
        }
        count++;
    }

    public void fillOver(NCode code) {
        SymbolItem item;
        switch (code) {
            case FuncFParam:
                for (SymbolItem i : params) {
                    if (i.getName().equals(paramName)) {
                        addError('b');
                    }
                }
                if (count == 0) {
                    params.add(new VariableSymbol(paramName));
                } else {
                    params.add(new ArraySymbol(paramName, count, d1, d2));
                    clearD();
                }
                return;
            case VarDef:
                if (count == 0) {
                    item = new VariableSymbol(name);
                } else {
                    item = new ArraySymbol(name, count, d1, d2);
                    clearD();
                }
                break;
            case ConstDef:
                if (count == 0) {
                    item = new ConstVariableSymbol(name);
                } else {
                    item = new ConstArraySymbol(name, count, d1, d2);
                    clearD();
                }
                break;
            default:
                retInt = this.code.equals(TCode.INTTK);
                item = new FuncSymbol(this.code, name, params);
                break;
        }
        item.setValues(initValues);
        if (getLocalSymbol(name) != null) {
            addError('b');
        }
        if (isGlobal()) {
            item.declare("global");
        } else {
            item.declare("common");
        }
        items.add(item);
        initValues = new ArrayList<>();
    }

    public void setChildTableParams(SymbolTable table) {
        table.setParams(params);
        params = new ArrayList<>();
    }

    public void fillParamName(String s) {
        paramName = s;
    }

    public void fillInitValue(int n) {
        initValues.add(n);
    }

    public void clearD() {
        count = 0;
        d1 = 0;
        d2 = 0;
    }

    public boolean isGlobal() {
        return father == null;
    }

    public SymbolTable createChildTable() {
        SymbolTable child = new SymbolTable(this);
        children.add(child);
        return child;
    }

    public void setParams(ArrayList<SymbolItem> items) {
        this.items = items;
        paramsNum = items.size();
    }

    public void addIr(IR ir) {
        IrList.getInstance().addIr(ir);
    }

    public void addLibFunc() {
        ArrayList<Integer> i32 = new ArrayList<>();
        i32.add(0);
        SymbolItem item = new FuncSymbol(TCode.INTTK, new ArrayList<>(), "getint");
        items.add(item);
        item.declare("global");
        item = new FuncSymbol(TCode.VOIDTK, i32, "putint");
        items.add(item);
        item.declare("global");
        item = new FuncSymbol(TCode.VOIDTK, i32, "putch");
        items.add(item);
        item.declare("global");
    }

    public SymbolItem getSymbol(String name) {
        for (SymbolItem item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        if (isGlobal()) {
            addError('c');
            return new VariableSymbol("placeHolder");
        } else {
            return father.getSymbol(name);
        }
    }

    public SymbolItem getLocalSymbol(String name) {
        for (SymbolItem item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    private void addError(char t) {
        ErrorInfoList.getInstance().addError(t, line);
    }
}
