package mozaikujava;
import static mozaikujava.ImageUtility.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class mozaiku {
	public static int box = 10;
	public static int widthsize = 400;
	static Path path = Paths.get("");
	static String currentpath = path.toAbsolutePath().toString();
	static BufferedImage startimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	static BufferedImage changeimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	static BufferedImage goalimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	static BufferedImage completeimg = new BufferedImage(widthsize, widthsize, BufferedImage.TYPE_INT_RGB);
	static BufferedImage compareimg = new BufferedImage(widthsize*4, widthsize, BufferedImage.TYPE_INT_RGB);
	static int[][][] startimgi = new int[widthsize][widthsize][3];
	static int[][][] changeimgi = new int[widthsize][widthsize][3];
	static int[][][] goalimgi = new int[widthsize][widthsize][3];
	static int[][][] completeimgi = new int[widthsize][widthsize][3];
	static int[][][] startcanny;
	static int[][][] goalcanny;
	static ArrayList<ArrayList<Integer[]>> cutposition = new ArrayList<>();
	
	
	public static void main(String[] args) {
		mozaikumain();
		
	}
	
	public static void reset() {
		Path p1 = Paths.get(currentpath+"/../result");
		Path p2 = Paths.get(currentpath+"/../result/result_1");
		Path p3 = Paths.get(currentpath+"/../result/result_2");
		Path p4 = Paths.get(currentpath+"/../result/result_mv");
		Path p5 = Paths.get(currentpath+"/../result/result_parts");
		Path p6 = Paths.get(currentpath+"/../result/result_parts2");
		Path p7 = Paths.get(currentpath+"/../result/trim");
		Path p8 = Paths.get(currentpath+"/../result/trim_target");
		Path p9 = Paths.get(currentpath+"/../result/trim2");
		
		
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
			// TODO ????????????????????? catch ????????????
			e.printStackTrace();
		}
	}
	
	public static void reset2() {
		
	}
	
	 public static void fileClass(File TestFile) {
	        if (TestFile.exists()) {
	            //??????????????????????????????
	            if (TestFile.isFile()) {
	                //???????????????????????????
	                if (TestFile.delete()) {
	                }
	            //????????????????????????????????????
	            } else if(TestFile.isDirectory()) {
	                //???????????????????????????????????????
	                File[] files = TestFile.listFiles();
	                //???????????????????????????????????????????????????????????????
	                for(int i=0; i<files.length; i++) {
	                    fileClass(files[i]);
	                }
	                //?????????????????????????????????
	                if (TestFile.delete()) {
	                }else{
	                }
	            }
	        } else {
	        }
	    }
	
	public static void register(String startimgpath,String changeimgpath,String goalimgpath) {
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
		//?????????int??????????????????
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
		
		//canny???????????????
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
			ImageIO.write(startcannyimg, "jpg", new File(currentpath+"/../result/startcanny.jpg"));
			ImageIO.write(goalcannyimg, "jpg", new File(currentpath+"/../result/goalcanny.jpg"));
		} catch (IOException e) {
			// TODO ????????????????????? catch ????????????
			e.printStackTrace();
		}
	}
	
	public static void impoint(BufferedImage img) {
		int cutwidth = widthsize/box;
		for(int i = 0; i < box*box; i++) {
			int boxh = i/box;
			int boxw = i%box;
			ArrayList<Integer[]> p = new ArrayList<>();
			Integer[] p1 = {boxw*cutwidth,boxh*cutwidth};
			Integer[] p2 = {(boxw+1)*cutwidth,boxh*cutwidth};
			Integer[] p3 = {boxw*cutwidth,(boxh+1)*cutwidth};
			Integer[] p4 = {(boxw+1)*cutwidth,(boxh+1)*cutwidth};
			p.add(p1); p.add(p2); p.add(p3); p.add(p4);
			cutposition.add(p);
			trim(startimgi,cutwidth,i);
		}
	}
	
	public static void trim(BufferedImage img,int boxsize, int i) {
		BufferedImage write = new BufferedImage(boxsize,boxsize, BufferedImage.TYPE_INT_RGB);
		for (int j=0; j<boxsize*boxsize; j++) {
			int boxh = i/box;
			int boxw = i%box;
			int w = j%boxsize;
			int h = j/boxsize;
			int rgb = img.getRGB(boxw*boxsize+w, boxh*boxsize+h);
			write.setRGB(w,h,rgb);
		}
		try {
			ImageIO.write(write, "jpg", new File(currentpath+"/../result/trim/"+Integer.valueOf(i).toString()+".jpg"));
		} catch (IOException e) {
			// TODO ????????????????????? catch ????????????
			e.printStackTrace();
		}
	}
	public static void trim(int[][][] imgi,int boxsize, int i) {
		int wimgi[][][] = new int[boxsize][boxsize][3];
		BufferedImage write = new BufferedImage(boxsize,boxsize, BufferedImage.TYPE_INT_RGB);
		for (int j=0; j<boxsize*boxsize*3; j++) {
			int boxh = i/box;
			int boxw = i%box;
			int w = j%boxsize;
			int h = (j/boxsize)%boxsize;
			int c = j/(boxsize*boxsize);
			wimgi[w][h][c] = imgi[boxw*boxsize+w][boxh*boxsize+h][c];
		}
		for(int j=0; j<boxsize*boxsize; j++) {
			int w = j%boxsize;
			int h = j/boxsize;
			write.setRGB(w, h, rgb(wimgi[w][h][0],wimgi[w][h][1],wimgi[w][h][2]));
		}
		try {
			ImageIO.write(write, "jpg", new File(currentpath+"/../result/trim/"+Integer.valueOf(i).toString()+".jpg"));
		} catch (IOException e) {
			// TODO ????????????????????? catch ????????????
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Integer[]> compare() {
		int boxw = widthsize/box;
		ArrayList<Integer[]> Data =  new ArrayList<>();
		for(int i=0;i<box*box;i++) {
			int iw = i/box;int ih = i%box;
			BufferedImage goalcut = new BufferedImage(boxw, boxw, BufferedImage.TYPE_INT_RGB);
			for(int k=0; k<boxw*boxw; k++) {
				int kw = k/boxw;int kh = k%boxw;
				goalcut.setRGB(kw,kh ,goalimg.getRGB(iw*box+kw, ih*box+kh) );
			}
			for(int j =0;j<box*box;j++) {
				ArrayList<Integer[]> data2= new ArrayList<>();
				int jw = j/boxw;int jh = j%boxw;
				BufferedImage startcut = new BufferedImage(boxw, boxw, BufferedImage.TYPE_INT_RGB);
				BufferedImage changecut = new BufferedImage(boxw, boxw, BufferedImage.TYPE_INT_RGB);
				for(int k=0; k<boxw*boxw; k++) {
					int kw = k/boxw;int kh = k%boxw;
					startcut.setRGB(kw,kh ,startimg.getRGB(jw*box+kw, jh*box+kh));
					changecut.setRGB(kw, kh,changeimg.getRGB(jw*box+kw, jh*box+kh));
				}//???????????????
				float result0 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data0 = {(int) result0,i,j,0};
				Data.add(data0);
				for(int k=0; k<boxw*boxw; k++) {
					int kw = k/boxw;int kh = k%boxw;
					startcut.setRGB(boxw-kh-1,kw ,startimg.getRGB(jw*box+kw, jh*box+kh));
					changecut.setRGB(boxw-kh-1, kw,changeimg.getRGB(jw*box+kw, jh*box+kh));
				}//90?????????
				float result90 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data90 = {(int) result90,i,j,0};
				Data.add(data90);
				for(int k=0; k<boxw*boxw; k++) {
					int kw = k/boxw;int kh = k%boxw;
					startcut.setRGB(boxw-kw-1,boxw-kh-1,startimg.getRGB(jw*box+kw, jh*box+kh));
					changecut.setRGB(boxw-kw-1,boxw-kh-1,changeimg.getRGB(jw*box+kw, jh*box+kh));
				}//180?????????
				float result180 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data180 = {(int) result180,i,j,180};
				Data.add(data180);
				for(int k=0; k<boxw*boxw; k++) {
					int kw = k/boxw;int kh = k%boxw;
					startcut.setRGB(kh,boxw-kw-1 ,startimg.getRGB(jw*box+kw, jh*box+kh));
					changecut.setRGB(kh,boxw-kw-1,changeimg.getRGB(jw*box+kw, jh*box+kh));
				}//270?????????
				float result270 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data270 = {(int) result270,i,j,0};
				Data.add(data270);
			}
			
		}
		return Data;
	}
	public static ArrayList<Integer[]> compareint() {
		int boxw = widthsize/box;
		//int cboxew = 
		System.out.println(startcanny.length);
		ArrayList<Integer[]> Data =  new ArrayList<>();
		for(int i=0;i<box*box;i++) {
			int iw = i/box;int ih = i%box;
			int[][][] goalcut = new int[boxw][boxw][3];
			//int[][][] goalcannycut = new int[][][];
			for(int k=0; k<boxw*boxw; k++) {
				int kw = k/boxw;int kh = k%boxw;
				goalcut[kw][kh][0]=goalimgi[iw*boxw+kw][ih*boxw+kh][0];goalcut[kw][kh][1]=goalimgi[iw*boxw+kw][ih*boxw+kh][1];goalcut[kw][kh][2]=goalimgi[iw*boxw+kw][ih*boxw+kh][2];
			}
			for(int j =0;j<box*box;j++) {
				int jw = j/box;int jh = j%box;
				int[][][] startcut = new int[boxw][boxw][3];
				int[][][] changecut = new int[boxw][boxw][3];
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					startcut[kw][kh][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[kw][kh][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
				}//???????????????
				float result0 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data0 = {(int) result0,i,j,0};
				Data.add(data0);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					startcut[boxw-kh-1][kw][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[boxw-kh-1][kw][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
					
				}//90?????????
				float result90 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data90 = {(int) result90,i,j,90};
				Data.add(data90);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					
					startcut[boxw-kw-1][boxw-kh-1][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[boxw-kw-1][boxw-kh-1][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
				}//180?????????
				float result180 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data180 = {(int) result180,i,j,180};
				Data.add(data180);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					
					startcut[kh][boxw-kw-1][kc] = startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[kh][boxw-kw-1][kc] = changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
				}//270?????????
				float result270 =  hist.hist1(startcut, changecut, goalcut);
				Integer[] data270 = {(int) result270,i,j,270};
				//?????????????????????????????????????????????????????????????????????????????????
				Data.add(data270);
			}
			
		}
		return Data;
	}
	 
	public static ArrayList<Integer[]> compareint2() {
		int boxw = widthsize/box;
		//int cboxew = 
		ArrayList<Integer[]> Data =  new ArrayList<>();
		for(int i=0;i<box*box;i++) {
			int iw = i/box;int ih = i%box;
			int[][][] goalcut = new int[boxw][boxw][3];
			int[][][] goalcannycut = new int[boxw][boxw][3];
			//int[][][] goalcannycut = new int[][][];
			for(int k=0; k<boxw*boxw*3; k++) {
				int kw = (k/boxw)%boxw;int kh = k%boxw; int kc = k/(boxw*boxw);
				goalcut[kw][kh][kc]=goalimgi[iw*boxw+kw][ih*boxw+kh][kc];
				goalcannycut[kw][kh][kc] = goalcanny[iw*boxw+kw][ih*boxw+kh][kc];
			}
			for(int j =0;j<box*box;j++) {
				int jw = j/box;int jh = j%box;
				int[][][] startcut = new int[boxw][boxw][3];
				int[][][] changecut = new int[boxw][boxw][3];
				int[][][] startcannycut = new int[boxw][boxw][3];
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					startcut[kw][kh][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[kw][kh][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
					startcannycut[kw][kh][kc]=startcanny[jw*boxw+kw][jh*boxw+kh][kc];
				}//???????????????
				float result0 =  hist.hist1(startcut, changecut, goalcut,startcannycut,goalcannycut);
				Integer[] data0 = {(int) result0,i,j,0};
				Data.add(data0);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					startcut[boxw-kh-1][kw][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[boxw-kh-1][kw][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
					startcannycut[boxw-kh-1][kw][kc]=startcanny[jw*boxw+kw][jh*boxw+kh][kc];
					
				}//90?????????
				float result90 =  hist.hist1(startcut, changecut, goalcut,startcannycut,goalcannycut);
				Integer[] data90 = {(int) result90,i,j,90};
				Data.add(data90);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					
					startcut[boxw-kw-1][boxw-kh-1][kc]=startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[boxw-kw-1][boxw-kh-1][kc]=changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
					startcannycut[boxw-kw-1][boxw-kh-1][kc]=startcanny[jw*boxw+kw][jh*boxw+kh][kc];
				}//180?????????
				float result180 =  hist.hist1(startcut, changecut, goalcut,startcannycut,goalcannycut);
				Integer[] data180 = {(int) result180,i,j,180};
				Data.add(data180);
				for(int k=0; k<boxw*boxw*3; k++) {
					int kw = (k/boxw)%boxw;int kh = k%boxw;int kc = k/boxw/boxw;
					
					startcut[kh][boxw-kw-1][kc] = startimgi[jw*boxw+kw][jh*boxw+kh][kc];
					changecut[kh][boxw-kw-1][kc] = changeimgi[jw*boxw+kw][jh*boxw+kh][kc];
					startcannycut[kh][boxw-kw-1][kc] = startcanny[jw*boxw+kw][jh*boxw+kh][kc];
				}//270?????????
				float result270 =  hist.hist1(startcut, changecut, goalcut,startcannycut,goalcannycut);
				Integer[] data270 = {(int) result270,i,j,270};
				//?????????????????????????????????????????????????????????????????????????????????
				Data.add(data270);
			}
			
		}
		return Data;
	}

	
	public static void draw() {
		int count = 0;
		int boxw = widthsize/box;
		ArrayList<Integer[]> data = new ArrayList<>();
		data = compareint2();
		System.out.println("????????????");
		ArrayList<Integer[]> d = new ArrayList<>();
		ArrayList<Integer> used = new ArrayList<>();
		Integer[] a= {0,0,0,0};
		int part = 0;//??????????????????????????????????????????????????????
		used.add(0);
		while (used.size() <= box*box) {
			d = pickupandsort(data,part);
			int p = 0;
			if(used.size()<box*box) {
				int useflag = 1;
				while(useflag == 1) {
					if(used.contains(d.get(p)[2])||d.get(p)[2]==0) {
						//System.out.println("????????????");
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
			//System.out.println(a[2]+"???"+a[1]+"??????"+a[3]);
			used.add(a[2]);
			count++;
			//System.out.println(count);
			//System.out.println(used);
			int spartw = a[2]/box;
			int sparth = a[2]%box;
			int cpartw = a[1]/box;
			int cparth = a[1]%box;
			if(a[3]==0) {
				for(int i = 0;i<boxw*boxw*3;i++) {
					int w = (i/boxw)%boxw;
					int h = i%boxw;
					int c = i/(boxw*boxw);
					completeimgi[cpartw*boxw+w][cparth*boxw+h][c] = startimgi[spartw*boxw+w][sparth*boxw+h][c];
				}
			}else if(a[3]==90) {
				for(int i = 0;i<boxw*boxw*3;i++) {
					int w = (i/boxw)%boxw;
					int h = i%boxw;
					int c = i/(boxw*boxw);
					completeimgi[cpartw*boxw+boxw-h-1][cparth*boxw+w][c] = startimgi[spartw*boxw+w][sparth*boxw+h][c];
				}
			}else if(a[3]==180) {
				for(int i = 0;i<boxw*boxw*3;i++) {
					int w = (i/boxw)%boxw;
					int h = i%boxw;
					int c = i/(boxw*boxw);
					completeimgi[cpartw*boxw+boxw-w-1][cparth*boxw+boxw-h-1][c] = startimgi[spartw*boxw+w][sparth*boxw+h][c];
				}
			}else if(a[3]==270) {
				for(int i = 0;i<boxw*boxw*3;i++) {
					int w = (i/boxw)%boxw;
					int h = i%boxw;
					int c = i/(boxw*boxw);
					completeimgi[cpartw*boxw+h][cparth*boxw+boxw-w-1][c] = startimgi[spartw*boxw+w][sparth*boxw+h][c];
				}
			}
			//System.out.println( "??????????????????");
		}
		System.out.println( completeimgi.length);
		for(int i =0; i<completeimgi.length*completeimgi.length;i++) {
			int w = i/completeimgi.length;
			int h = i%completeimgi.length;
			completeimg.setRGB(w, h, rgb(completeimgi[w][h][0],completeimgi[w][h][1],completeimgi[w][h][2]));
		}
		try {
			ImageIO.write(completeimg, "jpg", new File(currentpath+"/../result/completeimg.jpg"));
		} catch (IOException e) {
			// TODO ????????????????????? catch ????????????
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
			ImageIO.write(compareimg, "jpg", new File(currentpath+"/../result/compareimg.jpg"));
			//System.out.println(currentpath+"/../result/compareimg.jpg");
		} catch (IOException e) {
			// TODO ????????????????????? catch ????????????
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Integer[]> pickupandsort(ArrayList<Integer[]> data,int pickupparts){
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
	public static ArrayList<Integer[]> quicksort(ArrayList<Integer[]> data){
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
	static  Object excecution(String startimgpaths,String goalimgpaths,String savefile) {
		long startTime = System.currentTimeMillis();
		reset();
		String startimgpath = startimgpaths;
		String goalimgpath = goalimgpaths;
		String changeimgpath = currentpath+"/../result/changeimg.jpg";
		new imgchange(startimgpath,goalimgpath);
		register(startimgpath,changeimgpath,goalimgpath);
		impoint(startimg);
		draw();
		
		long endTime = System.currentTimeMillis();
		System.out.println("???????????????" + (endTime - startTime) + " ms");
		return compareimg;
	}
	
	static void mozaikumain() {
		long startTime = System.currentTimeMillis();
		reset();
		String startimgpath = currentpath+"/../image/hosizukiyo.jpg";
		String goalimgpath = currentpath+"/../image/pikaso.jpg";
		String changeimgpath = currentpath+"/../result/changeimg.jpg";
		new imgchange(startimgpath,goalimgpath);
		register(startimgpath,changeimgpath,goalimgpath);
		impoint(startimg);
		draw();
		long endTime = System.currentTimeMillis();
		System.out.println("???????????????" + (endTime - startTime) + " ms");
	}
	
}
