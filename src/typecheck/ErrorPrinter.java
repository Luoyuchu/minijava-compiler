package typecheck;

import java.util.ArrayList;

public class ErrorPrinter {
    ArrayList<String> errorInfo = new ArrayList<String>();

    public int getSize() {
        return errorInfo.size();
    }

    public void print(String msg, int row, int col) {
        errorInfo.add(msg + " at " + "line:" + row + " col:" + col);
    }

    public void printAll() {
        for (int i = 0; i < errorInfo.size(); ++i) {
            System.out.println(errorInfo.get(i));
        }
    }
}
