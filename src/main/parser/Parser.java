package main.parser;

import main.models.common.ErrorInfoList;
import main.models.common.grammer.GTreeLeaf;
import main.models.common.grammer.GTreeRoot;
import main.models.common.NCode;
import main.models.common.symbol.SymbolTable;
import main.models.common.TCode;
import main.models.common.Token;
import main.models.common.TokenSequence;
import main.models.exceptions.ParserException;
import main.utils.CharClassifier;
import main.utils.CodeClassifier;

public class Parser {
    private GTreeRoot root;
    private TokenSequence tokens;
    private final ErrorInfoList eil;

    public GTreeRoot getRoot() {
        return root;
    }

    public Parser() {
        tokens = null;
        eil = ErrorInfoList.getInstance();
    }

    public void parse(TokenSequence tokens, SymbolTable global) throws ParserException {
        this.tokens = tokens;
        root = new GTreeRoot(NCode.CompUnit, global);
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

    private void parseDecl(GTreeRoot father, SymbolTable table) throws ParserException {
        if (peek().equals(TCode.CONSTTK)) {
            parseConstDecl(father, table);
        } else {
            parseValDecl(father, table);
        }
    }

    private void parseConstDecl(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstDecl, table);
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

    private void parseConstDef(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstDef, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        table.fillType(TCode.INTTK);
        table.fillName(s);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                table.fillDimension(cur.getLastChild());
                judgeNext(cur, TCode.RBRACK);
            }
        }
        table.fillOver(NCode.ConstDef);
        judgeNext(cur, TCode.ASSIGN);
        parseConstInitVal(cur, table);
        father.addChild(cur);
    }

    private void parseConstInitVal(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstInitVal, table);
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

    private void parseValDecl(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.VarDecl, table);
        judgeNext(cur, TCode.INTTK);
        parseVarDef(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseVarDef(cur, table);
        }
        judgeNext(cur, TCode.SEMICN);
        father.addChild(cur);
    }

    private void parseVarDef(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.VarDef, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        table.fillType(TCode.INTTK);
        table.fillName(s);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                table.fillDimension(cur.getLastChild());
                judgeNext(cur, TCode.RBRACK);
            }
        }
        table.fillOver(NCode.VarDef);
        if (peek().equals(TCode.ASSIGN)) {
            addNextLeaf(cur);
            parseInitVal(cur, table);
        }
        father.addChild(cur);
    }

    private void parseInitVal(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.InitVal, table);
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

    private void parseFuncDef(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncDef, table);
        table.fillType(peek());
        parseFuncType(cur, table);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        table.fillName(s);
        judgeNext(cur, TCode.LPARENT);
        if (peek().equals(TCode.RPARENT)) {
            addNextLeaf(cur);
        } else {
            parseFuncFParams(cur, table);
            judgeNext(cur, TCode.RPARENT);
        }
        table.fillOver(NCode.FuncDef);
        parseBlock(cur, table);
        father.addChild(cur);
    }

    private void parseMainFuncDef(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.MainFuncDef, table);
        judgeNext(cur, TCode.INTTK);
        judgeNext(cur, TCode.MAINTK);
        judgeNext(cur, TCode.LPARENT);
        judgeNext(cur, TCode.RPARENT);
        parseBlock(cur, table);
        father.addChild(cur);
    }

    private void parseFuncType(GTreeRoot father, SymbolTable table) {
        GTreeRoot cur = new GTreeRoot(NCode.FuncType, table);
        if (peek().equals(TCode.VOIDTK) || peek().equals(TCode.INTTK)) {
            addNextLeaf(cur);
        }
        father.addChild(cur);
    }

    private void parseFuncFParams(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncFParams, table);
        parseFuncFParam(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseFuncFParam(cur, table);
        }
        father.addChild(cur);
    }

    private void parseFuncFParam(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncFParam, table);
        judgeNext(cur, TCode.INTTK);
        String s = tokens.peekName();
        judgeNext(cur, TCode.IDENFR);
        table.fillParamName(s);
        if (peek().equals(TCode.LBRACK)) {
            addNextLeaf(cur);
            judgeNext(cur, TCode.RBRACK);
            table.fillDimension(null);
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur, table);
                table.fillDimension(cur.getLastChild());//TODO
                judgeNext(cur, TCode.RBRACK);
            }
        }
        table.fillOver(NCode.FuncFParam);
        father.addChild(cur);
    }

    private void parseBlock(GTreeRoot father, SymbolTable fatherTable) throws ParserException {
        SymbolTable table = fatherTable.createChildTable();
        GTreeRoot cur = new GTreeRoot(NCode.Block, table);
        judgeNext(cur, TCode.LBRACE);
        while (!peek().equals(TCode.RBRACE)) {
            parseBlockItem(cur, table);
        }
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseBlockItem(GTreeRoot father, SymbolTable table) throws ParserException {
        if (peek().equals(TCode.CONSTTK) || peek().equals(TCode.INTTK)) {
            parseDecl(father, table);
        } else {
            parseStmt(father, false, table);
        }
    }

    private void parseStmt(GTreeRoot father, boolean inLoop, SymbolTable table)
            throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Stmt, table);
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

    private void parseExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Exp, table);
        parseAddExp(cur, table);
        father.addChild(cur);
    }

    private void parseCond(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Cond, table);
        parseLOrExp(cur, table);
        father.addChild(cur);
    }

    private void parseLVal(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.LVal, table);
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

    private void parsePrimaryExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.PrimaryExp, table);
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

    private void parseNumber(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Number, table);
        judgeNext(cur, TCode.INTCON);
        father.addChild(cur);
    }

    private void parseUnaryExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.UnaryExp, table);
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
                    judgeNext(cur, TCode.IDENFR);
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

    private void parseUnaryOp(GTreeRoot father, SymbolTable table) {
        GTreeRoot cur = new GTreeRoot(NCode.UnaryOp, table);
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseFuncRParams(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncRParams, table);
        parseExp(cur, table);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseExp(cur, table);
        }
        father.addChild(cur);
    }

    private void parseMulExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.MulExp, NCode.UnaryExp, table);
        father.addChild(cur);
    }

    private void parseAddExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.AddExp, NCode.MulExp, table);
        father.addChild(cur);
    }

    private void parseRelExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.RelExp, NCode.AddExp, table);
        father.addChild(cur);
    }

    private void parseEqExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.EqExp, NCode.RelExp, table);
        father.addChild(cur);
    }

    private void parseLAndExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.LAndExp, NCode.EqExp, table);
        father.addChild(cur);
    }

    private void parseLOrExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.LOrExp, NCode.LAndExp, table);
        father.addChild(cur);
    }

    private void parseConstExp(GTreeRoot father, SymbolTable table) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstExp, table);
        parseAddExp(cur, table);
        father.addChild(cur);
    }

    private void judgeNext(GTreeRoot father, TCode code) {
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

    private void addNextLeaf(GTreeRoot father) {
        father.addChild(new GTreeLeaf(next()));
    }

    private GTreeRoot leftRecur(NCode whole, NCode recur, SymbolTable table)
            throws ParserException {
        GTreeRoot cur = new GTreeRoot(whole, table);
        parse4NCode(cur, recur, table);
        GTreeRoot temp;
        while (CodeClassifier.isSeparator(whole, peek())) {
            temp = new GTreeRoot(whole, table);
            temp.addChild(cur);
            addNextLeaf(temp);
            parse4NCode(temp, recur, table);
            cur = temp;
        }
        return cur;
    }

    private void parse4NCode(GTreeRoot cur, NCode code, SymbolTable table)
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