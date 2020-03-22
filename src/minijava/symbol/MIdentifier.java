package minijava.symbol;

import minijava.exception.DebugException;

public class MIdentifier extends MType{
    protected String parentName = null;
    protected MIdentifier parent = null;

    public MIdentifier(String name, String categoryName, String parentName, int row, int col) {
        super(name, categoryName, row, col);
        this.setParentName(parentName);
    }
    public MIdentifier(String name, String categoryName, MIdentifier parent, int row, int col) {
        super(name, categoryName, row, col);
        this.setParent(parent);
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public MIdentifier getParent() {
        return parent;
    }

    public void setParent(MIdentifier parent) {
        this.parent = parent;
    }

    public int addVar(MVar newVar) {
        System.out.println("something error! MIdentifier.addVar");
        throw new DebugException();
        //return 2;
    }
    public MVar getVar(String id) {
        System.out.println("something error! MIdentifier.getVar");
        throw new DebugException();
        //return null;
    }
}
