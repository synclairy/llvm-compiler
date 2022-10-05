package main.parser;

import main.models.common.GTreeLeaf;
import main.models.common.GTreeRoot;
import main.models.common.NCode;
import main.models.common.TCode;
import main.models.common.Token;
import main.models.common.TokenSequence;
import main.models.exceptions.ParserException;
import main.utils.CodeClassifier;

public class Parser {
    private final GTreeRoot root;
    private TokenSequence tokens;

    public GTreeRoot getRoot() {
        return root;
    }

    public Parser() {
        root = new GTreeRoot(NCode.CompUnit);
        tokens = null;
    }

    public void parse(TokenSequence tokens) throws ParserException {
        this.tokens = tokens;
        parseCompUnit();
    }

    private void parseCompUnit() throws ParserException {
        while (!peek(2).equals(TCode.LPARENT)) {
            parseDecl(root);
        }
        while (!peek(1).equals(TCode.MAINTK)) {
            parseFuncDef(root);
        }
        parseMainFuncDef(root);
        if (!peek().equals(TCode.EOF)) {
            throw new ParserException(tokens.getLastLine());
        }
    }

    private void parseDecl(GTreeRoot father) throws ParserException {
        if (peek().equals(TCode.CONSTTK)) {
            parseConstDecl(father);
        } else {
            parseValDecl(father);
        }
    }

    private void parseConstDecl(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstDecl);
        addNextLeaf(cur);
        judgeNext(cur, TCode.INTTK);
        parseConstDef(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseConstDef(cur);
        }
        judgeNext(cur, TCode.SEMICN);
        father.addChild(cur);
    }

    private void parseConstDef(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstDef);
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        judgeNext(cur, TCode.ASSIGN);
        parseConstInitVal(cur);
        father.addChild(cur);
    }

    private void parseConstInitVal(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstInitVal);
        if (peek().equals(TCode.LBRACE)) {
            addNextLeaf(cur);
            if (peek().equals(TCode.RBRACE)) {
                addNextLeaf(cur);
            } else {
                parseConstInitVal(cur);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseConstInitVal(cur);
                }
                judgeNext(cur, TCode.RBRACE);
            }
        } else {
            parseConstExp(cur);
        }
        father.addChild(cur);
    }

    private void parseValDecl(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.VarDecl);
        judgeNext(cur, TCode.INTTK);
        parseVarDef(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseVarDef(cur);
        }
        judgeNext(cur, TCode.SEMICN);
        father.addChild(cur);
    }

    private void parseVarDef(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.VarDef);
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        if (peek().equals(TCode.ASSIGN)) {
            addNextLeaf(cur);
            parseInitVal(cur);
        }
        father.addChild(cur);
    }

    private void parseInitVal(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.InitVal);
        if (peek().equals(TCode.LBRACE)) {
            addNextLeaf(cur);
            if (peek().equals(TCode.RBRACE)) {
                addNextLeaf(cur);
            } else {
                parseInitVal(cur);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseInitVal(cur);
                }
                judgeNext(cur, TCode.RBRACE);
            }
        } else {
            parseExp(cur);
        }
        father.addChild(cur);
    }

    private void parseFuncDef(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncDef);
        parseFuncType(cur);
        judgeNext(cur, TCode.IDENFR);
        judgeNext(cur, TCode.LPARENT);
        if (peek().equals(TCode.RPARENT)) {
            addNextLeaf(cur);
        } else {
            parseFuncFParams(cur);
            judgeNext(cur, TCode.RPARENT);
        }
        parseBlock(cur);
        father.addChild(cur);
    }

    private void parseMainFuncDef(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.MainFuncDef);
        judgeNext(cur, TCode.INTTK);
        judgeNext(cur, TCode.MAINTK);
        judgeNext(cur, TCode.LPARENT);
        judgeNext(cur, TCode.RPARENT);
        parseBlock(cur);
        father.addChild(cur);
    }

    private void parseFuncType(GTreeRoot father) {
        GTreeRoot cur = new GTreeRoot(NCode.FuncType);
        if (peek().equals(TCode.VOIDTK) || peek().equals(TCode.INTTK)) {
            addNextLeaf(cur);
        }
        father.addChild(cur);
    }

    private void parseFuncFParams(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncFParams);
        parseFuncFParam(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseFuncFParam(cur);
        }
        father.addChild(cur);
    }

    private void parseFuncFParam(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncFParam);
        judgeNext(cur, TCode.INTTK);
        judgeNext(cur, TCode.IDENFR);
        if (peek().equals(TCode.LBRACK)) {
            addNextLeaf(cur);
            judgeNext(cur, TCode.RBRACK);
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseConstExp(cur);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        father.addChild(cur);
    }

    private void parseBlock(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Block);
        judgeNext(cur, TCode.LBRACE);
        while (!peek().equals(TCode.RBRACE)) {
            parseBlockItem(cur);
        }
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseBlockItem(GTreeRoot father) throws ParserException {
        if (peek().equals(TCode.CONSTTK) || peek().equals(TCode.INTTK)) {
            parseDecl(father);
        } else {
            parseStmt(father);
        }
    }

    private void parseStmt(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Stmt);
        switch (peek()) {
            case PRINTFTK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.LPARENT);
                judgeNext(cur, TCode.STRCON);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseExp(cur);
                }
                judgeNext(cur, TCode.RPARENT);
                judgeNext(cur, TCode.SEMICN);
                break;
            case RETURNTK:
                addNextLeaf(cur);
                if (peek().equals(TCode.SEMICN)) {
                    addNextLeaf(cur);
                } else {
                    parseExp(cur);
                    judgeNext(cur, TCode.SEMICN);
                }
                break;
            case BREAKTK:
            case CONTINUETK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.SEMICN);
                break;
            case WHILETK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.LPARENT);
                parseCond(cur);
                judgeNext(cur, TCode.RPARENT);
                parseStmt(cur);
                break;
            case IFTK:
                addNextLeaf(cur);
                judgeNext(cur, TCode.LPARENT);
                parseCond(cur);
                judgeNext(cur, TCode.RPARENT);
                parseStmt(cur);
                if (peek().equals(TCode.ELSETK)) {
                    addNextLeaf(cur);
                    parseStmt(cur);
                }
                break;
            case LBRACE:
                parseBlock(cur);
                break;
            default:
                if (tokens.posCmp(TCode.ASSIGN, TCode.SEMICN) < 0) {
                    parseLVal(cur);
                    judgeNext(cur, TCode.ASSIGN);
                    if (peek().equals(TCode.GETINTTK)) {
                        addNextLeaf(cur);
                        judgeNext(cur, TCode.LPARENT);
                        judgeNext(cur, TCode.RPARENT);
                    } else {
                        parseExp(cur);
                    }
                    judgeNext(cur, TCode.SEMICN);
                } else {
                    if (peek().equals(TCode.SEMICN)) {
                        addNextLeaf(cur);
                    } else  {
                        parseExp(cur);
                        judgeNext(cur, TCode.SEMICN);
                    }
                }
        }
        father.addChild(cur);
    }

    private void parseExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Exp);
        parseAddExp(cur);
        father.addChild(cur);
    }

    private void parseCond(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Cond);
        parseLOrExp(cur);
        father.addChild(cur);
    }

    private void parseLVal(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.LVal);
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseExp(cur);
                judgeNext(cur, TCode.RBRACK);
            }
        }
        father.addChild(cur);
    }

    private void parsePrimaryExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.PrimaryExp);
        switch (peek()) {
            case LPARENT:
                addNextLeaf(cur);
                parseExp(cur);
                judgeNext(cur, TCode.RPARENT);
                break;
            case INTCON:
                parseNumber(cur);
                break;
            default:
                parseLVal(cur);
        }
        father.addChild(cur);
    }

    private void parseNumber(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.Number);
        judgeNext(cur, TCode.INTCON);
        father.addChild(cur);
    }

    private void parseUnaryExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.UnaryExp);
        switch (peek()) {
            case PLUS:
            case MINU:
            case NOT:
                parseUnaryOp(cur);
                parseUnaryExp(cur);
                break;
            case LPARENT:
            case INTCON:
                parsePrimaryExp(cur);
                break;
            default:
                if (peek(1).equals(TCode.LPARENT)) {
                    judgeNext(cur, TCode.IDENFR);
                    addNextLeaf(cur);
                    if (peek().equals(TCode.RPARENT)) {
                        addNextLeaf(cur);
                    } else {
                        parseFuncRParams(cur);
                        judgeNext(cur, TCode.RPARENT);
                    }
                } else {
                    parsePrimaryExp(cur);
                }
        }
        father.addChild(cur);
    }

    private void parseUnaryOp(GTreeRoot father) {
        GTreeRoot cur = new GTreeRoot(NCode.UnaryOp);
        addNextLeaf(cur);
        father.addChild(cur);
    }

    private void parseFuncRParams(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.FuncRParams);
        parseExp(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseExp(cur);
        }
        father.addChild(cur);
    }

    private void parseMulExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.MulExp, NCode.UnaryExp);
        father.addChild(cur);
    }

    private void parseAddExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.AddExp, NCode.MulExp);
        father.addChild(cur);
    }

    private void parseRelExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.RelExp, NCode.AddExp);
        father.addChild(cur);
    }

    private void parseEqExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.EqExp, NCode.RelExp);
        father.addChild(cur);
    }

    private void parseLAndExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.LAndExp, NCode.EqExp);
        father.addChild(cur);
    }

    private void parseLOrExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = leftRecur(NCode.LOrExp, NCode.LAndExp);
        father.addChild(cur);
    }

    private void parseConstExp(GTreeRoot father) throws ParserException {
        GTreeRoot cur = new GTreeRoot(NCode.ConstExp);
        parseAddExp(cur);
        father.addChild(cur);
    }

    private void judgeNext(GTreeRoot father, TCode code) throws ParserException {
        if (peek().equals(code)) {
            addNextLeaf(father);
        } else {
            throw new ParserException(tokens.getLastLine());
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

    private GTreeRoot leftRecur(NCode whole, NCode recur) throws ParserException {
        GTreeRoot cur = new GTreeRoot(whole);
        parse4NCode(cur, recur);
        GTreeRoot temp;
        while (CodeClassifier.isSeparator(whole, peek())) {
            temp = new GTreeRoot(whole);
            temp.addChild(cur);
            addNextLeaf(temp);
            parse4NCode(temp, recur);
            cur = temp;
        }
        return cur;
    }

    private void parse4NCode(GTreeRoot cur, NCode code) throws ParserException {
        switch (code) {
            case LAndExp:
                parseLAndExp(cur);
                break;
            case EqExp:
                parseEqExp(cur);
                break;
            case RelExp:
                parseRelExp(cur);
                break;
            case AddExp:
                parseAddExp(cur);
                break;
            case MulExp:
                parseMulExp(cur);
                break;
            case UnaryExp:
                parseUnaryExp(cur);
                break;
            default:
        }
    }
}