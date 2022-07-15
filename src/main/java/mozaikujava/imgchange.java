package mozaikujava;

import static mozaikujava.ImageUtility.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class imgchange {
	public static int size = 400;
	public static int border = 30;
	public imgchange(String startimgpath,String goalimgpath) {
		changedeviation(startimgpath,goalimgpath);
	}
	
	public static void main(String[] args) {
		Path path = Paths.get("");
		String currentpath = path.toAbsolutePath().toString();
		String startimgpath = currentpath+"/image/hosizukiyo.jpg";
		String goalimgpath = currentpath+"/image/pikaso.jpg";
		changedeviation(startimgpath,goalimgpath);
	}
	public static void changedeviation(String startimgpath,String goalimgpath) {
		Path path = Paths.get("");
		String currentpath = path.toAbsolutePath().toString();
		BufferedImage startimg = null;
		BufferedImage goalimg = null;
		ArrayList<int[]> sin=new ArrayList<>();
		ArrayList<int[]> gin=new ArrayList<>();
		float[] save;
		float[] gave;
		try {
			BufferedImage sizeimgs = ImageIO.read(new File(startimgpath));
			BufferedImage sizeimgg = ImageIO.read(new File(goalimgpath));
			startimg = new BufferedImage(size,size, BufferedImage.TYPE_3BYTE_BGR);
			goalimg = new BufferedImage(size,size, BufferedImage.TYPE_3BYTE_BGR);
			startimg.createGraphics().drawImage(sizeimgs.getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING),0, 0, size, size, null);
			goalimg.createGraphics().drawImage(sizeimgg.getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING),0, 0, size, size, null);
			ImageIO.write(startimg, "jpg", new File(currentpath+"/result/startimg.jpg"));
			ImageIO.write(goalimg, "jpg", new File(currentpath+"/result/goalimg.jpg"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//平均を求める
		int[] sums = {0,0,0};
		int[] sumg = {0,0,0};
		int count =0;
		for(int i = 0; i<size*size ; i++) {
			int h = (int)(i/size);
			int w = i%size;
			int sc = startimg.getRGB(h, w);
			int[] srgb = {r(sc),g(sc),b(sc)};
			if(!((srgb[0]<border && srgb[1]<border &&srgb[2]<border)||(srgb[0]>255-border&&srgb[1]>255-border&&srgb[2]>255-border))) {
				//System.out.println(sin.size());
				sin.add(new int[] {h,w});
				sums = new int[]{sums[0]+srgb[0],sums[1]+srgb[1],sums[2]+srgb[2]};
			}else {	
			}
			int gc = goalimg.getRGB(h, w);
			int[] grgb = {r(gc),g(gc),b(gc)};
			if(!((grgb[0]<border&&grgb[1]<border&&grgb[2]<border)||(grgb[0]>255-border&&grgb[1]>255-border&&grgb[2]>255-border))) {
				gin.add(new int[] {h,w});
				sumg = new int[]{sumg[0]+grgb[0],sumg[1]+grgb[1],sumg[2]+grgb[2]};
			}else {
			}
		}
		save = new float[]{sums[0]/sin.size(),sums[1]/sin.size(),sums[2]/sin.size()};
		gave = new float[]{sumg[0]/gin.size(),sumg[1]/gin.size(),sumg[2]/gin.size()};
		//標準偏差を求める
		float[] sdevisum = {0,0,0};
		float[] gdevisum = {0,0,0};
		for(int i = 0; i< sin.size();i++) {
			int h = sin.get(i)[0];
			int w = sin.get(i)[1];
			int sc = startimg.getRGB(h, w);
			int[] srgb = {r(sc),g(sc),b(sc)};
			sdevisum = new float[] {(int) (sdevisum[0]+Math.pow(srgb[0]-save[0],2)),(int) (sdevisum[1]+Math.pow(srgb[1]-save[1],2)),(int) (sdevisum[2]+Math.pow(srgb[2]-save[2],2))};
		}
		for(int i = 0; i< gin.size();i++) {
			int h = gin.get(i)[0];
			int w = gin.get(i)[1];
			//System.out.println();
			int sc = goalimg.getRGB(h, w);
			int[] grgb = {r(sc),g(sc),b(sc)};
			gdevisum = new float[] {(float) (gdevisum[0]+Math.pow(grgb[0]-gave[0],2)),(float) (gdevisum[1]+Math.pow(grgb[1]-gave[1],2)),(float) (gdevisum[2]+Math.pow(grgb[2]-gave[2],2))};
		}
		float[] sdevi = new float[] {(float) Math.sqrt(sdevisum[0]/sin.size()),(float) Math.sqrt(sdevisum[1]/sin.size()),(float) Math.sqrt(sdevisum[2]/sin.size())};
		float[] gdevi = new float[] {(float) Math.sqrt(gdevisum[0]/gin.size()),(float) Math.sqrt(gdevisum[1]/gin.size()),(float) Math.sqrt(gdevisum[2]/gin.size())};
		//画像を変更していく
		for(int i = 0; i<sin.size() ; i++) {
			int h = sin.get(i)[0];
			int w = sin.get(i)[1];
			int sc = startimg.getRGB(h, w);
			int[] srgb = {r(sc),g(sc),b(sc)};
			int[] setrgb = {0,0,0};
			for (int j=0;j<3;j++) {
				setrgb[j] = (int) (gave[j]+(gdevi[j]*((srgb[j]-save[j])/sdevi[j])));
				if (setrgb[j]>255) {
					setrgb[j]=255;
				}else if(setrgb[j]<0) {
					setrgb[j]=0;
				}
			}
			int rgb = rgb(setrgb[0],setrgb[1],setrgb[2]);
			startimg.setRGB(h,w,rgb);
		}
		try {
			ImageIO.write(startimg, "jpg", new File(currentpath+"/result/changeimg.jpg"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println(sin.size());
		System.out.println(gin.size());
		System.out.println(Arrays.toString(save));
		System.out.println(Arrays.toString(gave));
		System.out.println(Arrays.toString(sdevi));
		System.out.println(Arrays.toString(gdevi));
	}
}
