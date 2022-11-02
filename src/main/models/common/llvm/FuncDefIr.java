package main.models.common.llvm;

import java.util.ArrayList;

public class FuncDefIr implements IR {
    private final boolean isVoid;
    private final String name;
    private final ArrayList<Integer> levels;

    public FuncDefIr(boolean isVoid, String name, ArrayList<Integer> levels) {
        this.isVoid = isVoid;
        this.name = name;
        this.levels = levels;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\ndefine dso_local ");
        s.append(isVoid ? "void @" : "i32 @");
        s.append(name).append("(");
        for (int i = 0; i < levels.size(); i++) {
            switch (levels.get(i)) {
                case 0:
                    s.append("i32 ");
                    break;
                case 1:
                    s.append("i32* ");
                    break;
                default:
                    s.append("[").append(levels.get(i) - 1).append(" x i32]* ");
            }
            //s.append("%").append(i);
            if (i != levels.size() - 1) {
                s.append(", ");
            }
        }
        s.append(") #0 {\n");
        return s.toString();
    }
}
