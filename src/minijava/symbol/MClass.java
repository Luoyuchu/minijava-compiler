package minijava.symbol;

import java.util.HashMap;

public class MClass extends MIdentifier {
    protected HashMap<String, MMethod> methodHashMap = new HashMap<String, MMethod>();
    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();

    public MClass(String name, String parentName, int row, int col) {
        super(name, "CLASS", parentName, row, col);
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

    public int addMethod(MMethod newMethod) {
        if (methodHashMap.containsKey(newMethod.getName())) {
            return 1;
        }
        methodHashMap.put(newMethod.getName(), newMethod);
        return 0;
    }

    public MMethod getMethod(String id) {
        if (methodHashMap.get(id) != null) {
            return methodHashMap.get(id);
        }
        if (this.getParent() != null) {
            return ((MClass)this.getParent()).getMethod(id);
        }
        return null;
    }
}
