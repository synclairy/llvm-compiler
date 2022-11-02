package main.parser;

import main.models.common.handler.ErrorInfoList;
import main.models.common.ast.TreeLeaf;
import main.models.common.ast.TreeRoot;
import main.models.common.ast.NCode;
import main.models.common.symbol.SymbolTable;
import main.models.common.ast.TCode;
import main.models.common.ast.Token;
import main.models.common.ast.TokenSequence;
import main.models.exceptions.ParserException;
import main.utils.CharClassifier;
import main.utils.CodeClassifier;

public class Parser {
    private TreeRoot root;
    private TokenSequence tokens;
    private final ErrorInfoList eil;

    public TreeRoot getRoot() {
        return root;
    }

    public Parser() {
        tokens = null;
        eil = ErrorInfoList.getInstance();
    }

    public void parse(TokenSequence tokens, SymbolTable global) throws ParserException {
        this.tokens = tokens;
        root = new TreeRoot(NCode.CompUnit, global);
        parseCompUnit(global);
    }

    private void parseCompUnit(SymbolTable table) throws ParserException {
        while (!peek(2).equals(TCode.LPARENT)) {
            parseDecl(root, table);
        }
        while (!peek(1).equals(TCode.MAINTK)) {
            parseFuncDef(root, table);
        }
        parseMainFuncDef(root, table);
        if (!peek().equals(TCode.EOF)) {
            throw new ParserException(tokens.getLastLine());
        }
    }

    private void parseDecl(TreeRoot father, SymbolTable table) throws ParserException {
        if (peek().equals(TCode.CONSTTK)) {
            parseConstDecl(father, table);
        } else {
            parseValDecl(father, table);
        }
    }

    private void parseConstDecl(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.ConstDecl, table);
        addNextLeaf(cur);
        judgeNext(cur, TCode.INTTK);
        parseConstDef(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseConstDef(cur, table);
        }
        judgeNext(cur, TCode.SEMICN);
        father.addChild(cur);
    }

    private void parseConstDef(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.ConstDef, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        judgeNext(cur, TCode.ASSIGN);
        parseConstInitVal(cur, table);
        father.addChild(cur);
    }

    private void parseConstInitVal(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.ConstInitVal, table);
        if (peek().equals(TCode.LBRACE)) {
            addNextLeaf(cur);
            if (peek().equals(TCode.RBRACE)) {
                addNextLeaf(cur);
            } else {
                parseConstInitVal(cur, table);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseConstInitVal(cur, table);
                }
                judgeNext(cur, TCode.RBRACE);
            }
        } else {
            parseConstExp(cur, table);
        }
        father.addChild(cur);
    }

    private void parseValDecl(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.VarDecl, table);
        judgeNext(cur, TCode.INTTK);
        parseVarDef(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseVarDef(cur, table);
        }
        judgeNext(cur, TCode.SEMICN);
        father.addChild(cur);
    }

    private void parseVarDef(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.VarDef, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        if (peek().equals(TCode.ASSIGN)) {
            addNextLeaf(cur);
            parseInitVal(cur, table);
        }
        father.addChild(cur);
    }

    private void parseInitVal(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.InitVal, table);
        if (peek().equals(TCode.LBRACE)) {
            addNextLeaf(cur);
            if (peek().equals(TCode.RBRACE)) {
                addNextLeaf(cur);
            } else {
                parseInitVal(cur, table);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseInitVal(cur, table);
                }
                judgeNext(cur, TCode.RBRACE);
            }
        } else {
            parseExp(cur, table);
        }
        father.addChild(cur);
    }

    private void parseFuncDef(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.FuncDef, table);
        parseFuncType(cur, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        judgeNext(cur, TCode.LPARENT);
        if (peek().equals(TCode.RPARENT)) {
            addNextLeaf(cur);
        } else {
            parseFuncFParams(cur, table);
            judgeNext(cur, TCode.RPARENT);
        }
        parseBlock(cur, table);
        father.addChild(cur);
    }

    private void parseMainFuncDef(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.MainFuncDef, table);
        judgeNext(cur, TCode.INTTK);
        judgeNext(cur, TCode.MAINTK);
        judgeNext(cur, TCode.LPARENT);
        judgeNext(cur, TCode.RPARENT);
        parseBlock(cur, table);
        father.addChild(cur);
    }

    private void parseFuncType(TreeRoot father, SymbolTable table) {
        TreeRoot cur = new TreeRoot(NCode.FuncType, table);
        if (peek().equals(TCode.VOIDTK) || peek().equals(TCode.INTTK)) {
            addNextLeaf(cur);
        }
        father.addChild(cur);
    }

    private void parseFuncFParams(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.FuncFParams, table);
        parseFuncFParam(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseFuncFParam(cur, table);
        }
        father.addChild(cur);
    }

    private void parseFuncFParam(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.FuncFParam, table);
        judgeNext(cur, TCode.INTTK);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        if (peek().equals(TCode.LBRACK)) {
            addNextLeaf(cur);
            judgeNext(cur, TCode.RBRACK);
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        father.addChild(cur);
    }

    private void parseBlock(TreeRoot father, SymbolTable fatherTable) throws ParserException {
        SymbolTable table = fatherTable.createChildTable();
        TreeRoot cur = new TreeRoot(NCode.Block, table);
        judgeNext(cur, TCode.LBRACE);
        while (!peek().equals(TCode.RBRACE)) {
            parseBlockItem(cur, table);
        }
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseBlockItem(TreeRoot father, SymbolTable table) throws ParserException {
        if (peek().equals(TCode.CONSTTK) || peek().equals(TCode.INTTK)) {
            parseDecl(father, table);
        } else {
            parseStmt(father, false, table);
        }
    }

    private void parseStmt(TreeRoot father, boolean inLoop, SymbolTable table)
            throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.Stmt, table);
        switch (peek()) {
            case PRINTFTK:
                addNextLeaf(cur);
                int line = tokens.getLastLine();
                int cnt = CharClassifier.countArgs(tokens.getPeekValue());
                judgeNext(cur, TCode.LPARENT);
                judgeNext(cur, TCode.STRCON);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseExp(cur, table);
                    cnt--;
                }
                if (cnt != 0) {
                    eil.addError('l', line);
                }
                judgeNext(cur, TCode.RPARENT);
                judgeNext(cur, TCode.SEMICN);
                break;
            case RETURNTK:
                addNextLeaf(cur);
                if (peek().equals(TCode.SEMICN)) {
                    addNextLeaf(cur);
                } else {
                    parseExp(cur, table);
                    judgeNext(cur, TCode.SEMICN);
                }
                break;
            case BREAKTK:
            case CONTINUETK:
                addNextLeaf(cur);
                if (!inLoop) {
                    eil.addError('m', tokens.getLastLine());
                }
                judgeNext(cur, TCode.SEMICN);
                break;
            case WHILETK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.LPARENT);
                parseCond(cur, table);
                judgeNext(cur, TCode.RPARENT);
                parseStmt(cur, true, table);
                break;
            case IFTK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.LPARENT);
                parseCond(cur, table);
                judgeNext(cur, TCode.RPARENT);
                parseStmt(cur, inLoop, table);
                if (peek().equals(TCode.ELSETK)) {
                    addNextLeaf(cur);
                    parseStmt(cur,inLoop, table);
                }
                break;
            case LBRACE:
                parseBlock(cur, table);
                break;
            default:
                if (tokens.posCmp(TCode.ASSIGN, TCode.SEMICN) < 0) {
                    parseLVal(cur, table);
                    judgeNext(cur, TCode.ASSIGN);
                    if (peek().equals(TCode.GETINTTK)) {
                        addNextLeaf(cur);
                        judgeNext(cur, TCode.LPARENT);
                        judgeNext(cur, TCode.RPARENT);
                    } else {
                        parseExp(cur, table);
                    }
                    judgeNext(cur, TCode.SEMICN);
                } else {
                    if (peek().equals(TCode.SEMICN)) {
                        addNextLeaf(cur);
                    } else  {
                        parseExp(cur, table);
                        judgeNext(cur, TCode.SEMICN);
                    }
                }
        }
        father.addChild(cur);
    }

    private void parseExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.Exp, table);
        parseAddExp(cur, table);
        father.addChild(cur);
    }

    private void parseCond(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.Cond, table);
        parseLOrExp(cur, table);
        father.addChild(cur);
    }

    private void parseLVal(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.LVal, table);
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseExp(cur, table);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        father.addChild(cur);
    }

    private void parsePrimaryExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.PrimaryExp, table);
        switch (peek()) {
            case LPARENT:
                addNextLeaf(cur);
                parseExp(cur, table);
                judgeNext(cur, TCode.RPARENT);
                break;
            case INTCON:
                parseNumber(cur, table);
                break;
            default:
                parseLVal(cur, table);
        }
        father.addChild(cur);
    }

    private void parseNumber(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.Number, table);
        judgeNext(cur, TCode.INTCON);
        father.addChild(cur);
    }

    private void parseUnaryExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.UnaryExp, table);
        switch (peek()) {
            case PLUS:
            case MINU:
            case NOT:
                parseUnaryOp(cur, table);
                parseUnaryExp(cur, table);
                break;
            case LPARENT:
            case INTCON:
                parsePrimaryExp(cur, table);
                break;
            default:
                if (peek(1).equals(TCode.LPARENT)) {
                    if (peek().equals(TCode.IDENFR) || peek().equals(TCode.GETINTTK)) {
                        addNextLeaf(cur);
                    }
                    addNextLeaf(cur);
                    if (peek().equals(TCode.RPARENT)) {
                        addNextLeaf(cur);
                    } else {
                        parseFuncRParams(cur, table);
                        judgeNext(cur, TCode.RPARENT);
                    }
                } else {
                    parsePrimaryExp(cur, table);
                }
        }
        father.addChild(cur);
    }

    private void parseUnaryOp(TreeRoot father, SymbolTable table) {
        TreeRoot cur = new TreeRoot(NCode.UnaryOp, table);
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseFuncRParams(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.FuncRParams, table);
        parseExp(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseExp(cur, table);
        }
        father.addChild(cur);
    }

    private void parseMulExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.MulExp, NCode.UnaryExp, table);
        father.addChild(cur);
    }

    private void parseAddExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.AddExp, NCode.MulExp, table);
        father.addChild(cur);
    }

    private void parseRelExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.RelExp, NCode.AddExp, table);
        father.addChild(cur);
    }

    private void parseEqExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.EqExp, NCode.RelExp, table);
        father.addChild(cur);
    }

    private void parseLAndExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.LAndExp, NCode.EqExp, table);
        father.addChild(cur);
    }

    private void parseLOrExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = leftRecur(NCode.LOrExp, NCode.LAndExp, table);
        father.addChild(cur);
    }

    private void parseConstExp(TreeRoot father, SymbolTable table) throws ParserException {
        TreeRoot cur = new TreeRoot(NCode.ConstExp, table);
        parseAddExp(cur, table);
        father.addChild(cur);
    }

    private void judgeNext(TreeRoot father, TCode code) {
        if (peek().equals(code)) {
            addNextLeaf(father);
        } else {
            int line = tokens.getLastLine();
            switch (code) {
                case COMMA:
                    eil.addError('i', line);
                    break;
                case RPARENT:
                    eil.addError('j', line);
                    break;
                case RBRACK:
                    eil.addError('k', line);
                    break;
                default:
                    //TODO: throw new ParserException(line);
            }
        }
    }

    private TCode peek() {
        return tokens.peek();
    }

    private TCode peek(int n) {
        return tokens.peek(n);
    }

    private Token next() {
        return tokens.next();
    }

    private void addNextLeaf(TreeRoot father) {
        father.addChild(new TreeLeaf(next()));
    }

    private TreeRoot leftRecur(NCode whole, NCode recur, SymbolTable table)
            throws ParserException {
        TreeRoot cur = new TreeRoot(whole, table);
        parse4NCode(cur, recur, table);
        TreeRoot temp;
        while (CodeClassifier.isSeparator(whole, peek())) {
            temp = new TreeRoot(whole, table);
            temp.addChild(cur);
            addNextLeaf(temp);
            parse4NCode(temp, recur, table);
            cur = temp;
        }
        return cur;
    }

    private void parse4NCode(TreeRoot cur, NCode code, SymbolTable table)
            throws ParserException {
        switch (code) {
            case LAndExp:
                parseLAndExp(cur, table);
                break;
            case EqExp:
                parseEqExp(cur, table);
                break;
            case RelExp:
                parseRelExp(cur, table);
                break;
            case AddExp:
                parseAddExp(cur, table);
                break;
            case MulExp:
                parseMulExp(cur, table);
                break;
            case UnaryExp:
                parseUnaryExp(cur, table);
                break;
            default:
        }
    }
}