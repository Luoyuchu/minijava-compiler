package symbol;

public class MVar extends MIdentifier {
    protected String typeName;

    public MVar(String typeName, int row, int col) {
        super("TMP_VAR", "TMP_VAR", (String) null, row, col);
        this.setTypeName(typeName);
    }
    public MVar(String name, String typeName, MIdentifier parent, int row, int col) {
        super(name, "VAR", parent, row, col);
        this.setTypeName(typeName);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
