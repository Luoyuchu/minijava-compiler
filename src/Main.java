import java.io.*;

import exception.TypeCheckException;
import symbol.MClass;
import symbol.MClassList;
import typecheck.ErrorPrinter;
import visitor.*;
import syntaxtree.*;
import java.util.Scanner;

class MyVisitor extends DepthFirstVisitor {
	public void visit(Node n) {

	}
	public void visit(VarDeclaration n) {
		Identifier id = (Identifier)n.f1;
		System.out.println("VarName: " + id.f0.toString());
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
	}
}

public class Main {
	public static void main(String args[]){
		ErrorPrinter errorPrinter = new ErrorPrinter();
		try {
			InputStream in = new FileInputStream(args[0]);
			MClassList allClassList = new MClassList();
			Node root = new MiniJavaParser(in).Goal();
			root.accept(new BuildSymbolTableVisitor(allClassList, errorPrinter), null);
			MClass errorClass = new MClass();
			int ret = allClassList.checkClassExtend(errorClass);
			if (ret != 0) {
				errorPrinter.print((ret == 1 ? "Extend type error " : "Circle extend ") + errorClass.getName(), errorClass.getRow(), errorClass.getCol());
				throw new TypeCheckException();
			}
			root.accept(new TypeCheckVisitor(allClassList, errorPrinter), null);
			System.out.println(1);
			System.out.println("Program type checked successfully!");
		} catch (TypeCheckException e) {
			System.out.println(0);
			System.out.println("Type check failed!");
			errorPrinter.printAll();
		} catch (ParseException e) {
			System.out.println(0);
			e.printStackTrace();
		} catch (TokenMgrError e) {
			System.out.println(0);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(0);
			e.printStackTrace();
		}
	}
}

