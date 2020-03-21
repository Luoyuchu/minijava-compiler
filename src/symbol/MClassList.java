package symbol;

import typecheck.ErrorPrinter;


import java.util.HashMap;
import java.util.HashSet;

public class MClassList extends MType {
    protected String mainClassName;
    protected HashMap<String, MClass> classHashMap = new HashMap<>();

    public MClassList() {
        super(null, "LIST");
    }

    public int checkCircleExtend(MClass start) {
        HashSet<String> tag = new HashSet<>();
        MClass cur = start;
        while (cur != null) {
            if (tag.contains(cur.getName())) {
                return 1;
            }
            tag.add(cur.getName());
            cur = (MClass)cur.getParent();
        }
        return 0;
    }

    public int checkClassExtend(MClass errorClass) {
        for (String key : classHashMap.keySet()) {
            MClass theClass = classHashMap.get(key);
            if (theClass.getParentName() != null) {
                String parentName = theClass.getParentName();
                if (errorClass != null) {
                    errorClass.setName(parentName);
                    errorClass.setRow(theClass.getRow());
                    errorClass.setCol(theClass.getCol());
                }
                if (!containClass(parentName)) {
                    return 1;
                }
                theClass.setParent(getClass(parentName));
                if (checkCircleExtend(theClass) != 0) {
                    return 2;
                }
            }
        }
        return 0;
    }

    public int addClass(MClass newClass) {
        if (classHashMap.containsKey(newClass.getName())) {
            return 1;
        }
        classHashMap.put(newClass.getName(), newClass);
        return 0;
    }

    public MClass getClass(String id) {
        return classHashMap.get(id);
    }

    public boolean containClass(String id) {
        return classHashMap.containsKey(id);
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public int checkTypeDeclared(String typeName) {
        if (typeName.equals("int") || typeName.equals("int[]") || typeName.equals("boolean")) {
            return 0;
        }
        if (containClass(typeName)) {
            return 0;
        }
        return 1;
    }

    public int checkSubType(String typeNameA, String typeNameB) {
        if (typeNameA.equals(typeNameB)) {
            return 0;
        }
        MClass classA = getClass(typeNameA), classB = getClass(typeNameB);
        if (classB != null) {
            while (classA != null) {
                if (classA == classB) {
                    return 0;
                }
                classA = (MClass)classA.getParent();
            }
        }
        return 1;
    }
}
