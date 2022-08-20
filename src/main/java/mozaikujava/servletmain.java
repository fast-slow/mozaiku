package mozaikujava;

public class servletmain {

	public static void main(String[] args) {
		
	}
	public Object mozaiku(String startimg,String goalimg,String savefile) {
		
		System.out.println("実行");
		mozaiku mo = new mozaiku();
		return mo.excecution(startimg, goalimg,savefile);
	}
	

}
