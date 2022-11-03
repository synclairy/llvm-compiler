package main.models.common.ast;

import main.models.common.llvm.AddIr;
import main.models.common.llvm.FunctionDefine;
import main.models.common.llvm.IR;
import main.models.common.llvm.IrList;
import main.models.common.llvm.MulIr;
import main.models.common.llvm.ReturnIr;
import main.models.common.llvm.SdivIr;
import main.models.common.llvm.SubIr;
import main.models.common.symbol.SymbolTable;
import main.utils.CharClassifier;
import main.utils.Printer;

import java.util.ArrayList;

public class TreeRoot implements TreeNode {
    private final ArrayList<TreeNode> children;
    private final NCode code;
    private final SymbolTable table;
    private static SymbolTable global;

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void llvm() {
        switch (code) {
            case Number:
            case PrimaryExp:
            case UnaryExp:
            case MulExp:
            case AddExp:
            case Exp:
            case LVal:
            case FuncRParams:
                compute();
                break;
            case MainFuncDef:
            case Decl:
            case ConstDecl:
            case VarDecl:
            case ConstDef:
            case ConstInitVal:
            case VarDef:
            case InitVal:
            case FuncDef:
            case FuncFParam:
            case FuncFParams:
                declare();
                break;
            default:
                flow();
        }
    }

    public void declare() {
        Token token = getFirstToken();
        switch (code) {
            case MainFuncDef:
                IrList.clearCount();
                IrList.getInstance().addFuncDef(
                        new FunctionDefine(false, "main", new ArrayList<>()));
                getRootByIndex(4).llvm();
                break;
            case ConstDecl:
                travelSal1(NCode.ConstDef);
                break;
            case VarDecl:
                travelSal1(NCode.VarDef);
                break;
            case ConstDef:
                table.fillType(TCode.INTTK);
                table.fillName(token.getValue());
                if (TCode.LBRACK.equals(getCodeByIndex(1))) {
                    table.fillDimension(getRootByIndex(2).synthesize());
                }
                if (TCode.LBRACK.equals(getCodeByIndex(4))) {
                    table.fillDimension(getRootByIndex(5).synthesize());
                }
                travelSal1(NCode.ConstInitVal);
                table.fillOver(code);
                break;
            case ConstInitVal:
                if (children.size() == 1) {
                    table.fillInitValue(synthesize());
                } else {
                    travelSal1(NCode.ConstInitVal);
                }
                break;
            case VarDef:
                table.fillType(TCode.INTTK);
                table.fillName(token.getValue());
                if (TCode.LBRACK.equals(getCodeByIndex(1))) {
                    table.fillDimension(getRootByIndex(2).synthesize());
                }
                if (TCode.LBRACK.equals(getCodeByIndex(4))) {
                    table.fillDimension(getRootByIndex(5).synthesize());
                }
                if (table.isGlobal()) {
                    travelSal1(NCode.InitVal);
                    table.fillOver(code);
                } else {
                    table.fillOver(code);
                    travelSal1(NCode.InitVal);
                }
                break;
            case InitVal:
                if (children.size() == 1) {
                    if (table.isGlobal()) {
                        table.fillInitValue(synthesize());
                    } else {
                        getRootByIndex(0).llvm();
                        table.storeSymbol(null, lastOp());
                    }
                } else {
                    travelSal1(NCode.InitVal);
                }
                break;
            case FuncDef:
                table.fillType(getFirstToken().getCode());
                table.fillName(getTokenByIndex(1).getValue());
                IrList.clearCount();
                travelSal1(NCode.FuncFParams);
                table.fillOver(code);
                TreeRoot b = (TreeRoot) children.get(children.size() - 1);
                table.setChildTableParams(b.table);
                travelSal1(NCode.Block);
                break;
            case FuncFParams:
                travelSal1(NCode.FuncFParam);
                break;
            case FuncFParam:
                table.fillParamName(getTokenByIndex(1).getValue());
                if (TCode.LBRACK.equals(getCodeByIndex(2))) {
                    table.fillDimension(0);
                }
                if (TCode.LBRACK.equals(getCodeByIndex(4))) {
                    table.fillDimension(getRootByIndex(5).synthesize());
                }
                table.fillOver(code);
                break;
            default:
        }
    }

    public void flow() {
        Token token = getFirstToken();
        switch (code) {
            case CompUnit:
                travelSal2(NCode.VarDecl, NCode.ConstDecl);
                table.addLibFunc();
                travelSal2(NCode.FuncDef, NCode.MainFuncDef);
                break;
            case Block:
                table.declareParams();
                travelSalAll();
                table.blockOver();
                break;
            case Stmt:
                switch (token.getCode()) {
                    case RETURNTK:
                        if (children.size() == 3) {
                            getRootByIndex(1).llvm();
                            addIr(new ReturnIr(lastOp()));
                            table.setNeedRet(1);
                        } else {
                            addIr(new ReturnIr());
                            table.setNeedRet(0);
                        }
                        break;
                    case IDENFR:
                        if (TCode.ASSIGN.equals(getCodeByIndex(1))) {
                            if (getTokenByIndex(2) != null) {
                                global.callFunc("getint", new ArrayList<>());
                            } else {
                                getRootByIndex(2).llvm();
                            }
                            table.storeSymbol(token.getValue(), lastOp());
                        } else {
                            travelSal1(NCode.Exp);
                        }
                        break;
                    case PRINTFTK:
                        ArrayList<String> chars = CharClassifier
                                                .fixFormatString(getTokenByIndex(2).getValue());
                        ArrayList<String> params = new ArrayList<>();
                        for (TreeNode node : children) {
                            if (node instanceof TreeRoot) {
                                ((TreeRoot) node).llvm();
                                params.add(lastOp());
                            }
                        }
                        //TODO
                        int count = 0;
                        for (String c : chars) {
                            ArrayList<String> temp = new ArrayList<>();
                            if (c == null) {
                                temp.add(params.get(count++));
                                global.callFunc("putint", temp);
                            } else {
                                temp.add(c);
                                global.callFunc("putch", temp);
                            }
                        }
                        break;
                    case LBRACE:
                        travelSal1(NCode.Block);
                        break;
                    default:
                }
                break;
            default:
        }
    }

    public void compute() {
        Token t = getFirstToken();
        TCode tcode = t.getCode();
        if (children.get(0) instanceof TreeRoot) {
            switch (children.size()) {
                case 1:
                    getRootByIndex(0).llvm();
                    break;
                case 2:
                    getRootByIndex(1).llvm();
                    if (tcode.equals(TCode.MINU)) {
                        addIr(new SubIr("0", lastOp()));
                    }
                    break;
                case 3:
                    getRootByIndex(0).llvm();
                    String s1 = lastOp();
                    getRootByIndex(2).llvm();
                    String s2 = lastOp();
                    tcode = getCodeByIndex(1);
                    switch (code) {
                        case AddExp:
                            if (tcode.equals(TCode.PLUS)) {
                                addIr(new AddIr(s1, s2));
                            } else {
                                addIr(new SubIr(s1, s2));
                            }
                            break;
                        case MulExp:
                            if (tcode.equals(TCode.MULT)) {
                                addIr(new MulIr(s1, s2));
                            } else if (tcode.equals(TCode.DIV)) {
                                addIr(new SdivIr(s1, s2));
                            } else {
                                addIr(new SdivIr(s1, s2));
                                addIr(new MulIr(lastOp(), s2));
                                addIr(new SubIr(s1, lastOp()));
                            }
                            break;
                        default:
                    }
                    break;
                default:
            }
        } else {
            switch (tcode) {
                case INTCON:
                    IrList.setLastOp(t.getValue());
                    //addIr(new AddIr("0", t.getValue()));
                    break;
                case LPARENT:
                    getRootByIndex(1).llvm();
                    break;
                case GETINTTK:
                    global.callFunc("getint", new ArrayList<>());
                    break;
                case IDENFR:
                    if (children.size() == 1) {
                        table.loadSymbol(getFirstToken().getValue());
                    } else if (TCode.LPARENT.equals(getCodeByIndex(1))) {
                        ArrayList<String> params = new ArrayList<>();
                        if (children.size() != 3) {
                            ArrayList<TreeNode> temp = ((TreeRoot) children.get(2)).getChildren();
                            for (TreeNode node : temp) {
                                if (node instanceof TreeRoot) {
                                    ((TreeRoot) node).llvm();
                                    params.add(lastOp());
                                }
                            }
                        }
                        global.callFunc(t.getValue(), params);
                    }
                    break;
                default:
            }
        }
    }

    public int synthesize() {
        if (children.get(0) instanceof TreeRoot) {
            switch (children.size()) {
                case 1:
                    return getRootByIndex(0).synthesize();
                case 2:
                    int n = getRootByIndex(1).synthesize();
                    if (getFirstToken().getCode().equals(TCode.MINU)) {
                        n = -1 * n;
                    }
                    return n;
                case 3:
                    int s1 = getRootByIndex(0).synthesize();
                    int s2 = getRootByIndex(2).synthesize();
                    TCode tcode = getCodeByIndex(1);
                    switch (code) {
                        case AddExp:
                            if (tcode.equals(TCode.PLUS)) {
                                return s1 + s2;
                            } else {
                                return s1 - s2;
                            }
                        case MulExp:
                            if (tcode.equals(TCode.MULT)) {
                                return s1 * s2;
                            } else if (tcode.equals(TCode.DIV)) {
                                return s1 / s2;
                            } else {
                                return s1 % s2;
                            }
                        default:
                            return 0;//TODO
                    }
                default:
                    return 0;//TODO
            }
        } else {
            switch (getFirstToken().getCode()) {
                case INTCON:
                    return Integer.parseInt(getFirstToken().getValue());
                case LPARENT:
                    return getRootByIndex(1).synthesize();
                default:
                    if (children.size() == 1) {
                        return table.getSymbolValue(getFirstToken().getValue(), 0, 0);
                    }
                    return 0;
            }
        }
    }

    public void travelSal1(NCode code) {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                if (((TreeRoot) node).getCode().equals(code)) {
                    ((TreeRoot) node).llvm();
                }
            }
        }
    }

    public void travelSal2(NCode code1, NCode code2) {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                if (((TreeRoot) node).getCode().equals(code1)
                        || ((TreeRoot) node).getCode().equals(code2)) {
                    ((TreeRoot) node).llvm();
                }
            }
        }
    }

    public void travelSalAll() {
        for (TreeNode node : children) {
            if (node instanceof TreeRoot) {
                ((TreeRoot) node).llvm();
            }
        }
    }

    public NCode getCode() {
        return code;
    }

    public void addIr(IR ir) {
        IrList.getInstance().addIr(ir);
    }

    public Token getFirstToken() {
        TreeNode node = children.get(0);
        if (node instanceof TreeLeaf) {
            return ((TreeLeaf) children.get(0)).getToken();
        }
        else if (node instanceof TreeRoot) {
            return ((TreeRoot) node).getFirstToken();
        }
        return null;
    }

    public TreeRoot getRootByIndex(int i) {
        if (i >= children.size()) {
            return null;
        }
        return ((TreeRoot) children.get(i));
    }

    public Token getTokenByIndex(int i) {
        if (i >= children.size()) {
            return null;
        }
        TreeNode node = children.get(i);
        if (node instanceof TreeLeaf) {
            return ((TreeLeaf) children.get(i)).getToken();
        }
        else {
            return null;
        }
    }

    public TCode getCodeByIndex(int i) {
        if (getTokenByIndex(i) == null) {
            return null;
        }
        return getTokenByIndex(i).getCode();
    }

    public String lastOp() {
        return IrList.lastOp();
    }

    @Override
    public void print() {
        for (TreeNode node : children) {
            node.print();
        }
        Printer.getInstance().print(code.toString());
    }

    public TreeRoot(NCode code, SymbolTable table) {
        children = new ArrayList<>();
        this.code = code;
        this.table = table;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public static void setGlobal(SymbolTable global) {
        TreeRoot.global = global;
    }
}
