package main.models.common.llvm.ir;

import main.models.common.ast.TCode;
import main.models.common.llvm.IrList;
import main.models.common.symbol.FuncSymbol;

import java.util.ArrayList;

public class CallIr implements IR {
    private final String resultReg;
    private final String name;
    private final ArrayList<String> params;
    private final TCode code;
    private final ArrayList<Integer> levels;

    public CallIr(FuncSymbol func,
                  ArrayList<String> params) {
        this.code = func.getReturnType();
        if (code.equals(TCode.VOIDTK)) {
            this.resultReg = "";
        } else {
            this.resultReg = IrList.newReg() + " = ";
        }
        this.name = func.getName();
        this.params = params;
        levels = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\t");
        s.append(resultReg);
        s.append("call ");
        s.append(code.equals(TCode.VOIDTK) ? "void @" : "i32 @");
        s.append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            levels.add(0);//TODO
            switch (levels.get(i)) {
                case 0:
                    s.append("i32 ");
                    break;
                case 1:
                    s.append("i32* ");
                    break;
                default:
                    s.append("[").append(levels.get(i) - 1).append(" x i32]* ");
            }
            s.append(params.get(i));
            if (i != params.size() - 1) {
                s.append(", ");
            }
        }
        s.append(")\n");
        return s.toString();
    }
}
