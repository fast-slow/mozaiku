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
	javabeans javabeans = new javabeans();
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
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().append("Served at: ").append(request.getContextPath());
		HttpSession session = request.getSession();
		String action ;
		try {
			javabeans = (model.javabeans) session.getAttribute("javabeans");
			action = javabeans.getaction();
		}catch(Exception e) {
			action = "";
		}
		System.out.println(action);
		if (action == "") {
			System.out.println("test");
			start(request,response);
		}else if(action == "caliculate") {
			try {
				caliculate(request,response);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}else {
			javabeans.setaction("");
			session.setAttribute("javabeans", javabeans);
			start(request,response);
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	void start(HttpServletRequest request, HttpServletResponse response) {
		String p1 = this.getServletContext().getRealPath("/");
		String realPath = p1 + "../../../../../../";
		String imgpath = realPath+"image/";
		File dir = new File(imgpath);
		File[] list = dir.listFiles();
		ArrayList<String> imglistname = new ArrayList<String>();
		for(int i=0; i<list.length; i++) {
			  imglistname.add(list[i].getName());     //ファイル名のみ
		    }
		imglistname.remove(imglistname.indexOf(".DS_Store"));
		javabeans javabeans = new javabeans();
		javabeans.setimgpath(imglistname);
		javabeans.setapppath(realPath);
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
	
	void caliculate(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
		String startimg = request.getParameter("startimg");
		String goalimg = request.getParameter("goalimg");
		HttpSession session = request.getSession();
		javabeans = (model.javabeans) session.getAttribute("javabeans");
		String pythonpath = javabeans.getapppath()+"main.py";
		String startpath = javabeans.getapppath()+"image/"+startimg;
		String goalpath = javabeans.getapppath()+"image/"+goalimg;
		String resultpath =getServletContext().getRealPath("/result")+"/compare.jpg";
		BufferedImage compareimg =  (BufferedImage) servletmain.mozaiku(startpath,goalpath,resultpath);
		ImageIO.write(compareimg, "jpg", new File(resultpath));
		javabeans.setresultpath(resultpath);
		session.setAttribute("javabeans", javabeans);
		RequestDispatcher dispatch = request.getRequestDispatcher("/result.jsp");
	    try {
	    	System.out.println("test2");
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
