package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
		String imginpath = javabeans.getimginpath();
		String resultpath;
		//結果を格納するフォルダの作成及び古いフォルダの削除
		String currentpath = request.getContextPath();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
		String preresultpath = getServletContext().getRealPath("/result/")+sdf.format(date);
		ArrayList<String> refiles = new ArrayList<String>();
		File dir = new File( getServletContext().getRealPath("/result"));
	    File[] files = dir.listFiles();
	    int count = 1;
	    System.out.println(currentpath);
	    for (int i = 0; i < files.length; i++) {
	        File file = files[i];
	        refiles.add(file.toString());
	    }
	    while(true) {
	    	if(refiles.contains(preresultpath+String.valueOf(count))) {
	    		count++;
	    	}else {
	    		resultpath = preresultpath+String.valueOf(count);
	    		break;
	    	}
	    }
		if(count == 1) {
			for(int i = 0; i < refiles.size(); i++) {
				if(refiles.get(i).contains(getServletContext().getRealPath("/result"))) {
					if(!refiles.get(i).contains("jsp")) {
						if(!refiles.get(i).contains("java")) {
							fileClass(new File(refiles.get(i)));
						}
					}
				}
			}
		}
		Files.createDirectory(Paths.get(resultpath));
		servletmain servmain = new servletmain();
		BufferedImage compareimg = null;
		try {
			compareimg = (BufferedImage) servmain.mozaiku(startpath,goalpath,resultpath);
		} catch (Exception e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		ImageIO.write(compareimg, "jpg", new File(resultpath+"/compare.jpg"));
		javabeans.setresultpath(resultpath);
		javabeans.setstartimgpath(startpath);
		javabeans.setgoalimgpath(startpath);
		javabeans.setresultfoldername("result/"+sdf.format(date)+String.valueOf(count));
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
	public static void fileClass(File TestFile) {
        if (TestFile.exists()) {
            //ファイル存在チェック
            if (TestFile.isFile()) {
                //存在したら削除する
                if (TestFile.delete()) {
                }
            //対象がディレクトリの場合
            } else if(TestFile.isDirectory()) {
                //ディレクトリ内の一覧を取得
                File[] files = TestFile.listFiles();
                //存在するファイル数分ループして再帰的に削除
                for(int i=0; i<files.length; i++) {
                    fileClass(files[i]);
                }
                //ディレクトリを削除する
                if (TestFile.delete()) {
                }else{
                }
            }
        } else {
        }
    }

}
