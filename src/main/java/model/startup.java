package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mozaikujava.servletmain;
import java.awt.image.BufferedImage;
/**
 * Servlet implementation class startup
 */
@WebServlet("")
public class startup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Path path = Paths.get("");
	static String currentpath = path.toAbsolutePath().toString();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public startup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		start(request,response);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	void start(HttpServletRequest request, HttpServletResponse response) {
		//String imgpath = getServletContext().getRealPath("/img")+"/";
		String imginpath = getServletContext().getRealPath("/img");
		File dir = new File(imginpath);
		File[] list = dir.listFiles();
		ArrayList<String> imglistname = new ArrayList<String>();
		for(int i=0; i<list.length; i++) {
			  imglistname.add(list[i].getName());     //ファイル名のみ
		    }
		//imglistname.remove(imglistname.indexOf(".DS_Store"));
		javabeans javabeans = new javabeans();
		javabeans.setimgpath(imglistname);
		javabeans.setimginpath(imginpath);
		HttpSession session = request.getSession();
		session.setAttribute("javabeans", javabeans);
		
		RequestDispatcher dispatch = request.getRequestDispatcher("/chooseimg.jsp");
	    try {
			dispatch.forward(request, response);
		} catch (ServletException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
