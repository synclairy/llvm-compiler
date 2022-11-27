package main.parser;

import main.models.common.ast.component.flow.*;
import main.models.common.ast.component.compute.*;
import main.models.common.ast.component.declare.*;
import main.models.common.handler.ErrorInfoList;
import main.models.common.ast.TreeLeaf;
import main.models.common.ast.TreeRoot;
import main.models.common.ast.NCode;
import main.models.common.symbol.SymbolTable;
import main.models.common.ast.TCode;
import main.models.common.ast.Token;
import main.models.common.ast.TokenSequence;
import main.utils.CharClassifier;
import main.utils.CodeClassifier;

public class Parser {
    private TreeRoot root;
    private TokenSequence tokens;

    public TreeRoot getRoot() {
        return root;
    }

    public Parser() {
        tokens = null;
    }

    public void parse(TokenSequence tokens, SymbolTable global) {
        this.tokens = tokens;
        root = new CompUnitNode();
        root.setTable(global);
        parseCompUnit();
    }

    private void parseCompUnit() {
        while (!peek(2).equals(TCode.LPARENT)) {
            parseDecl(root);
        }
        while (!peek(1).equals(TCode.MAINTK)) {
            parseFuncDef(root);
        }
        parseMainFuncDef(root);
        if (!peek().equals(TCode.EOF)) {
            //throw new ParserException(tokens.getLastLine());
        }
    }

    private void parseDecl(TreeRoot father) {
        if (peek().equals(TCode.CONSTTK)) {
            parseConstDecl(father);
        } else {
            parseValDecl(father);
        }
    }

    private void parseConstDecl(TreeRoot father) {
        TreeRoot cur = new ConstDeclNode();
        father.addChild(cur);
        addNextLeaf(cur);
        judgeNext(cur, TCode.INTTK);
        parseConstDef(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseConstDef(cur);
        }
        judgeNext(cur, TCode.SEMICN);
    }

    private void parseConstDef(TreeRoot father) {
        TreeRoot cur = new ConstDefNode();
        father.addChild(cur);
        String s = tokens.peekName();
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
    }

    private void parseConstInitVal(TreeRoot father) {
        TreeRoot cur = new ConstInitValNode();
        father.addChild(cur);
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
    }

    private void parseValDecl(TreeRoot father) {
        TreeRoot cur = new VarDeclNode();
        father.addChild(cur);
        judgeNext(cur, TCode.INTTK);
        parseVarDef(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseVarDef(cur);
        }
        judgeNext(cur, TCode.SEMICN);
    }

    private void parseVarDef(TreeRoot father) {
        TreeRoot cur = new VarDefNode();
        father.addChild(cur);
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
    }

    private void parseInitVal(TreeRoot father) {
        TreeRoot cur = new InitValNode();
        father.addChild(cur);
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
    }

    private void parseFuncDef(TreeRoot father) {
        TreeRoot cur = new FuncDefNode();
        father.addChild(cur);
        parseFuncType(cur);
        judgeNext(cur, TCode.IDENFR);
        judgeNext(cur, TCode.LPARENT);
        if (peek().equals(TCode.INTTK)) {
            parseFuncFParams(cur);
        }
        judgeNext(cur, TCode.RPARENT);
        parseBlock(cur);
    }

    private void parseMainFuncDef(TreeRoot father) {
        TreeRoot cur = new MainFuncDefNode();
        father.addChild(cur);
        judgeNext(cur, TCode.INTTK);
        judgeNext(cur, TCode.MAINTK);
        judgeNext(cur, TCode.LPARENT);
        judgeNext(cur, TCode.RPARENT);
        parseBlock(cur);
    }

    private void parseFuncType(TreeRoot father) {
        TreeRoot cur = new FuncTypeNode();
        father.addChild(cur);
        if (peek().equals(TCode.VOIDTK) || peek().equals(TCode.INTTK)) {
            addNextLeaf(cur);
        }
    }

    private void parseFuncFParams(TreeRoot father) {
        TreeRoot cur = new FuncFParamsNode();
        father.addChild(cur);
        parseFuncFParam(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseFuncFParam(cur);
        }
    }

    private void parseFuncFParam(TreeRoot father) {
        TreeRoot cur = new FuncFParamNode();
        father.addChild(cur);
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
    }

    private void parseBlock(TreeRoot father) {
        TreeRoot cur = new BlockNode();
        father.addChild(cur);
        judgeNext(cur, TCode.LBRACE);
        while (!peek().equals(TCode.RBRACE)) {
            parseBlockItem(cur);
        }
        addNextLeaf(cur);
    }

    private void parseBlockItem(TreeRoot father) {
        if (peek().equals(TCode.CONSTTK) || peek().equals(TCode.INTTK)) {
            parseDecl(father);
        } else {
            parseStmt(father);
        }
    }

    private void parseStmt(TreeRoot father) {
        TreeRoot cur = new StmtNode();
        father.addChild(cur);
        switch (peek()) {
            case PRINTFTK:
                addNextLeaf(cur);
                int line = tokens.getLastLine();
                judgeNext(cur, TCode.LPARENT);
                int cnt = CharClassifier.countArgs(tokens.getPeekValue());
                judgeNext(cur, TCode.STRCON);
                while (peek().equals(TCode.COMMA)) {
                    addNextLeaf(cur);
                    parseExp(cur);
                    cnt--;
                }
                if (cnt != 0) {
                    addError('l', line);
                }
                judgeNext(cur, TCode.RPARENT);
                judgeNext(cur, TCode.SEMICN);
                break;
            case RETURNTK:
                addNextLeaf(cur);
                if (CodeClassifier.isFirstOfExp(peek())) {
                    parseExp(cur);
                }
                judgeNext(cur, TCode.SEMICN);
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
    }

    private void parseExp(TreeRoot father) {
        TreeRoot cur = new ExpNode();
        father.addChild(cur);
        parseAddExp(cur);
    }

    private void parseCond(TreeRoot father) {
        TreeRoot cur = new CondNode();
        father.addChild(cur);
        parseLOrExp(cur);
    }

    private void parseLVal(TreeRoot father) {
        TreeRoot cur = new LValNode();
        father.addChild(cur);
        judgeNext(cur, TCode.IDENFR);
        for (int i = 0; i < 2; i++) {
            if (peek().equals(TCode.LBRACK)) {
                addNextLeaf(cur);
                parseExp(cur);
                judgeNext(cur, TCode.RBRACK);
            }
        }
    }

    private void parsePrimaryExp(TreeRoot father) {
        TreeRoot cur = new PrimaryExpNode();
        father.addChild(cur);
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
    }

    private void parseNumber(TreeRoot father) {
        TreeRoot cur = new NumberNode();
        father.addChild(cur);
        judgeNext(cur, TCode.INTCON);
    }

    private void parseUnaryExp(TreeRoot father) {
        TreeRoot cur = new UnaryExpNode();
        father.addChild(cur);
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
                    if (peek().equals(TCode.IDENFR) || peek().equals(TCode.GETINTTK)) {
                        addNextLeaf(cur);
                    }
                    addNextLeaf(cur);
                    if (peek().equals(TCode.RPARENT)) {
                        addNextLeaf(cur);
                    } else {
                        if (peek() != TCode.SEMICN) {
                            parseFuncRParams(cur);
                        }
                        judgeNext(cur, TCode.RPARENT);
                    }
                } else {
                    parsePrimaryExp(cur);
                }
        }
    }

    private void parseUnaryOp(TreeRoot father) {
        TreeRoot cur = new UnaryOpNode();
        father.addChild(cur);
        addNextLeaf(cur);
    }

    private void parseFuncRParams(TreeRoot father) {
        TreeRoot cur = new FuncRParamsNode();
        father.addChild(cur);
        parseExp(cur);
        while (peek().equals(TCode.COMMA)) {
            addNextLeaf(cur);
            parseExp(cur);
        }
    }

    private void parseMulExp(TreeRoot father) {
        leftRecur(father, NCode.MulExp, NCode.UnaryExp);
    }

    private void parseAddExp(TreeRoot father) {
        leftRecur(father, NCode.AddExp, NCode.MulExp);
    }

    private void parseRelExp(TreeRoot father) {
        leftRecur(father, NCode.RelExp, NCode.AddExp);
    }

    private void parseEqExp(TreeRoot father) {
        leftRecur(father, NCode.EqExp, NCode.RelExp);
    }

    private void parseLAndExp(TreeRoot father) {
        leftRecur(father, NCode.LAndExp, NCode.EqExp);
    }

    private void parseLOrExp(TreeRoot father) {
        leftRecur(father, NCode.LOrExp, NCode.LAndExp);
    }

    private void parseConstExp(TreeRoot father) {
        TreeRoot cur = new ConstExpNode();
        father.addChild(cur);
        parseAddExp(cur);
    }

    private void judgeNext(TreeRoot father, TCode code) {
        if (peek().equals(code)) {
            addNextLeaf(father);
        } else {
            int line = tokens.getLastLine();
            switch (code) {
                case SEMICN:
                    addError('i', line);
                    father.addChild(new TreeLeaf(new Token(TCode.SEMICN, "'")));
                    break;
                case RPARENT:
                    addError('j', line);
                    father.addChild(new TreeLeaf(new Token(TCode.RPARENT, ")")));
                    break;
                case RBRACK:
                    addError('k', line);
                    father.addChild(new TreeLeaf(new Token(TCode.RBRACK, "]")));
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

    private void leftRecur(TreeRoot father, NCode whole, NCode recur) {
        TreeRoot cur = create4NCode(whole);
        assert cur != null;
        SymbolTable table = father.getTable();
        cur.setTable(table);
        parse4NCode(cur, recur);
        TreeRoot temp;
        while (CodeClassifier.isSeparator(whole, peek())) {
            temp = create4NCode(whole);
            assert temp != null;
            temp.addChild(cur);
            temp.setTable(table);
            addNextLeaf(temp);
            parse4NCode(temp, recur);
            cur = temp;
        }
        father.addChild(cur);
    }

    private void parse4NCode(TreeRoot cur, NCode code) {
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

    private TreeRoot create4NCode(NCode code) {
        switch (code) {
            case LOrExp:
                return new LOrExpNode();
            case LAndExp:
                return new LAndExpNode();
            case EqExp:
                return new EqExpNode();
            case RelExp:
                return new RelExpNode();
            case AddExp:
                return new AddExpNode();
            case MulExp:
                return new MulExpNode();
            case UnaryExp:
                return new UnaryExpNode();
            default:
        }
        return null;
    }
    
    private void addError(char c, int l) {
        ErrorInfoList.getInstance().addError(c, l);
    }
}