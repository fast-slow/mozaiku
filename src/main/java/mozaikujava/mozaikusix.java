package mozaikujava;
import static mozaikujava.ImageUtility.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;


public class mozaikusix {
	public int boxsize = 50;
	public int widthsize = 400;
	public int sixheight = (int) ((widthsize-(boxsize*0.5))/(boxsize*1.5));
	public int sixwidth = (int) ((widthsize-(boxsize*Math.sqrt(0.75)))/(boxsize*Math.sqrt(3)));
	public int wholesize = 0;
	public int[] size;
	Path path = Paths.get("");
	//static String currentpath = path.toAbsolutePath().toString();
	String currentpath = this.path.toAbsolutePath().toString();
	String resultpath;
	BufferedImage startimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	BufferedImage changeimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	BufferedImage goalimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	BufferedImage completeimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	BufferedImage compareimg = new BufferedImage(widthsize*4, widthsize, BufferedImage.TYPE_INT_RGB);
	int[][][] startimgi = new int[widthsize][widthsize][3];
	int[][][] changeimgi = new int[widthsize][widthsize][3];
	int[][][] goalimgi = new int[widthsize][widthsize][3];
	int[][][] completeimgi = new int[widthsize][widthsize][3];
	int[][][] startcanny;
	int[][][] goalcanny;
	float[][][] centerposition = new float[sixwidth][sixheight][2];
	
	static ArrayList<ArrayList<Integer[]>> cutposition = new ArrayList<>();
	
	
	public static void main(String[] args) {
		mozaikusix mo = new mozaikusix();
		mo.mozaikumain();
	}
	
	public void reset() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
		String preresultpath = this.currentpath+"/result"+sdf.format(date);
		File dir = new File(this.currentpath);
		ArrayList<String> refiles = new ArrayList<String>();
	    File[] files = dir.listFiles();
	    int count = 1;
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
				if(refiles.get(i).contains(this.currentpath+"/result")) {
					fileClass(new File(refiles.get(i)));
				}
			}
			
		}
		Path p1 = Paths.get(resultpath);
		Path p2 = Paths.get(resultpath+"/result_1");
		Path p3 = Paths.get(resultpath+"/result_2");
		Path p4 = Paths.get(resultpath+"/result_mv");
		Path p5 = Paths.get(resultpath+"/result_parts");
		Path p6 = Paths.get(resultpath+"/result_parts2");
		Path p7 = Paths.get(resultpath+"/trim");
		Path p8 = Paths.get(resultpath+"/trim_target");
		Path p9 = Paths.get(resultpath+"/trim2");
		System.out.println(resultpath);
		try {
			fileClass(new File(p1.toString()));
			Files.createDirectory(p1);
			Files.createDirectory(p2);
			Files.createDirectory(p3);
			Files.createDirectory(p4);
			Files.createDirectory(p5);
			Files.createDirectory(p6);
			Files.createDirectory(p7);
			Files.createDirectory(p8);
			Files.createDirectory(p9);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	public static void reset2() {
		
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
	
	public void register(String startimgpath,String changeimgpath,String goalimgpath) {
		try {
			BufferedImage prestartimg = ImageIO.read(new File(startimgpath));
			BufferedImage prechangeimg = ImageIO.read(new File(changeimgpath));
			BufferedImage pregoalimg = ImageIO.read(new File(goalimgpath));

			startimg.createGraphics().drawImage(prestartimg.getScaledInstance(widthsize, widthsize, Image.SCALE_AREA_AVERAGING),0, 0, widthsize, widthsize, null);
			changeimg.createGraphics().drawImage(prechangeimg.getScaledInstance(widthsize, widthsize, Image.SCALE_AREA_AVERAGING),0, 0, widthsize, widthsize, null);
			goalimg.createGraphics().drawImage(pregoalimg.getScaledInstance(widthsize, widthsize, Image.SCALE_AREA_AVERAGING),0, 0, widthsize, widthsize, null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//画像をintの配列情報に
		for(int i = 0;i<widthsize*widthsize;i++) {
			int w = i/widthsize;
			int h = i%widthsize;
			int s = startimg.getRGB(w,h);
			int c = changeimg.getRGB(w,h);
			int g = goalimg.getRGB(w,h);
			startimgi[w][h][0] = r(s);startimgi[w][h][1] = g(s);startimgi[w][h][2] = b(s);
			changeimgi[w][h][0] = r(c);changeimgi[w][h][1] = g(c);changeimgi[w][h][2] = b(c);
			goalimgi[w][h][0] = r(g);goalimgi[w][h][1] = g(g);goalimgi[w][h][2] = b(g);
		}
		
		//canny画像の生成
		startcanny = hist.tocanny(startimgi);
		goalcanny = hist.tocanny(goalimgi);
		BufferedImage startcannyimg = new BufferedImage(startcanny.length, startcanny.length, BufferedImage.TYPE_INT_RGB);
		BufferedImage goalcannyimg = new BufferedImage(startcanny.length, startcanny.length, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i<startcanny.length*startcanny.length;i++) {
			int w = i/startcanny.length;
			int h = i%startcanny.length;
			startcannyimg.setRGB(w,h,rgb(startcanny[w][h][0],startcanny[w][h][1],startcanny[w][h][2]));
			goalcannyimg.setRGB(w,h,rgb(goalcanny[w][h][0],goalcanny[w][h][1],goalcanny[w][h][2]));
		}
		try {
			ImageIO.write(startcannyimg, "jpg", new File(resultpath+"/startcanny.jpg"));
			ImageIO.write(goalcannyimg, "jpg", new File(resultpath+"/goalcanny.jpg"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	
	
	public void positionregister() {
		for(int i=0; i<sixheight*sixwidth; i++) {
			int w = (int) (i/sixheight);
			int h = (int) (i%sixheight);
			if(h%2==1) {
				centerposition[w][h][0] = (float) (boxsize*((w*Math.sqrt(3))+Math.sqrt(0.75)));
			}else {
				centerposition[w][h][0] = (float) (boxsize*((w*Math.sqrt(3))+Math.sqrt(3)));
			}
			centerposition[w][h][1] = (float) (boxsize*((h*1.5)+1));
		}
	}
	
	public ArrayList<Integer[]> compareintsix() {
		ArrayList<Integer[]> Data = new ArrayList<>();
		histsix hists = new histsix();
		for(int i=0; i<sixheight*sixwidth; i++) {
			int iw = i / sixheight;
			int ih = i % sixheight;
			for(int j=0;j<sixheight*sixwidth;j++) {
				int jw = j / sixheight;
				int jh = i% sixheight;
				//System.out.println( "比較中");
				float[] startcenterpositon = {centerposition[jw][jh][0],centerposition[jw][jh][1]};
				float[] goalcenterpositon = {centerposition[iw][ih][0],centerposition[iw][ih][1]};
				for(int k=0;k<6;k++) {
					//System.out.println("個別比較中");
					float result = hists.hist(startcenterpositon,goalcenterpositon,startimgi,changeimgi,goalimgi,startcanny,goalcanny,k*60);
					Integer[] data = {(int) result,i,j,k*60};
					//System.out.println(result);
					//類似度，目標画像のパーツ番号，元画像のパーツ番号，角度
					Data.add(data);
				}
			}
		}
		
		
		return Data;
	}
	 
	public void drawsix() {
		positionregister();
		float[][][] completeimgf = new float[widthsize][widthsize][3];
		int count = 0;
		ArrayList<Integer[]> data = new ArrayList<>();
		data = compareintsix();
		System.out.println("比較完了");
		ArrayList<Integer[]> d = new ArrayList<>();
		ArrayList<Integer> used = new ArrayList<>();
		Integer[] a= {0,0,0,0};
		int part = 0;//着目している完成目標画像のパーツ番号
		used.add(0);
		size = new int[boxsize*2];
		for(int i=0; i<boxsize*2;i++) {
			if(i<boxsize/2) {
				size[i]=(int) ((0.5+i)*Math.sqrt(12));
			}else if(i<=boxsize*1.5) {
				size[i]=(int)(boxsize*Math.sqrt(3));
			}else if(i<boxsize*2) {
				size[i]=(int)((0.5+boxsize*2-i-1)*Math.sqrt(12));
			}
		}
		wholesize =0;
		for(int i=0;i<size.length;i++) {
			wholesize += size[i];
		}
		while (used.size() <= sixheight*sixwidth) {
			//System.out.println(used.toString());
			d = pickupandsort(data,part);
			int p = 0;
			if(used.size()<sixheight*sixwidth) {
				int useflag = 1;
				while(useflag == 1) {
					if(used.contains(d.get(p)[2])||d.get(p)[2]==0) {
						//System.out.println("使用済み");
						p++;
						useflag = 1;
					}else {
						//System.out.println(p);
						useflag = 0;
					}
				}
				a = d.get(p);
			}else {
				a[0]=100;a[1]=part;a[2]=0;a[3]=0;
				
			}
			part = a[2];
			//System.out.println(a[2]+"→"+a[1]+"度数"+a[3]);
			used.add(a[2]);
			count++;
			int spartw = a[2]%sixwidth;
			int sparth = a[2]/sixwidth;
			int cpartw = a[1]%sixwidth;
			int cparth = a[1]/sixwidth;
			int rotate = a[3];
			
			float[] center = {centerposition[spartw][sparth][0],centerposition[spartw][sparth][1]};
			//System.out.println(center[0]);
			int[][] fitimg = fitting(center,startimgi,rotate);
			
			for(int i = 0;i<fitimg.length*3;i++) {
				float h=0;
				float w=0;
				int num = i%fitimg.length;
				int c = i/fitimg.length;
				int lengthcount=0;
				for(int j = 0; j<size.length; j++) {
					lengthcount+=size[j];
					if(lengthcount>=num) {
						h = j - boxsize;
						w = -(size[j]/2)+(num-(lengthcount - size[j]));
						break;
					}
				}
				//System.out.println(h);
				float[] numposition = {center[0]+w,center[1]+h};
				//System.out.println("x="+numposition[0]+"y＝"+numposition[1]);
				float[] leftup=new float[2];
				int[] vertogether = new int[2];
				//System.out.println("x="+numposition[0]);System.out.println("y="+numposition[1]);
				leftup[0] = (float) (numposition[0]-0.5); leftup[1] = (float) (numposition[1]-0.5);
				//System.out.println("x="+leftup[0]+"y＝"+leftup[1]);
				vertogether[0]=(int) Math.ceil(leftup[0]);
				vertogether[1]=(int) Math.ceil(leftup[1]);
				//System.out.println("いけた");
				if(leftup[0]<=0||leftup[0]>=widthsize-1||leftup[1]<=0||leftup[1]>=widthsize-1) {
					//System.out.println(leftup[0]);
				}else {
					//System.out.println(leftup[0]);
					//System.out.println("x="+vertogether[0]+"y＝"+vertogether[1]);
					completeimgf[vertogether[0]-1][vertogether[1]-1][c]+=(fitimg[num][c])*(vertogether[0]-leftup[0])*(vertogether[1]-leftup[1]);
					completeimgf[vertogether[0]-1][vertogether[1]][c]+=(fitimg[num][c])*(vertogether[0]-leftup[0])*(leftup[1]+1-vertogether[1]);
					completeimgf[vertogether[0]][vertogether[1]-1][c]+=(fitimg[num][c])*(leftup[0]+1-vertogether[0])*(vertogether[1]-leftup[1]);
					completeimgf[vertogether[0]][vertogether[1]][c]+=(fitimg[num][c])*(leftup[0]+1-vertogether[0])*(leftup[1]+1-vertogether[1]);
				}
			}
			
			//System.out.println( "配置できたよ");
		}
		for(int i=0; i<widthsize*widthsize*3;i++) {
			int w = (i/widthsize)%widthsize;
			int h = i%widthsize;
			int c = i/(widthsize*widthsize);
			completeimgi[w][h][c] = (int) completeimgf[w][h][c];
		}
		System.out.println( completeimgi.length);
		for(int i =0; i<completeimgi.length*completeimgi.length;i++) {
			int w = i/completeimgi.length;
			int h = i%completeimgi.length;
			completeimg.setRGB(w, h, rgb(completeimgi[w][h][0],completeimgi[w][h][1],completeimgi[w][h][2]));
		}
		try {
			ImageIO.write(completeimg, "jpg", new File(resultpath+"/completeimg.jpg"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		int[][][] compareimgi = new int[completeimgi.length*4][completeimgi.length][3]; 
		for(int i =0; i<completeimgi.length*completeimgi.length*3;i++) {
			int w = (i/completeimgi.length)%completeimgi.length;
			int h = i%completeimgi.length;
			int c = i/(completeimgi.length*completeimgi.length);
			compareimgi[w][h][c]=startimgi[w][h][c];
			compareimgi[completeimgi.length+w][h][c]=changeimgi[w][h][c];
			compareimgi[completeimgi.length*2+w][h][c]=goalimgi[w][h][c];
			compareimgi[completeimgi.length*3+w][h][c]=completeimgi[w][h][c];
		}
		for(int i =0; i<completeimgi.length*completeimgi.length*4;i++) {
			int w = (i/completeimgi.length)%(completeimgi.length*4);
			int h = i%completeimgi.length;
			compareimg.setRGB(w, h, rgb(compareimgi[w][h][0],compareimgi[w][h][1],compareimgi[w][h][2]));
			
		}
		try {
			ImageIO.write(compareimg, "jpg", new File(resultpath+"/compareimg.jpg"));
			//System.out.println(currentpath+"/../result/compareimg.jpg");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
		
	public int[][] fitting(float[] centerposition, int[][][] img ,int rotate) {
		float[] position=new float[2];
		float[] leftup=new float[2];
		float[] leftdown=new float[2];
		float[] rightup=new float[2];
		float[] rightdown=new float[2];
		float[] leftupcolor = new float[3];
		float[] leftdowncolor = new float[3];
		float[] rightupcolor = new float[3];
		float[] rightdowncolor = new float[3];
		int[] vertogether = new int[2];
		float [] positioncolor = new float[3];
		int[][] fittingimg = new int[wholesize][3];
		int count=0;
		for(int i=0; i<size.length;i++) {
			for(int j=0; j<size[i];j++) {
				float wholewidth = size[i];
				float height = (float) (boxsize-0.5-i);
				float width = (float) (j-(wholewidth/2)+0.5);
				float rasirotate = (float) Math.toRadians(rotate);
				position[0]=centerposition[0] + (float) ((height*Math.sin(rasirotate))+(width*Math.cos(rasirotate)));
				position[1] =centerposition[1]+ (float) ((height*Math.cos(rasirotate))+(width*Math.sin(rasirotate)));
				leftup[0] = (float) (position[0]-0.5); leftup[1] = (float) (position[0]-0.5);
				leftdown[0] = (float) (position[0]-0.5); leftdown[1] = (float) (position[0]+0.5);
				rightup[0] = (float) (position[0]+0.5); rightup[1] = (float) (position[0]-0.5);
				rightdown[0] = (float) (position[0]+0.5); leftdown[1] = (float) (position[0]+0.5);
				//中身にある頂点の集まるところ.
				vertogether[0]=(int) Math.ceil(leftup[0]);
				vertogether[1]=(int) Math.ceil(leftup[1]);
				//色の計算
				
				for(int k=0;k<3;k++) {
					if((vertogether[0]<=0)|| (vertogether[1]<=0)||(vertogether[0]>=img.length)||(vertogether[1]>=img.length)) {
						//positioncolor[k]=(float)(img[vertogether[0]][vertogether[1]][k]);
					}else {
						leftupcolor[k] = img[vertogether[0]-1][vertogether[1]-1][k]*((vertogether[0]-leftup[0])*(vertogether[1]-leftup[1])) ;
						leftdowncolor[k] = img[vertogether[0]-1][vertogether[1]][k]*((vertogether[0]-leftdown[0])*(leftdown[1]-vertogether[1]));
						rightupcolor[k] = img[vertogether[0]][vertogether[1]-1][k]*((rightup[0]-vertogether[0])*(vertogether[1]-rightup[1]));
						rightdowncolor[k] = img[vertogether[0]][vertogether[1]][k]*((rightdown[0]-vertogether[0])*(rightdown[1]-vertogether[1]));
						positioncolor[k] = leftupcolor[k]+leftdowncolor[k]+rightupcolor[k]+rightdowncolor[k];
						System.out.println(positioncolor[k]);
					}
					fittingimg[count][k] = (int) positioncolor[k];
				}
				
				count++;
			}
		}
		return fittingimg;
	}
	
	

	public int[][][] matchimg(int[][] fitimg,int[][] completeimg,float[] centerposition){
		
		return changeimgi;
		
	}
	
	
	
	
	public ArrayList<Integer[]> pickupandsort(ArrayList<Integer[]> data,int pickupparts){
		ArrayList<Integer[]> pickdata = new ArrayList<>();
		ArrayList<Integer[]> sortdata = new ArrayList<>();
		for(int i=0;i<data.size();i++) {
			if(data.get(i)[1]==pickupparts) {
				pickdata.add(data.get(i));
			}
		}
		//sort
		sortdata = quicksort(pickdata);
		for(int i=0; i<sortdata.size(); i++) {
			//System.out.println(sortdata.get(i)[0]+","+sortdata.get(i)[1]+","+sortdata.get(i)[2]+","+sortdata.get(i)[3]);
		}
		return sortdata;
		
	}
	public ArrayList<Integer[]> quicksort(ArrayList<Integer[]> data){
		ArrayList<Integer[]> ldata=new ArrayList<>();
		ArrayList<Integer[]> rdata=new ArrayList<>();
		ArrayList<Integer[]> quickdata=new ArrayList<>();
		if(data.size()>1) {
			for(int i=1; i<data.size();i++) {
				if(data.get(0)[0]<data.get(i)[0]) {
					ldata.add(data.get(i));
				}
				else {
					rdata.add(data.get(i));
				}
			}
			if(ldata.size()>1) {
				ldata = quicksort(ldata);
			}if(rdata.size()>1) {
				rdata = quicksort(rdata);
			}
		}
		for(int i=0;i<ldata.size();i++) {
			quickdata.add(ldata.get(i));
		}
		quickdata.add(data.get(0));
		for(int i=0;i<rdata.size();i++) {
			quickdata.add(rdata.get(i));
		}
		return quickdata;
	}
	
	Object excecution(String startimgpaths,String goalimgpaths,String savefile) {
		long startTime = System.currentTimeMillis();
		reset();
		String startimgpath = startimgpaths;
		String goalimgpath = goalimgpaths;
		String changeimgpath = resultpath+"/changeimg.jpg";
		new imgchange(startimgpath,goalimgpath,resultpath);
		register(startimgpath,changeimgpath,goalimgpath);
		drawsix();
		
		long endTime = System.currentTimeMillis();
		System.out.println("処理時間：" + (endTime - startTime) + " ms");
		return compareimg;
	}
	
	
	void mozaikumain() {
		long startTime = System.currentTimeMillis();
		reset();
		String startimgpath = currentpath+"/../image/hosizukiyo.jpg";
		String goalimgpath = currentpath+"/../image/pikaso.jpg";
		String changeimgpath = resultpath+"/changeimg.jpg";
		new imgchange(startimgpath,goalimgpath,resultpath);
		register(startimgpath,changeimgpath,goalimgpath);
		drawsix();
		long endTime = System.currentTimeMillis();
		System.out.println("処理時間：" + (endTime - startTime) + " ms");
	}
	
	
}
