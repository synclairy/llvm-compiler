package main.models.common.handler;

import main.utils.Printer;
import java.util.ArrayList;
import java.util.Collections;

public class ErrorInfoList {
    private static final ErrorInfoList ERROR_INFO_LIST = new ErrorInfoList();
    private ArrayList<ErrorInfo> list = new ArrayList<>();

    public static ErrorInfoList getInstance() {
        return  ERROR_INFO_LIST;
    }

    public void addError(char t, int line) {
        list.add(new ErrorInfo(t, line));
    }

    public void print() {
        Collections.sort(list);
        int line = -1;
        for (ErrorInfo e : list) {
            if (line != e.getLine()) {
                Printer.getInstance().print(e.toString());
                line = e.getLine();
            }
        }
    }
}

