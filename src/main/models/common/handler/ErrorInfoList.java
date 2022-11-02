package main.models.common.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ErrorInfoList {
    private static final ErrorInfoList ERROR_INFO_LIST = new ErrorInfoList();
    private FileWriter fw;
    private BufferedWriter bw;
    private ArrayList<ErrorInfo> list;

    public static ErrorInfoList getInstance() {
        return  ERROR_INFO_LIST;
    }

    public void initial() {
        list = new ArrayList<>();
        File file = new File("error.txt");
        try {
            fw = new FileWriter(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addError(char t, int line) {
        list.add(new ErrorInfo(t, line));
    }

    public void print() {
        Collections.sort(list);
        for (ErrorInfo e : list) {
            try {
                bw.write(e.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

