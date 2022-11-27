import main.lexer.Lexer;
import main.models.common.handler.ErrorInfoList;
import main.models.common.ast.TreeRoot;
import main.models.common.symbol.SymbolTable;
import main.models.common.ast.TokenSequence;
import main.parser.Parser;
import main.utils.Printer;

public class Compiler {
    private final Lexer lexer;
    private final Parser parser;

    public Compiler() {
        lexer = new Lexer();
        parser = new Parser();
    }

    private void compile() {
        Printer.getInstance().initial("error");

        lexer.analyse();
        TokenSequence tokens = lexer.getTokens();

        SymbolTable global = new SymbolTable(null);
        parser.parse(tokens, global);

        TreeRoot root = parser.getRoot();
        TreeRoot.setGlobal(global);
        root.llvm();

        ErrorInfoList.getInstance().print();

        Printer.getInstance().close();
    }

    public static void main(String[] args) {
        Compiler compiler = new Compiler();
        compiler.compile();
    }
}
