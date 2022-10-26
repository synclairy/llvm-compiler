import main.lexer.Lexer;
import main.models.common.ErrorInfoList;
import main.models.common.symbol.SymbolTable;
import main.models.common.TokenSequence;
import main.models.exceptions.CompilerException;
import main.parser.Parser;

public class Compiler {
    private final Lexer lexer;
    private final Parser parser;

    public Compiler() {
        lexer = new Lexer();
        parser = new Parser();
    }

    private void compile() throws CompilerException {
        // Printer.getInstance().initial();

        ErrorInfoList.getInstance().initial();

        lexer.analyse();
        TokenSequence tokens = lexer.getTokens();

        SymbolTable global = new SymbolTable(null, null);
        parser.parse(tokens, global);

        global.print();
        // GTreeRoot root = parser.getRoot();

        // ErrorInfoList.getInstance().print();
        // ErrorInfoList.getInstance().close();

        // Printer.getInstance().close();
    }
    public static void main(String[] args) {
        Compiler compiler = new Compiler();
        try {
            compiler.compile();
        } catch (CompilerException e) {
            e.print();
        }
    }
}
