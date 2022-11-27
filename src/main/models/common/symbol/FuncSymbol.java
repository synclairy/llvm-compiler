package main.models.common.symbol;

import main.models.common.ast.TCode;
import main.models.common.llvm.define.FunctionDeclare;
import main.models.common.llvm.define.FunctionDefine;
import main.models.common.llvm.IrList;

import java.util.ArrayList;

public class FuncSymbol implements SymbolItem {
    private final TCode returnType;
    private final String name;
    private ArrayList<SymbolItem> params;
    private boolean isLib = false;
    private final ArrayList<Integer> levels;

    public TCode getReturnType() {
        return returnType;
    }

    public FuncSymbol(TCode returnType, String name, ArrayList<SymbolItem> params) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.levels = new ArrayList<>();
        for (SymbolItem item : params) {
            if (item instanceof VariableSymbol) {
                levels.add(0);
            } else if (item instanceof ArraySymbol) {
                levels.add(((ArraySymbol) item).getLevel());
            }
        }
    }

    public FuncSymbol(TCode returnType, ArrayList<Integer> levels, String name) {
        this.returnType = returnType;
        this.name = name;
        this.levels = levels;
        isLib = true;
    }

    public ArrayList<Integer> getLevels() {
        return levels;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValues(ArrayList<Integer> values) {

    }

    @Override
    public void declare(String type) {
        boolean isVoid = returnType.equals(TCode.VOIDTK);
        if (isLib) {
            IrList.getInstance().addFuncDecl(new FunctionDeclare(isVoid, name, levels));
        } else {
            IrList.getInstance().addFuncDef(new FunctionDefine(isVoid, name, levels));
        }
    }

    @Override
    public String getReg() {
        return null;
    }
}
