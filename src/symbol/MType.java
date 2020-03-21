package symbol;

public class MType {
    protected String name = null;
    protected String categoryName = null;
    protected int row = -1, col = -1;

    public MType() {}
    public MType(String name) {
        this.setName(name);
    }
    public MType(String name, String categoryName) {
        this.setName(name);
        this.setCategoryName(categoryName);
    }
    public MType(String name, int row, int col) {
        this.setName(name);
        this.setRow(row);
        this.setCol(col);
    }
    public MType(String name, String categoryName, int row, int col) {
        this.setName(name);
        this.setCategoryName(categoryName);
        this.setRow(row);
        this.setCol(col);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
