class Test { 
	public static void main (String[] a) {
		System.out.println(new Start().start()); 
	} 
}
class Start {
	public int start() { 
		A a;
		B b;
		a = new B();
		b = new A();
		return 0;
	}
}
class A{
}
class B extends A{
}

