package minijava.visitor;

import minijava.exception.TypeCheckException;
import minijava.symbol.*;
import minijava.syntaxtree.*;
import minijava.typecheck.ErrorPrinter;

public class BuildSymbolTableVisitor extends GJDepthFirst<MType, MType> {

    ErrorPrinter errorPrinter;
    MClassList classList;

    public BuildSymbolTableVisitor(MClassList classList, ErrorPrinter errorPrinter) {
        this.classList = classList;
        this.errorPrinter = errorPrinter;
    }

    boolean checkExtendMainClass(MType id, MType parentId) {
        if (parentId.getName().equals(classList.getMainClassName())) {
            errorPrinter.print(id.getName() + " extends from MainClass", id.getRow(), id.getCol());
            throw new TypeCheckException();
        }
        return false;
    }

    boolean addVar(MIdentifier n, MVar newVar) {
        if (n.addVar(newVar) != 0) {
            errorPrinter.print("Variable " + newVar.getName() + " duplicate definition", newVar.getRow(), newVar.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }

    boolean addPar(MMethod n, MVar newVar) {
        if (n.addPar(newVar) != 0) {
            errorPrinter.print("Parameter " + newVar.getName() + " duplicate definition", newVar.getRow(), newVar.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }

    boolean addMethod(MClass n, MMethod newMethod) {
        if (n.addMethod(newMethod) != 0) {
            errorPrinter.print("Method " + newMethod.getName() + " duplicate definition", newMethod.getRow(), newMethod.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }

    boolean addClass(MClass newClass) {
        if (classList.addClass(newClass) != 0) {
            errorPrinter.print("Class " + newClass.getName() + " duplicate definition", newClass.getRow(), newClass.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public MType visit(MainClass n, MType argu) {
        n.f0.accept(this, argu);
        MType id = n.f1.accept(this, argu);
        MClass newClass = new MClass(id.getName(), null, id.getRow(), id.getCol());
        addClass(newClass);
        classList.setMainClassName(id.getName());

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        MMethod newMethod = new MMethod("main", null, newClass, n.f6.beginLine, n.f6.beginColumn);
        addMethod(newClass, newMethod);

        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        id = n.f11.accept(this, argu);
        MVar newVar = new MVar(id.getName(), "String", newMethod, id.getRow(), id.getCol());
        addVar(newMethod, newVar);

        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, newMethod);

        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        return null;
    }


    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public MType visit(ClassDeclaration n, MType argu) {
        n.f0.accept(this, argu);
        MType id = n.f1.accept(this, argu);
        MClass newClass = new MClass(id.getName(), null, id.getRow(), id.getCol());
        addClass(newClass);

        n.f2.accept(this, argu);
        n.f3.accept(this, newClass);

        n.f4.accept(this, newClass);

        n.f5.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public MType visit(ClassExtendsDeclaration n, MType argu) {
        n.f0.accept(this, argu);
        MType id = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        MType parentId = n.f3.accept(this, argu);
        checkExtendMainClass(id, parentId);
        MClass newClass = new MClass(id.getName(), parentId.getName(), id.getRow(), id.getCol());
        addClass(newClass);

        n.f4.accept(this, argu);
        n.f5.accept(this, newClass);

        n.f6.accept(this, newClass);

        n.f7.accept(this, argu);
        return null;
    }


    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public MType visit(MethodDeclaration n, MType argu) {
        MClass parentClass = (MClass)argu;

        n.f0.accept(this, argu);
        MType type = n.f1.accept(this, argu);
        MType id = n.f2.accept(this, argu);
        MMethod newMethod = new MMethod(id.getName(), type.getName(), parentClass, id.getRow(), id.getCol());
        addMethod(parentClass, newMethod);
        n.f3.accept(this, newMethod);
        n.f4.accept(this, newMethod);
        n.f5.accept(this, newMethod);
        n.f6.accept(this, newMethod);
        n.f7.accept(this, newMethod);
        n.f8.accept(this, newMethod);
        n.f9.accept(this, newMethod);
        n.f10.accept(this, newMethod);
        n.f11.accept(this, newMethod);
        n.f12.accept(this, newMethod);
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public MType visit(FormalParameter n, MType argu) {
        MMethod parentMethod = (MMethod)argu;
        MType type = n.f0.accept(this, argu);
        MType id = n.f1.accept(this, argu);
        MVar newVar = new MVar(id.getName(), type.getName(), parentMethod, id.getRow(), id.getCol());
        addPar(parentMethod, newVar);
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public MType visit(VarDeclaration n, MType argu) {
        MIdentifier parent = (MIdentifier)argu;
        MType type = n.f0.accept(this, argu);
        MType id = n.f1.accept(this, argu);
        MVar newVar = new MVar(id.getName(), type.getName(), parent, id.getRow(), id.getCol());
        addVar(parent, newVar);

        n.f2.accept(this, argu);
        return null;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MType visit(Identifier n, MType argu) {
        return new MType(n.f0.toString(), "IDENTIFIER_STRING", n.f0.beginLine, n.f0.beginColumn);
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public MType visit(Type n, MType argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public MType visit(ArrayType n, MType argu) {
        return new MType("int[]", "BASIC");
    }

    /**
     * f0 -> "boolean"
     */
    public MType visit(BooleanType n, MType argu) {
        return new MType("boolean", "BASIC");
    }

    /**
     * f0 -> "int"
     */
    public MType visit(IntegerType n, MType argu) {
        return new MType("int", "BASIC");
    }
}
