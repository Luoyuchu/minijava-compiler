class Test { 
	public static void main (String[] a) {
		System.out.println(new Start().start()); 
	} 
}
class Start {
	public int start() { 
		return 0;
	}
	
}
class End extends Start {
	public int start(int i) {
		return 0;
	}
}

