package main.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Printer {
    private static final Printer PRINTER = new Printer();
    private FileWriter fw;
    private BufferedWriter bw;

    public static Printer getInstance() {
        return PRINTER;
    }

    public void initial() {
        File file = new File("output.txt");
        try {
            fw = new FileWriter(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);
    }

    public void print(String s) {
        try {
            bw.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
