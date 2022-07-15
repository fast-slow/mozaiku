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
 * Servlet implementation class result
 */
@WebServlet("/result")
public class result extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public result() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			caliculate(request,response);
		} catch (IOException | InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	void caliculate(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
		javabeans javabeans;
		HttpSession session = request.getSession();
		javabeans = (model.javabeans) session.getAttribute("javabeans");
		String startimg = request.getParameter("startimg");
		String goalimg = request.getParameter("goalimg");
		String startpath = javabeans.getimginpath()+"/"+startimg;
		String goalpath = javabeans.getimginpath()+"/"+goalimg;
		//String startpath = "img/"+startimg;
		//String goalpath = "img/"+goalimg;
		String imginpath = javabeans.getimginpath();
		String resultpath =getServletContext().getRealPath("/result")+"/compare.jpg";
		BufferedImage compareimg =  (BufferedImage) servletmain.mozaiku(startpath,goalpath,resultpath);
		ImageIO.write(compareimg, "jpg", new File(resultpath));
		javabeans.setresultpath(resultpath);
		javabeans.setstartimgpath(startpath);
		javabeans.setgoalimgpath(startpath);
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
