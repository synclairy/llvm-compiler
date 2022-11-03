package main.models.common.ast.component.flow;

import main.models.common.ast.NCode;
import main.models.common.ast.TreeRoot;

public class BlockNode extends TreeRoot {
    @Override
    public NCode getCode() {
        return NCode.Block;
    }

    @Override
    public void llvm() {
        getTable().declareParams();
        travelSalAll();
        getTable().blockOver();
    }
}
