package minijava.visitor;

import minijava.exception.TypeCheckException;
import minijava.symbol.*;
import minijava.syntaxtree.*;
import minijava.typecheck.ErrorPrinter;


import java.util.ArrayList;

public class TypeCheckVisitor extends GJDepthFirst<MType, MType> {
    MClassList classList;
    ErrorPrinter errorPrinter;

    public TypeCheckVisitor(MClassList classList, ErrorPrinter errorPrinter) {
        this.classList = classList;
        this.errorPrinter = errorPrinter;
    }

    public boolean checkTypeDeclared(MType type) {
        if (classList.checkTypeDeclared(type.getName()) != 0) {
            errorPrinter.print("Undefined type " + type.getName(), type.getRow(), type.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }
    public boolean checkVarType(MVar var, String typename) {
        if (!typename.equals(var.getTypeName())) {
            errorPrinter.print("Not a " + typename, var.getRow(), var.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }
    public MClass getVarClass(MVar var) {
        MClass varClass = classList.getClass(var.getTypeName());
        if (varClass == null) {
            errorPrinter.print("Not a class", var.getRow(), var.getCol());
            throw new TypeCheckException();
        }
        return varClass;
    }
    public boolean checkTypeClass(MType type) {
        if (!classList.containClass(type.getName())) {
            errorPrinter.print("Not a class", type.getRow(), type.getCol());
            throw new TypeCheckException();
            //return true;
        }
        return false;
    }
    public boolean checkSubType(MVar var1, String typename) {
        if (classList.checkSubType(var1.getTypeName(), typename) != 0) {
            errorPrinter.print("Not an equal or subType", var1.getRow(), var1.getCol());
            throw new TypeCheckException();
        }
        return false;
    }
    public boolean checkMethodParameter(MMethod methodCall, MMethod methodModel) {
        ArrayList<MVar> expList, paramList;
        if (methodCall == null) {
            if (methodModel.getParamList().size() == 0) {
                return false;
            }
            else {
                errorPrinter.print("Parameter wrong", methodModel.getRow(), methodModel.getCol());
                throw new TypeCheckException();
            }
        }
        expList = methodCall.getParamList();
        paramList = methodModel.getParamList();
        if (expList.size() != paramList.size()) {
            errorPrinter.print("Parameter wrong", methodCall.getRow(), methodCall.getCol());
            throw new TypeCheckException();
        }
        for (int i = 0; i < expList.size(); ++i) {
            if (classList.checkSubType(expList.get(i).getTypeName(), paramList.get(i).getTypeName()) != 0) {
                errorPrinter.print("Parameter wrong", methodCall.getRow(), methodCall.getCol());
                throw new TypeCheckException();
            }
        }
        return false;
    }
    public boolean checkMethodOverload(MMethod method) {
        if (method.getParent().getParent() == null) {
            return false;
        }
        MMethod anotherMethod = ((MClass) method.getParent().getParent()).getMethod(method.getName());
        if (anotherMethod == null) return false;
        boolean failTag = false;
        // if (classList.checkSubType(method.getReturnTypeName(), anotherMethod.getReturnTypeName()) != 0)
        if (!method.getReturnTypeName().equals(anotherMethod.getReturnTypeName())) {
            failTag = true;
        }
        ArrayList<MVar> listA, listB;
        listA = method.getParamList();
        listB = anotherMethod.getParamList();
        if (listA.size() != listB.size()) {
            failTag = true;
        }
        else {
            for (int i = 0; i < listA.size(); ++i) {
                if (!listA.get(i).getTypeName().equals(listB.get(i).getTypeName())) {
                    failTag = true;
                    break;
                }
            }
        }
        if (failTag) {
            errorPrinter.print("Class with same name but not override " + method.getName(), method.getRow(), method.getCol());
            throw new TypeCheckException();
        }
        return false;
    }

    public MClass getClass(MType id) {
        MClass theClass = classList.getClass(id.getName());
        if (theClass == null) {
            errorPrinter.print("Class is not found " + id.getName(), id.getRow(), id.getCol());
            throw new TypeCheckException();
        }
        return theClass;
    }
    public MMethod getMethod(MClass n, MType id) {
        MMethod theMethod = n.getMethod(id.getName());
        if (theMethod == null) {
            errorPrinter.print("Method is not found " + id.getName(), id.getRow(), id.getCol());
            throw new TypeCheckException();
        }
        return theMethod;
    }
    public MVar getVar(MIdentifier n, MType id) {
        MVar theVar = n.getVar(id.getName());
        if (theVar == null) {
            errorPrinter.print("Variable is not found " + id.getName(), id.getRow(), id.getCol());
            throw new TypeCheckException();
        }
        return theVar;
    }


    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public MType visit(Goal n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return null;
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
        MClass theClass = getClass(id);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        id = new MType("main", n.f6.beginLine, n.f6.beginColumn);
        MMethod theMethod = getMethod(theClass, id);
        n.f7.accept(this, theMethod);
        n.f8.accept(this, theMethod);
        n.f9.accept(this, theMethod);
        n.f10.accept(this, theMethod);
        n.f11.accept(this, theMethod);
        n.f12.accept(this, theMethod);
        n.f13.accept(this, theMethod);
        n.f14.accept(this, theMethod);
        n.f15.accept(this, theMethod);
        n.f16.accept(this, theMethod);
        n.f17.accept(this, theMethod);
        return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public MType visit(TypeDeclaration n, MType argu) {
        n.f0.accept(this, argu);
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
        MClass theClass = getClass(id);
        n.f2.accept(this, theClass);
        n.f3.accept(this, theClass);
        n.f4.accept(this, theClass);
        n.f5.accept(this, theClass);
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
        MClass theClass = getClass(id);
        n.f2.accept(this, theClass);
        n.f3.accept(this, theClass);
        n.f4.accept(this, theClass);
        n.f5.accept(this, theClass);
        n.f6.accept(this, theClass);
        n.f7.accept(this, theClass);
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public MType visit(VarDeclaration n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MType id = n.f2.accept(this, argu);
        MMethod theMethod = getMethod((MClass)argu, id);
        checkMethodOverload(theMethod);
        n.f3.accept(this, theMethod);
        n.f4.accept(this, theMethod);
        n.f5.accept(this, theMethod);
        n.f6.accept(this, theMethod);
        n.f7.accept(this, theMethod);
        n.f8.accept(this, theMethod);
        n.f9.accept(this, theMethod);
        MVar exp = (MVar)n.f10.accept(this, theMethod);
        checkSubType(exp, theMethod.getReturnTypeName());
        n.f11.accept(this, theMethod);
        n.f12.accept(this, theMethod);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public MType visit(FormalParameterList n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public MType visit(FormalParameter n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public MType visit(FormalParameterRest n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public MType visit(Type n, MType argu) {
        MType type = n.f0.accept(this, argu);
        checkTypeDeclared(type);
        return null;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public MType visit(ArrayType n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return new MType("int[]");
    }

    /**
     * f0 -> "boolean"
     */
    public MType visit(BooleanType n, MType argu) {
        n.f0.accept(this, argu);
        return new MType("boolean");
    }

    /**
     * f0 -> "int"
     */
    public MType visit(IntegerType n, MType argu) {
        n.f0.accept(this, argu);
        return new MType("int");
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public MType visit(Statement n, MType argu) {
        n.f0.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public MType visit(Block n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public MType visit(AssignmentStatement n, MType argu) {
        MType id = n.f0.accept(this, argu);
        MVar exp1 = getVar((MIdentifier)argu, id);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        checkSubType(exp2, exp1.getTypeName());
        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public MType visit(ArrayAssignmentStatement n, MType argu) {
        MType id = n.f0.accept(this, argu);
        MVar exp1 = getVar((MIdentifier)argu, id);
        checkVarType(exp1, "int[]");
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp2, "int");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        MVar exp3 = (MVar)n.f5.accept(this, argu);
        checkVarType(exp3, "int");
        n.f6.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public MType visit(IfStatement n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp = (MVar)n.f2.accept(this, argu);
        checkVarType(exp, "boolean");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public MType visit(WhileStatement n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp = (MVar)n.f2.accept(this, argu);
        checkVarType(exp, "boolean");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public MType visit(PrintStatement n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp = (MVar)n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        checkVarType(exp, "int");
        return null;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public MType visit(Expression n, MType argu) {
        MVar exp = (MVar)n.f0.accept(this, argu);
        return exp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public MType visit(AndExpression n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp1, "boolean");
        checkVarType(exp2, "boolean");
        MVar ret = new MVar("boolean", n.f1.beginLine, n.f1.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public MType visit(CompareExpression n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp1, "int");
        checkVarType(exp2, "int");
        MVar ret = new MVar("boolean", n.f1.beginLine, n.f1.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public MType visit(PlusExpression n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp1, "int");
        checkVarType(exp2, "int");
        MVar ret = new MVar("int", n.f1.beginLine, n.f1.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public MType visit(MinusExpression n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp1, "int");
        checkVarType(exp2, "int");
        MVar ret = new MVar("int", n.f1.beginLine, n.f1.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public MType visit(TimesExpression n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        checkVarType(exp1, "int");
        checkVarType(exp2, "int");
        MVar ret = new MVar("int", n.f1.beginLine, n.f1.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public MType visit(ArrayLookup n, MType argu) {
        MVar exp1 = (MVar)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        MVar exp2 = (MVar)n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        checkVarType(exp1, "int[]");
        checkVarType(exp2, "int");
        MVar ret = new MVar("int", exp1.getRow(), exp1.getCol());
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public MType visit(ArrayLength n, MType argu) {
        MVar exp = (MVar)n.f0.accept(this, argu);
        checkVarType(exp, "int[]");
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        MVar ret = new MVar("int", n.f2.beginLine, n.f2.beginColumn);
        return ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public MType visit(MessageSend n, MType argu) {
        MVar exp = (MVar)n.f0.accept(this, argu);
        MClass expClass = getVarClass(exp);
        n.f1.accept(this, argu);
        MType id = n.f2.accept(this, argu);
        MMethod theMethod = getMethod(expClass, id);
        n.f3.accept(this, argu);
        MMethod expList = (MMethod)n.f4.accept(this, argu);
        checkMethodParameter(expList, theMethod);
        n.f5.accept(this, argu);
        MVar ret = new MVar(theMethod.getReturnTypeName(), id.getRow(), id.getCol());
        return ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public MType visit(ExpressionList n, MType argu) {
        MMethod expList = new MMethod("MSG_SEND", null, (MIdentifier)argu, -1, -1);
        MVar exp = (MVar)n.f0.accept(this, argu);
        expList.addPar(exp);
        expList.setRow(exp.getRow());
        expList.setCol(exp.getCol());
        n.f1.accept(this, expList);
        return expList;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public MType visit(ExpressionRest n, MType argu) {
        MMethod expList = (MMethod)argu;
        n.f0.accept(this, expList.getParent());
        MVar exp = (MVar)n.f1.accept(this, expList.getParent());
        expList.addPar(exp);
        return null;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public MType visit(PrimaryExpression n, MType argu) {
        MVar exp;
        MType ret = n.f0.accept(this, argu);
        if (ret instanceof MVar) {
            exp = (MVar)ret;
        }
        else {
            exp = getVar((MIdentifier)argu, ret);
        }
        return exp;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public MType visit(IntegerLiteral n, MType argu) {
        return new MVar("int", n.f0.beginLine, n.f0.beginColumn);
    }

    /**
     * f0 -> "true"
     */
    public MType visit(TrueLiteral n, MType argu) {
        return new MVar("boolean", n.f0.beginLine, n.f0.beginColumn);
    }

    /**
     * f0 -> "false"
     */
    public MType visit(FalseLiteral n, MType argu) {
        return new MVar("boolean", n.f0.beginLine, n.f0.beginColumn);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MType visit(Identifier n, MType argu) {
        return new MType(n.f0.toString(), "IDENTIFIER_STRING", n.f0.beginLine, n.f0.beginColumn);
    }

    /**
     * f0 -> "this"
     */
    public MType visit(ThisExpression n, MType argu) {
        MClass curClass = (MClass)((MMethod)argu).getParent();
        n.f0.accept(this, argu);
        MVar exp = new MVar(curClass.getName(), n.f0.beginLine, n.f0.beginColumn);
        return exp;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public MType visit(ArrayAllocationExpression n, MType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        MVar exp = (MVar)n.f3.accept(this, argu);
        checkVarType(exp, "int");
        MVar ret = new MVar("int[]", n.f1.beginLine, n.f1.beginColumn);
        n.f4.accept(this, argu);
        return ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public MType visit(AllocationExpression n, MType argu) {
        n.f0.accept(this, argu);
        MType type = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        checkTypeClass(type);
        MVar exp = new MVar(type.getName(), type.getRow(), type.getCol());
        n.f3.accept(this, argu);
        return exp;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public MType visit(NotExpression n, MType argu) {
        n.f0.accept(this, argu);
        MVar exp = (MVar)n.f1.accept(this, argu);
        checkVarType(exp, "boolean");
        return exp;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public MType visit(BracketExpression n, MType argu) {
        n.f0.accept(this, argu);
        MVar exp = (MVar)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return exp;
    }
}
