import main.lexer.Lexer;
import main.models.common.TokenSequence;
import main.models.exceptions.CompilerException;
import main.models.common.GTreeRoot;
import main.parser.Parser;
import main.utils.Printer;

public class Compiler {
    private final Lexer lexer;
    private final Parser parser;

    public Compiler() {
        lexer = new Lexer();
        parser = new Parser();
    }

    private void compile() throws CompilerException {
        Printer.getInstance().initial();

        lexer.analyse();
        TokenSequence tokens = lexer.getTokens();

        parser.parse(tokens);
        GTreeRoot root = parser.getRoot();

        root.print();
        Printer.getInstance().close();
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
