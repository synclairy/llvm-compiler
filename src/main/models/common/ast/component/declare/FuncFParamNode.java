package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.TreeRoot;

public class FuncFParamNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.FuncFParam;
    }

    @Override
    public void llvm() {
        getTable().fillParamName(getTokenByIndex(1).getValue());
        if (TCode.LBRACK.equals(getCodeByIndex(2))) {
            getTable().fillDimension(0);
        }
        if (TCode.LBRACK.equals(getCodeByIndex(4))) {
            getTable().fillDimension(getRootByIndex(5).synthesize());
        }
        getTable().fillOver(NCode.FuncFParam);
    }
}
