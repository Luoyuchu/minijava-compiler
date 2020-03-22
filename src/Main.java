import java.io.*;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.exception.DebugException;
import minijava.exception.TypeCheckException;
import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.Node;
import minijava.syntaxtree.VarDeclaration;
import minijava.visitor.BuildSymbolTableVisitor;
import minijava.visitor.DepthFirstVisitor;
import minijava.visitor.TypeCheckVisitor;
import minijava.symbol.MClass;
import minijava.symbol.MClassList;
import minijava.typecheck.ErrorPrinter;


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
			MClass errorClass = new MClass("NOT_A_CLASS", null, -1, -1);
			int ret = allClassList.checkClassExtend(errorClass);
			if (ret != 0) {
				if (errorClass.getName().equals("NOT_A_CLASS")) {
					throw new DebugException();
				}
				errorPrinter.print((ret == 1 ? "Extend type error " : "Circle extend ") + errorClass.getName(), errorClass.getRow(), errorClass.getCol());
				throw new TypeCheckException();
			}
			root.accept(new TypeCheckVisitor(allClassList, errorPrinter), null);
			System.out.println("Program type checked successfully");
		} catch (TypeCheckException e) {
			System.out.println("Type error");
			errorPrinter.printAll();
		} catch (RuntimeException e) {
			System.out.println("There is some bug!!!");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TokenMgrError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

