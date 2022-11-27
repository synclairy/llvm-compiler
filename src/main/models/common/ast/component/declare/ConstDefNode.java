package main.models.common.ast.component.declare;

import main.models.common.ast.NCode;
import main.models.common.ast.TCode;
import main.models.common.ast.Token;
import main.models.common.ast.TreeRoot;

public class ConstDefNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.ConstDef;
    }

    @Override
    public void llvm() {
        Token token = getFirstToken();
        getTable().fillType(TCode.INTTK);
        fillLineByIndex(0);
        getTable().fillName(token.getValue());
        if (TCode.LBRACK.equals(getCodeByIndex(1))) {
            getTable().fillDimension(getRootByIndex(2).synthesize());
        }
        if (TCode.LBRACK.equals(getCodeByIndex(4))) {
            getTable().fillDimension(getRootByIndex(5).synthesize());
        }
        travelSal1(NCode.ConstInitVal);
        getTable().fillOver(NCode.ConstDef);
    }
}
