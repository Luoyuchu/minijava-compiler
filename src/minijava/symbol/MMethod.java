package minijava.symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class MMethod extends MIdentifier {
    protected String returnTypeName = null;
    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();
    protected ArrayList<MVar> paramList = new ArrayList<MVar>();

    public MMethod(String name, String returnTypeName, MIdentifier parent, int row, int col) {
        super(name, "METHOD", parent, row, col);
        this.setReturnTypeName(returnTypeName);
    }

    public String getReturnTypeName() {
        return returnTypeName;
    }

    public void setReturnTypeName(String returnTypeName) {
        this.returnTypeName = returnTypeName;
    }

    public ArrayList<MVar> getParamList() {
        return paramList;
    }

    public int addPar(MVar newVar) {
        paramList.add(newVar);
        return addVar(newVar);
    }

    public int addVar(MVar newVar) {
        if (varHashMap.containsKey(newVar.getName())) {
            return 1;
        }
        varHashMap.put(newVar.getName(), newVar);
        return 0;
    }

    public MVar getVar(String id) {
        if (varHashMap.get(id) != null) {
            return varHashMap.get(id);
        }
        if (this.getParent() != null){
            return this.getParent().getVar(id);
        }
        return null;
    }

}
