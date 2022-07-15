package mozaikujava;
import static mozaikujava.ImageUtility.*;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class hist {
	static Path path = Paths.get("");
	static String currentpath = path.toAbsolutePath().toString();
	public static float hist1(BufferedImage startimg,BufferedImage changeimg,BufferedImage goalimg) {
		int mean1weight=1; int mean2weight=1; int mean3weight=1;
		float mean1 = cannycompare(startimg,goalimg,75,150);
		float mean2 = colordiff(changeimg,goalimg);
		float mean3 = histdiff(changeimg,goalimg);
		float result = (mean1*mean1weight+mean2*mean2weight+mean3*mean3weight)/(mean1weight+mean2weight+mean3weight);
		return result*1000000;
	}
	public static float hist1(int[][][] startimg,int[][][]changeimg,int[][][]goalimg) {
		int mean1weight=2; int mean2weight=1; int mean3weight=1;
		float mean1 = cannycompare(startimg,goalimg,75,150);
		float mean2 = colordiff(changeimg,goalimg);
		float mean3 = histdiff2(changeimg,goalimg);
		float result = (mean1*mean1weight+mean2*mean2weight+mean3*mean3weight)/(mean1weight+mean2weight+mean3weight);
		return result*1000000;
	}
	public static float hist1(int[][][] startimg,int[][][]changeimg,int[][][]goalimg,int[][][] startcanny,int[][][]goalcanny) {
		int mean1weight=1; int mean2weight=10; int mean3weight=1;
		float mean1 = cannydiff(startcanny,goalcanny);
		float mean2 = colordiff(changeimg,goalimg);
		float mean3 = histdiff2(changeimg,goalimg);
		float result = (mean1*mean1weight+mean2*mean2weight+mean3*mean3weight)/(mean1weight+mean2weight+mean3weight);
		//System.out.println(result*100000);
		return result*100000;
	}
	public static float cannydiff(int[][][] startcanny,int[][][]goalcanny) {
		return histdiff2(startcanny,goalcanny);
	}
	public static float colordiff(BufferedImage changeimg, BufferedImage goalimg) {
		int width = changeimg.getWidth();
		int sumcr=0;int sumcg=0;int sumcb=0;
		int sumgr=0;int sumgg=0;int sumgb=0;
		for(int i =0; i<width*width;i++) {
			int w = i/width;
			int h = i%width;
			int crgb = changeimg.getRGB(w, h);
			int grgb = goalimg.getRGB(w, h);
			int cr = r(crgb);int cg = g(crgb);int cb = b(crgb);
			int gr = r(grgb);int gg = g(grgb);int gb = b(grgb);
			sumcr = sumcr+cr;sumcg = sumcg+cg;sumcb = sumcb+cb;
			sumgr = sumgr+gr;sumgg = sumgg+gg;sumgb = sumgb+gb;
		}
		int avecr = sumcr/(width*width);int avecg = sumcg/(width*width);int avecb = sumcb/(width*width);
		int avegr = sumgr/(width*width);int avegg = sumgg/(width*width);int avegb = sumgb/(width*width);
		int diff = (Math.abs(avecr-avegr)+Math.abs(avecg-avegg)+Math.abs(avecb-avegb))/3;
		
		float result = 1-(diff/255);
		return result;
	}
	public static float colordiff(int[][][] changeimg,int[][][]goalimg) {
		int width = changeimg.length;
		int[] sumc= {0,0,0}; int[] sumg = {0,0,0};
		for(int i = 0; i<width*width*3;i++) {
			int w = (i/width)%width;
			int h = i%width;
			int c = i/(width*width);
			sumc[c] = sumc[c]+changeimg[w][h][c];		
			sumg[c] = sumg[c]+goalimg[w][h][c];
		}
		float[] avec = {0,0,0};float[] aveg = {0,0,0};
		for(int j=0; j<3; j++) {
			avec[j] = sumc[j]/(width*width);
			aveg[j] = sumg[j]/(width*width);
		}
		float diff = (Math.abs(avec[0]-aveg[0])+Math.abs(avec[1]-aveg[1])+Math.abs(avec[2]-aveg[2]))/3;
		float result = 1-(diff/255);
		return result;
	}
	public static float histdiff(BufferedImage changeimg, BufferedImage goalimg) {
		int width = changeimg.getWidth();
		int sumcr=0;int sumcg=0;int sumcb=0;
		int sumgr=0;int sumgg=0;int sumgb=0;
		float sum2 = 0;
		for(int i =0; i<width*width;i++) {
			int w = i/width;
			int h = i%width;
			int crgb = changeimg.getRGB(w, h);
			int grgb = goalimg.getRGB(w, h);
			int cr = r(crgb);int cg = g(crgb);int cb = b(crgb);
			int gr = r(grgb);int gg = g(grgb);int gb = b(grgb);
			sumcr = sumcr+cr;sumcg = sumcg+cg;sumcb = sumcb+cb;
			sumgr = sumgr+gr;sumgg = sumgg+gg;sumgb = sumgb+gb;
			//ルートh_1(i)*h_2(i)
			sum2 = sum2+(float)Math.sqrt(cr*gr)+(float)Math.sqrt(cg*gg)+(float)Math.sqrt(cb*gb);
		}
		int avecr = sumcr/(width*width);int avecg = sumcg/(width*width);int avecb = sumcb/(width*width);
		int avegr = sumgr/(width*width);int avegg = sumgg/(width*width);int avegb = sumgb/(width*width);
		int avec = (avecr+avecg+avecb)/3;int aveg = (avegr+avegg+avegb)/3;
		float diff = (float) (Math.sqrt(1-(1/(3*width*width*(Math.sqrt(avec*aveg)))*sum2)));
		float result = 1-diff;
		return result;
	}
	public static float histdiff(int[][][] changeimg,int[][][] goalimg) {
		int width = changeimg.length;
		int[] sumc = {0,0,0};int[] sumg = {0,0,0};
		float sum2=0;
		for(int i = 0; i<width*width*3;i++) {
			int w = (i/width)%width;
			int h = i%width;
			int c = i/(width*width);
			sumc[c] = sumc[c]+changeimg[w][h][c];		
			sumg[c] = sumg[c]+goalimg[w][h][c];
			//ルートh_1(i)*h_2(i)
			sum2 = (float) (sum2+Math.sqrt(changeimg[w][h][c]*goalimg[w][h][c]));
		}
		int avec = (sumc[0]+sumc[1]+sumc[2])/(3*width*width);int aveg = (sumg[0]+sumg[1]+sumg[2])/(3*width*width);
		float diff = (float) (Math.sqrt(1-(1/(3*width*width*(Math.sqrt(avec*aveg))))*sum2));
		float result = 1-diff;
		return result;
	}
	public static float histdiff2(int[][][] changeimg,int[][][] goalimg) {
		int width = changeimg.length;
		float[] diffsum = {0,0,0};
		for(int i = 0; i<width*width*3;i++) {
			int w = (i/width)%width;
			int h = i%width;
			int c = i/(width*width);
			diffsum[c]=diffsum[c]+Math.abs(changeimg[w][h][c]-goalimg[w][h][c]);
		}
		float diff = ((diffsum[0]/(width*width))+(diffsum[1]/(width*width))+(diffsum[2]/(width*width)))/(3*255);
		
		float result = 1-diff;
		return result;
	}
	
	public static float cannycompare(BufferedImage startimg, BufferedImage goalimg) {
		return cannycompare(startimg,goalimg,50,200);
	}
	public static float cannycompare(BufferedImage startimg, BufferedImage goalimg , float t_min , float t_max) {
		int width = startimg.getWidth();
		int height = startimg.getHeight();
		
		//ガウシアンフィルタをかける
		float[][] gstart = new float[width-2][width-2];
		float[][] ggoal = new float[width-2][width-2];
		for (int i = 0; i < gstart.length*gstart.length ; i++ ) {
			int w = i/gstart.length;
			int h = i%gstart.length;
			int srgb11 = startimg.getRGB(w,h);int srgb21 = startimg.getRGB(w+1,h);int srgb31 = startimg.getRGB(w+2,h);int srgb12 = startimg.getRGB(w,h+1);int srgb22 = startimg.getRGB(w+1,h+1);int srgb32 = startimg.getRGB(w+2,h+1);int srgb13 = startimg.getRGB(w,h+2);int srgb23 = startimg.getRGB(w+1,h+2);int srgb33 = startimg.getRGB(w+2,h+2);
			int grgb11 = goalimg.getRGB(w,h);int grgb21 = goalimg.getRGB(w+1,h);int grgb31 = goalimg.getRGB(w+2,h);int grgb12 = goalimg.getRGB(w,h+1);int grgb22 = goalimg.getRGB(w+1,h+1);int grgb32 = goalimg.getRGB(w+2,h+1);int grgb13 = goalimg.getRGB(w,h+2);int grgb23 = goalimg.getRGB(w+1,h+2);int grgb33 = goalimg.getRGB(w+2,h+2);
			int sr11 = r(srgb11);int sr21 = r(srgb21);int sr31 = r(srgb31);int sr12 = r(srgb12);int sr22 = r(srgb22);int sr32 = r(srgb32);int sr13 = r(srgb13);int sr23 = r(srgb23);int sr33 = r(srgb33);int sg11 = g(srgb11);int sg21 = g(srgb21);int sg31 = g(srgb31);int sg12 = g(srgb12);int sg22 = g(srgb22);int sg32 = g(srgb32);int sg13 = g(srgb13);int sg23 = g(srgb23);int sg33 = g(srgb33);int sb11 = b(srgb11);int sb21 = b(srgb21);int sb31 = b(srgb31);int sb12 = b(srgb12);int sb22 = b(srgb22);int sb32 = b(srgb32);int sb13 = b(srgb13);	int sb23 = b(srgb23);int sb33 = b(srgb33);
			int gr11 = r(grgb11);int gr21 = r(grgb21);int gr31 = r(grgb31);int gr12 = r(grgb12);int gr22 = r(grgb22);int gr32 = r(grgb32);int gr13 = r(grgb13);int gr23 = r(grgb23);int gr33 = r(grgb33);int gg11 = g(grgb11);int gg21 = g(grgb21);int gg31 = g(grgb31);	int gg12 = g(grgb12);int gg22 = g(grgb22);int gg32 = g(grgb32);int gg13 = g(grgb13);int gg23 = g(grgb23);int gg33 = g(grgb33);int gb11 = b(grgb11);int gb21 = b(grgb21);int gb31 = b(grgb31);int gb12 = b(grgb12);int gb22 = b(grgb22);int gb32 = b(grgb32);int gb13 = b(grgb13);int gb23 = b(grgb23);int gb33 = b(grgb33);
			float setsr = sr11*1+sr21*2+sr31*1+sr12*2+sr22*4+sr32*2+sr13*1+sr23*2+sr33*1;
			float setsg = sg11*1+sg21*2+sg31*1+sg12*2+sg22*4+sg32*2+sg13*1+sg23*2+sg33*1;
			float setsb = sb11*1+sb21*2+sb31*1+sb12*2+sb22*4+sb32*2+sb13*1+sb23*2+sb33*1;
			float setgr = gr11*1+gr21*2+gr31*1+gr12*2+gr22*4+gr32*2+gr13*1+gr23*2+gr33*1;
			float setgg = gg11*1+gg21*2+gg31*1+gg12*2+gg22*4+gg32*2+gg13*1+gg23*2+gg33*1;
			float setgb = gb11*1+gb21*2+gb31*1+gb12*2+gb22*4+gb32*2+gb13*1+gb23*2+gb33*1;
			gstart[w][h] = (setsr+setsg+setsb)/3;
			ggoal[w][h] = (setgr+setgg+setgb)/3;
		}
		//ソーベルフィルタをかける
		float[][] sxstart = new float[width-4][width-4];
		float[][] sxgoal = new float[width-4][width-4];
		float[][] systart = new float[width-4][width-4];
		float[][] sygoal = new float[width-4][width-4];
		for (int i = 0; i < sxstart.length*sxstart.length ; i++ ) {
			int w = i/sxstart.length;
			int h = i%sxstart.length;
			float setsx = gstart[w][h]*-1+gstart[w+1][h]*0+gstart[w+2][h]*1+gstart[w][h+1]*-2+gstart[w+1][h+1]*0+gstart[w+2][h+1]*2+gstart[w][h+2]*-1+gstart[w+1][h+2]*0+gstart[w+2][h+2]*1;
			float setgx = ggoal[w][h]*-1+ggoal[w+1][h]*0+ggoal[w+2][h]*1+ggoal[w][h+1]*-2+ggoal[w+1][h+1]*0+ggoal[w+2][h+1]*2+ggoal[w][h+2]*-1+ggoal[w+1][h+2]*0+ggoal[w+2][h+2]*1;
			float setsy = gstart[w][h]*-1+gstart[w+1][h]*-2+gstart[w+2][h]*-1+gstart[w][h+1]*0+gstart[w+1][h+1]*0+gstart[w+2][h+1]*0+gstart[w][h+2]*1+gstart[w+1][h+2]*2+gstart[w+2][h+2]*1;
			float setgy = ggoal[w][h]*-1+ggoal[w+1][h]*-2+ggoal[w+2][h]*-1+ggoal[w][h+1]*0+ggoal[w+1][h+1]*0+ggoal[w+2][h+1]*0+ggoal[w][h+2]*1+ggoal[w+1][h+2]*2+ggoal[w+2][h+2]*1;
			sxstart[w][h] = setsx;
			sxgoal[w][h] = setgx;
			systart[w][h] = setsy;
			sygoal[w][h] = setgy;
		}

		//勾配の大きさと角度の算出
		float[][] slstartimg = new float[sxstart.length][sxstart.length];
		float[][] sistartimg = new float[sxstart.length][sxstart.length];
		float[][] slgoalimg = new float[sxstart.length][sxstart.length];
		float[][] sigoalimg = new float[sxstart.length][sxstart.length];
		float[][] sx = sxstart;
		float[][] sy = systart;
		float[][] gx = sxgoal;
		float[][] gy = sygoal;
		for (int i = 0; i < sxstart.length*sxstart.length ; i++ ) {
			int w = i/sxstart.length;
			int h = i%sxstart.length;
			
			slstartimg[w][h] = (float) Math.sqrt((sx[w][h]*sx[w][h])+(sy[w][h]*sy[w][h]));
			slgoalimg[w][h] = (float) Math.sqrt((gx[w][h]*gx[w][h])+(gy[w][h]*gy[w][h]));
			sistartimg[w][h] = (float) Math.atan(((sy[w][h]+0.000001)/3)/((sx[w][h]+0.000001)/3));
			sigoalimg[w][h] = (float) Math.atan(((gy[w][h]+0.000001)/3)/((gx[w][h]+0.000001)/3));
		}
		//non maximum suppersion
		float[][] nmsstartimg = new float[slstartimg.length-2][slstartimg.length-2];
		float[][] nmsgoalimg = new float[slstartimg.length-2][slstartimg.length-2];
		float pi = (float) Math.PI;
		for(int i = 0; i < nmsstartimg.length*nmsstartimg.length; i++) {
			int w = i/nmsstartimg.length;
			int h = i%nmsstartimg.length;
			//System.out.println(sistartimg[w+1][h+1]);
			float ssi = sistartimg[w+1][h+1]%(2*pi);
			
			nmsstartimg[w][h] = slstartimg[w+1][h+1];
			if((ssi<=(1/8)*pi||ssi>=(15/8)*pi)||(ssi<=(9/8)*pi&&ssi>=(7/8)*pi)) {
				if(sistartimg[w+2][h+1]>sistartimg[w+1][h+1]&&sistartimg[w][h+1]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
					
				}
			}else if((ssi<=(5/8)*pi&&ssi>=(3/8)*pi)||(ssi<=(13/8)*pi&&ssi>=(11/8)*pi)) {
				if(sistartimg[w+1][h+2]>sistartimg[w+1][h+1]&&sistartimg[w+1][h]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
					
				}
			}else if((ssi<=(3/8)*pi&&ssi>=(1/8)*pi)||(ssi<=(11/8)*pi&&ssi>=(9/8)*pi)) {
				if(sistartimg[w+2][h]>sistartimg[w+1][h+1]&&sistartimg[w][h+2]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
				}
			}else if((ssi<=(7/8)*pi&&ssi>=(5/8)*pi)||(ssi<=(15/8)*pi&&ssi>=(13/8)*pi)) {
				if(sistartimg[w][h]>sistartimg[w+1][h+1]&&sistartimg[w+2][h+2]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
				}
			}
			float gsi = sigoalimg[w+1][h+1]%(2*pi);
			nmsgoalimg[w][h] = slgoalimg[w+1][h+1];
			if((gsi<=(1/8)*pi||gsi>=(15/8)*pi)||(gsi<=(9/8)*pi&&gsi>=(7/8)*pi)) {
				if(sigoalimg[w+2][h+1]>sigoalimg[w+1][h+1]&&sigoalimg[w][h+1]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(5/8)*pi&&gsi>=(3/8)*pi)||(gsi<=(13/8)*pi&&gsi>=(11/8)*pi)) {
				if(sigoalimg[w+1][h+2]>sigoalimg[w+1][h+1]&&sigoalimg[w+1][h]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(3/8)*pi&&gsi>=(1/8)*pi)||(gsi<=(11/8)*pi&&gsi>=(9/8)*pi)) {
				if(sigoalimg[w+2][h]>sigoalimg[w+1][h+1]&&sigoalimg[w][h+2]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(7/8)*pi&&gsi>=(5/8)*pi)||(gsi<=(15/8)*pi&&gsi>=(13/8)*pi)) {
				if(sigoalimg[w][h]>sigoalimg[w+1][h+1]&&sigoalimg[w+2][h+2]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}
			
		}
		BufferedImage nstartimg = new BufferedImage(nmsstartimg.length, nmsstartimg.length, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage ngoalimg = new BufferedImage(nmsstartimg.length, nmsstartimg.length, BufferedImage.TYPE_3BYTE_BGR);
		for(int i = 0; i<nmsstartimg.length*nmsstartimg.length; i++) {
			int w = i/nmsstartimg.length;
			int h = i%nmsstartimg.length;
			nstartimg.setRGB(w, h, rgb((int)nmsstartimg[w][h],(int)nmsstartimg[w][h],(int)nmsstartimg[w][h]));
			ngoalimg.setRGB(w, h, rgb((int)nmsgoalimg[w][h],(int)nmsgoalimg[w][h],(int)nmsgoalimg[w][h]));
		}
		
		
		//hysterisis threshhold処理
		int[][] hystartimg = new int[nmsstartimg.length-4][nmsstartimg.length-4];
		int[][] hygoalimg = new int[nmsstartimg.length-4][nmsstartimg.length-4];
		float[][] nmss=nmsstartimg;
		float[][] nmsg=nmsgoalimg;
		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
			int w = i/hystartimg.length;
			int h = i%hystartimg.length;
			float st = nmsstartimg[w+2][h+2];
			if(st>t_max) {
				hystartimg[w][h]=255;
			}else if(st<t_min) {
				hystartimg[w][h]=0;
			}else {
				hystartimg[w][h]=0;
				if(nmss[w][h]>t_max||nmss[w+1][h]>t_max||nmss[w+2][h]>t_max||nmss[w+3][h]>t_max||nmss[w+4][h]>t_max||nmss[w][h+1]>t_max||nmss[w+1][h+1]>t_max||nmss[w+2][h+1]>t_max||nmss[w+3][h+1]>t_max||nmss[w+4][h+1]>t_max||nmss[w][h+2]>t_max||nmss[w+1][h+2]>t_max||nmss[w+2][h+2]>t_max||nmss[w+3][h+2]>t_max||nmss[w+4][h+2]>t_max||nmss[w][h+3]>t_max||nmss[w+1][h+3]>t_max||nmss[w+2][h+3]>t_max||nmss[w+3][h+3]>t_max||nmss[w+4][h+3]>t_max||nmss[w][h+4]>t_max||nmss[w+1][h+4]>t_max||nmss[w+2][h+4]>t_max||nmss[w+3][h+4]>t_max||nmss[w+4][h+4]>t_max) {
					hystartimg[w][h]=255;
				}
			}
			float gt = nmsgoalimg[w+2][h+2];
			if(gt>t_max) {
				hygoalimg[w][h]=255;
			}else if(gt<t_min) {
				hygoalimg[w][h]=0;
			}else {
				
				if(nmsg[w][h]>t_max||nmsg[w+1][h]>t_max||nmsg[w+2][h]>t_max||nmsg[w+3][h]>t_max||nmsg[w+4][h]>t_max||nmsg[w][h+1]>t_max||nmsg[w+1][h+1]>t_max||nmsg[w+2][h+1]>t_max||nmsg[w+3][h+1]>t_max||nmsg[w+4][h+1]>t_max||nmsg[w][h+2]>t_max||nmsg[w+1][h+2]>t_max||nmsg[w+2][h+2]>t_max||nmsg[w+3][h+2]>t_max||nmsg[w+4][h+2]>t_max||nmsg[w][h+3]>t_max||nmsg[w+1][h+3]>t_max||nmsg[w+2][h+3]>t_max||nmsg[w+3][h+3]>t_max||nmsg[w+4][h+3]>t_max||nmsg[w][h+4]>t_max||nmsg[w+1][h+4]>t_max||nmsg[w+2][h+4]>t_max||nmsg[w+3][h+4]>t_max||nmsg[w+4][h+4]>t_max) {
					hystartimg[w][h]=255;
				}
			}
		}
		
		//差の平均値を調べる
		float sum=0;
		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
			int w = i/hystartimg.length;
			int h = i%hystartimg.length;
			sum = sum+Math.abs(hystartimg[w][h]-hygoalimg[w][h]);
		}
		float ave = sum/(hystartimg.length*hystartimg.length);
		//画像の保存
//		BufferedImage resultstartimg = new BufferedImage(hystartimg.length, hystartimg.length, BufferedImage.TYPE_3BYTE_BGR);
//		BufferedImage resultgoalimg = new BufferedImage(hystartimg.length, hystartimg.length, BufferedImage.TYPE_3BYTE_BGR);
//		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
//			int w = i/hystartimg.length;
//			int h = i%hystartimg.length;
//			resultstartimg.setRGB(w, h, rgb(hystartimg[w][h],hystartimg[w][h],hystartimg[w][h]));
//			resultgoalimg.setRGB(w, h, rgb(hygoalimg[w][h],hygoalimg[w][h],hygoalimg[w][h]));
//		}
//		try {
//			ImageIO.write(resultstartimg, "jpg", new File(currentpath+"/../result/startcanny.jpg"));
//			ImageIO.write(resultgoalimg, "jpg", new File(currentpath+"/../result/goalcanny.jpg"));
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		float result = 1-(ave/255);
		return result;
	}
	public static float cannycompare(int[][][] startimg, int[][][] goalimg , float t_min , float t_max) {
		int width = startimg.length;
		int height = startimg.length;
		
		//ガウシアンフィルタをかける
		float[][] gstart = new float[width-2][width-2];
		float[][] ggoal = new float[width-2][width-2];
		for (int i = 0; i < gstart.length*gstart.length ; i++ ) {
			int w = i/gstart.length;
			int h = i%gstart.length;
			float setsr = startimg[w][h][0]*1+startimg[w+1][h][0]*2+startimg[w+2][h][0]*1+startimg[w][h+1][0]*2+startimg[w+1][h+1][0]*4+startimg[w+2][h+1][0]*2+startimg[w][h+2][0]*1+startimg[w+1][h+2][0]*2+startimg[w+2][h+2][0]*1;
			float setsg = startimg[w][h][1]*1+startimg[w+1][h][1]*2+startimg[w+2][h][1]*1+startimg[w][h+1][1]*2+startimg[w+1][h+1][1]*4+startimg[w+2][h+1][1]*2+startimg[w][h+2][1]*1+startimg[w+1][h+2][1]*2+startimg[w+2][h+2][1]*1;
			float setsb = startimg[w][h][2]*1+startimg[w+1][h][2]*2+startimg[w+2][h][2]*1+startimg[w][h+1][2]*2+startimg[w+1][h+1][2]*4+startimg[w+2][h+1][2]*2+startimg[w][h+2][2]*1+startimg[w+1][h+2][2]*2+startimg[w+2][h+2][2]*1;
			float setgr = goalimg[w][h][0]*1+goalimg[w+1][h][0]*2+goalimg[w+2][h][0]*1+goalimg[w][h+1][0]*2+goalimg[w+1][h+1][0]*4+goalimg[w+2][h+1][0]*2+goalimg[w][h+2][0]*1+goalimg[w+1][h+2][0]*2+goalimg[w+2][h+2][0]*1;
			float setgg = goalimg[w][h][1]*1+goalimg[w+1][h][1]*2+goalimg[w+2][h][1]*1+goalimg[w][h+1][1]*2+goalimg[w+1][h+1][1]*4+goalimg[w+2][h+1][1]*2+goalimg[w][h+2][1]*1+goalimg[w+1][h+2][1]*2+goalimg[w+2][h+2][1]*1;
			float setgb = goalimg[w][h][2]*1+goalimg[w+1][h][2]*2+goalimg[w+2][h][2]*1+goalimg[w][h+1][2]*2+goalimg[w+1][h+1][2]*4+goalimg[w+2][h+1][2]*2+goalimg[w][h+2][2]*1+goalimg[w+1][h+2][2]*2+goalimg[w+2][h+2][2]*1;
			gstart[w][h] = (setsr+setsg+setsb)/3;
			ggoal[w][h] = (setgr+setgg+setgb)/3;
		}
		
		//ソーベルフィルタをかける
		float[][] sxstart = new float[width-4][width-4];
		float[][] sxgoal = new float[width-4][width-4];
		float[][] systart = new float[width-4][width-4];
		float[][] sygoal = new float[width-4][width-4];
		for (int i = 0; i < sxstart.length*sxstart.length ; i++ ) {
			int w = i/sxstart.length;
			int h = i%sxstart.length;
			float setsx = gstart[w][h]*-1+gstart[w+1][h]*0+gstart[w+2][h]*1+gstart[w][h+1]*-2+gstart[w+1][h+1]*0+gstart[w+2][h+1]*2+gstart[w][h+2]*-1+gstart[w+1][h+2]*0+gstart[w+2][h+2]*1;
			float setgx = ggoal[w][h]*-1+ggoal[w+1][h]*0+ggoal[w+2][h]*1+ggoal[w][h+1]*-2+ggoal[w+1][h+1]*0+ggoal[w+2][h+1]*2+ggoal[w][h+2]*-1+ggoal[w+1][h+2]*0+ggoal[w+2][h+2]*1;
			float setsy = gstart[w][h]*-1+gstart[w+1][h]*-2+gstart[w+2][h]*-1+gstart[w][h+1]*0+gstart[w+1][h+1]*0+gstart[w+2][h+1]*0+gstart[w][h+2]*1+gstart[w+1][h+2]*2+gstart[w+2][h+2]*1;
			float setgy = ggoal[w][h]*-1+ggoal[w+1][h]*-2+ggoal[w+2][h]*-1+ggoal[w][h+1]*0+ggoal[w+1][h+1]*0+ggoal[w+2][h+1]*0+ggoal[w][h+2]*1+ggoal[w+1][h+2]*2+ggoal[w+2][h+2]*1;
			sxstart[w][h] = setsx/20;
			sxgoal[w][h] = setgx/20;
			systart[w][h] = setsy/20;
			sygoal[w][h] = setgy/20;
		}

		//勾配の大きさと角度の算出
		float[][] slstartimg = new float[sxstart.length][sxstart.length];
		float[][] sistartimg = new float[sxstart.length][sxstart.length];
		float[][] slgoalimg = new float[sxstart.length][sxstart.length];
		float[][] sigoalimg = new float[sxstart.length][sxstart.length];
		float[][] sx = sxstart;
		float[][] sy = systart;
		float[][] gx = sxgoal;
		float[][] gy = sygoal;
		for (int i = 0; i < sxstart.length*sxstart.length ; i++ ) {
			int w = i/sxstart.length;
			int h = i%sxstart.length;
			
			slstartimg[w][h] = (float) Math.sqrt((sx[w][h]*sx[w][h])+(sy[w][h]*sy[w][h]));
			slgoalimg[w][h] = (float) Math.sqrt((gx[w][h]*gx[w][h])+(gy[w][h]*gy[w][h]));
			sistartimg[w][h] = (float) Math.atan(((sy[w][h]+0.000001)/3)/((sx[w][h]+0.000001)/3));
			sigoalimg[w][h] = (float) Math.atan(((gy[w][h]+0.000001)/3)/((gx[w][h]+0.000001)/3));
		}
		//non maximum suppersionー
		
		float[][] nmsstartimg = new float[slstartimg.length-2][slstartimg.length-2];
		float[][] nmsgoalimg = new float[slstartimg.length-2][slstartimg.length-2];
		float pi = (float) Math.PI;
		for(int i = 0; i < nmsstartimg.length*nmsstartimg.length; i++) {
			int w = i/nmsstartimg.length;
			int h = i%nmsstartimg.length;
			//System.out.println(sistartimg[w+1][h+1]);
			float ssi = sistartimg[w+1][h+1]%(2*pi);
			
			nmsstartimg[w][h] = slstartimg[w+1][h+1];
			if((ssi<=(1/8)*pi||ssi>=(15/8)*pi)||(ssi<=(9/8)*pi&&ssi>=(7/8)*pi)) {
				if(sistartimg[w+2][h+1]>sistartimg[w+1][h+1]&&sistartimg[w][h+1]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
					
				}
			}else if((ssi<=(5/8)*pi&&ssi>=(3/8)*pi)||(ssi<=(13/8)*pi&&ssi>=(11/8)*pi)) {
				if(sistartimg[w+1][h+2]>sistartimg[w+1][h+1]&&sistartimg[w+1][h]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
					
				}
			}else if((ssi<=(3/8)*pi&&ssi>=(1/8)*pi)||(ssi<=(11/8)*pi&&ssi>=(9/8)*pi)) {
				if(sistartimg[w+2][h]>sistartimg[w+1][h+1]&&sistartimg[w][h+2]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
				}
			}else if((ssi<=(7/8)*pi&&ssi>=(5/8)*pi)||(ssi<=(15/8)*pi&&ssi>=(13/8)*pi)) {
				if(sistartimg[w][h]>sistartimg[w+1][h+1]&&sistartimg[w+2][h+2]>sistartimg[w+1][h+1]) {
					nmsstartimg[w][h]=0;
				}
			}
			float gsi = sigoalimg[w+1][h+1]%(2*pi);
			nmsgoalimg[w][h] = slgoalimg[w+1][h+1];
			if((gsi<=(1/8)*pi||gsi>=(15/8)*pi)||(gsi<=(9/8)*pi&&gsi>=(7/8)*pi)) {
				if(sigoalimg[w+2][h+1]>sigoalimg[w+1][h+1]&&sigoalimg[w][h+1]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(5/8)*pi&&gsi>=(3/8)*pi)||(gsi<=(13/8)*pi&&gsi>=(11/8)*pi)) {
				if(sigoalimg[w+1][h+2]>sigoalimg[w+1][h+1]&&sigoalimg[w+1][h]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(3/8)*pi&&gsi>=(1/8)*pi)||(gsi<=(11/8)*pi&&gsi>=(9/8)*pi)) {
				if(sigoalimg[w+2][h]>sigoalimg[w+1][h+1]&&sigoalimg[w][h+2]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}else if((gsi<=(7/8)*pi&&gsi>=(5/8)*pi)||(gsi<=(15/8)*pi&&gsi>=(13/8)*pi)) {
				if(sigoalimg[w][h]>sigoalimg[w+1][h+1]&&sigoalimg[w+2][h+2]>sigoalimg[w+1][h+1]) {
					nmsgoalimg[w][h]=0;
				}
			}
			
		}
		BufferedImage nstartimg = new BufferedImage(nmsstartimg.length, nmsstartimg.length, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage ngoalimg = new BufferedImage(nmsstartimg.length, nmsstartimg.length, BufferedImage.TYPE_3BYTE_BGR);
		for(int i = 0; i<nmsstartimg.length*nmsstartimg.length; i++) {
			int w = i/nmsstartimg.length;
			int h = i%nmsstartimg.length;
			nstartimg.setRGB(w, h, rgb((int)nmsstartimg[w][h],(int)nmsstartimg[w][h],(int)nmsstartimg[w][h]));
			ngoalimg.setRGB(w, h, rgb((int)nmsgoalimg[w][h],(int)nmsgoalimg[w][h],(int)nmsgoalimg[w][h]));
		}
		
		
		//hysterisis threshhold処理
		int[][] hystartimg = new int[nmsstartimg.length-4][nmsstartimg.length-4];
		int[][] hygoalimg = new int[nmsstartimg.length-4][nmsstartimg.length-4];
		float[][] nmss=nmsstartimg;
		float[][] nmsg=nmsgoalimg;
		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
			int w = i/hystartimg.length;
			int h = i%hystartimg.length;
			float st = nmsstartimg[w+2][h+2];
			if(st>t_max) {
				hystartimg[w][h]=255;
			}else if(st<t_min) {
				hystartimg[w][h]=0;
			}else {
				hystartimg[w][h]=0;
				if(nmss[w][h]>t_max||nmss[w+1][h]>t_max||nmss[w+2][h]>t_max||nmss[w+3][h]>t_max||nmss[w+4][h]>t_max||nmss[w][h+1]>t_max||nmss[w+1][h+1]>t_max||nmss[w+2][h+1]>t_max||nmss[w+3][h+1]>t_max||nmss[w+4][h+1]>t_max||nmss[w][h+2]>t_max||nmss[w+1][h+2]>t_max||nmss[w+2][h+2]>t_max||nmss[w+3][h+2]>t_max||nmss[w+4][h+2]>t_max||nmss[w][h+3]>t_max||nmss[w+1][h+3]>t_max||nmss[w+2][h+3]>t_max||nmss[w+3][h+3]>t_max||nmss[w+4][h+3]>t_max||nmss[w][h+4]>t_max||nmss[w+1][h+4]>t_max||nmss[w+2][h+4]>t_max||nmss[w+3][h+4]>t_max||nmss[w+4][h+4]>t_max) {
					hystartimg[w][h]=255;
				}
			}
			float gt = nmsgoalimg[w+2][h+2];
			if(gt>t_max) {
				hygoalimg[w][h]=255;
			}else if(gt<t_min) {
				hygoalimg[w][h]=0;
			}else {
				
				if(nmsg[w][h]>t_max||nmsg[w+1][h]>t_max||nmsg[w+2][h]>t_max||nmsg[w+3][h]>t_max||nmsg[w+4][h]>t_max||nmsg[w][h+1]>t_max||nmsg[w+1][h+1]>t_max||nmsg[w+2][h+1]>t_max||nmsg[w+3][h+1]>t_max||nmsg[w+4][h+1]>t_max||nmsg[w][h+2]>t_max||nmsg[w+1][h+2]>t_max||nmsg[w+2][h+2]>t_max||nmsg[w+3][h+2]>t_max||nmsg[w+4][h+2]>t_max||nmsg[w][h+3]>t_max||nmsg[w+1][h+3]>t_max||nmsg[w+2][h+3]>t_max||nmsg[w+3][h+3]>t_max||nmsg[w+4][h+3]>t_max||nmsg[w][h+4]>t_max||nmsg[w+1][h+4]>t_max||nmsg[w+2][h+4]>t_max||nmsg[w+3][h+4]>t_max||nmsg[w+4][h+4]>t_max) {
					hystartimg[w][h]=255;
				}
			}
		}
		
		//差の平均値を調べる
		float sum=0;
		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
			int w = i/hystartimg.length;
			int h = i%hystartimg.length;
			sum = sum+Math.abs(hystartimg[w][h]-hygoalimg[w][h]);
		}
		float ave = sum/(hystartimg.length*hystartimg.length);
		//画像の保存
//		BufferedImage resultstartimg = new BufferedImage(hystartimg.length, hystartimg.length, BufferedImage.TYPE_3BYTE_BGR);
//		BufferedImage resultgoalimg = new BufferedImage(hystartimg.length, hystartimg.length, BufferedImage.TYPE_3BYTE_BGR);
//		for(int i = 0; i<hystartimg.length*hystartimg.length; i++) {
//			int w = i/hystartimg.length;
//			int h = i%hystartimg.length;
//			resultstartimg.setRGB(w, h, rgb(hystartimg[w][h],hystartimg[w][h],hystartimg[w][h]));
//			resultgoalimg.setRGB(w, h, rgb(hygoalimg[w][h],hygoalimg[w][h],hygoalimg[w][h]));
//		}
//		try {
//			ImageIO.write(resultstartimg, "jpg", new File(currentpath+"/../result/startcanny.jpg"));
//			ImageIO.write(resultgoalimg, "jpg", new File(currentpath+"/../result/goalcanny.jpg"));
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		float result = 1-(ave/255);
		return result;
	}
	
	public static int[][][] tocanny(int[][][] img) {
		return tocanny(img,700,2500);
	}
	
	public static int[][][] tocanny(int[][][] imgi,float t_min , float t_max){
		int width = imgi.length;
		int height = imgi.length;
		float[][] gimg = new float[width-2][width-2];
		for (int i = 0; i < gimg.length*gimg.length;i++ ) {
			int w = (i/gimg.length);
			int h = i%gimg.length;
			float setsr = imgi[w][h][0]*1+imgi[w+1][h][0]*2+imgi[w+2][h][0]*1+imgi[w][h+1][0]*2+imgi[w+1][h+1][0]*4+imgi[w+2][h+1][0]*2+imgi[w][h+2][0]*1+imgi[w+1][h+2][0]*2+imgi[w+2][h+2][0]*1;
			float setsg = imgi[w][h][1]*1+imgi[w+1][h][1]*2+imgi[w+2][h][1]*1+imgi[w][h+1][1]*2+imgi[w+1][h+1][1]*4+imgi[w+2][h+1][1]*2+imgi[w][h+2][1]*1+imgi[w+1][h+2][1]*2+imgi[w+2][h+2][1]*1;
			float setsb = imgi[w][h][2]*1+imgi[w+1][h][2]*2+imgi[w+2][h][2]*1+imgi[w][h+1][2]*2+imgi[w+1][h+1][2]*4+imgi[w+2][h+1][2]*2+imgi[w][h+2][2]*1+imgi[w+1][h+2][2]*2+imgi[w+2][h+2][2]*1;
			gimg[w][h] = (setsr+setsg+setsb)/3;
		}
		//ソーベルフィルタをかける
		float[][] sximg = new float[width-4][width-4];
		float[][] syimg = new float[width-4][width-4];
		for (int i = 0; i < sximg.length*sximg.length ; i++ ) {
			int w = i/sximg.length;
			int h = i%sximg.length;
			float setsx = gimg[w][h]*-1+gimg[w+1][h]*0+gimg[w+2][h]*1+gimg[w][h+1]*-2+gimg[w+1][h+1]*0+gimg[w+2][h+1]*2+gimg[w][h+2]*-1+gimg[w+1][h+2]*0+gimg[w+2][h+2]*1;
			float setsy = gimg[w][h]*-1+gimg[w+1][h]*-2+gimg[w+2][h]*-1+gimg[w][h+1]*0+gimg[w+1][h+1]*0+gimg[w+2][h+1]*0+gimg[w][h+2]*1+gimg[w+1][h+2]*2+gimg[w+2][h+2]*1;
			sximg[w][h] = setsx;
			syimg[w][h] = setsy;
		}
//		BufferedImage sobelimg = new BufferedImage(sximg.length, sximg.length, BufferedImage.TYPE_INT_RGB);
//		for(int i =0; i<sximg.length*sximg.length;i++) {
//			int w = i/sximg.length;
//			int h = i%sximg.length;
//			sobelimg.setRGB(w, h, rgb((int)sximg[w][h],(int)sximg[w][h],(int)sximg[w][h]));
//		}
//		try {
//			ImageIO.write(sobelimg, "jpg", new File(currentpath+"/../result/sobelimg.jpg"));
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		//勾配の大きさと角度の算出
		float[][] slimg = new float[sximg.length][sximg.length];
		float[][] siimg = new float[sximg.length][sximg.length];
		for (int i = 0; i < sximg.length*sximg.length ; i++ ) {
			int w = i/sximg.length;
			int h = i%sximg.length;
			slimg[w][h] = (float) Math.sqrt((sximg[w][h]*sximg[w][h])+(syimg[w][h]*syimg[w][h]));
			siimg[w][h] = (float) Math.atan(((sximg[w][h]+0.000001))/((syimg[w][h]+0.000001)));
		}
//		BufferedImage slimgi = new BufferedImage(slimg.length, slimg.length, BufferedImage.TYPE_INT_RGB);
//		for(int i =0; i<slimg.length*slimg.length;i++) {
//			int w = i/slimg.length;
//			int h = i%slimg.length;
//			slimgi.setRGB(w, h, rgb((int)slimg[w][h],(int)slimg[w][h],(int)slimg[w][h]));
//		}
//		try {
//			ImageIO.write(slimgi, "jpg", new File(currentpath+"/../result/slimg.jpg"));
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		//non maximum suppersion
		float[][] nmsimg = new float[slimg.length-2][slimg.length-2];
		float pi = (float) Math.PI;
		for(int i = 0; i < nmsimg.length*nmsimg.length; i++) {
			int w = i/nmsimg.length;
			int h = i%nmsimg.length;
			float ssi = (siimg[w+1][h+1])%(2*pi);
			//System.out.println(ssi);
			nmsimg[w][h] = slimg[w+1][h+1];
			if((ssi<=(1/8)*pi&&ssi>=(-1/8)*pi)||(ssi<=(9/8)*pi&&ssi>=(7/8)*pi)) {
				if(siimg[w+2][h+1]>siimg[w+1][h+1]&&siimg[w][h+1]>siimg[w+1][h+1]) {
					nmsimg[w][h]=0;
				}
			}else if((ssi<=(5/8)*pi&&ssi>=(3/8)*pi)||(ssi<=(-3/8)*pi&&ssi>=(-5/8)*pi)) {
				if(siimg[w+1][h+2]>siimg[w+1][h+1]&&siimg[w+1][h]>siimg[w+1][h+1]) {
					nmsimg[w][h]=0;
				}
			}else if((ssi<=(3/8)*pi&&ssi>=(1/8)*pi)||(ssi<=(-5/8)*pi&&ssi>=(-7/8)*pi)) {
				if(siimg[w+2][h]>siimg[w+1][h+1]&&siimg[w][h+2]>siimg[w+1][h+1]) {
					nmsimg[w][h]=0;
				}
			}else if((ssi<=(7/8)*pi&&ssi>=(5/8)*pi)||(ssi<=(-1/8)*pi&&ssi>=(-3/8)*pi)) {
				if(siimg[w][h]>siimg[w+1][h+1]&&siimg[w+2][h+2]>siimg[w+1][h+1]) {
					nmsimg[w][h]=0;
				}
			}
			
		}
//		BufferedImage nmimg = new BufferedImage(nmsimg.length, nmsimg.length, BufferedImage.TYPE_INT_RGB);
//		for(int i =0; i<nmsimg.length*nmsimg.length;i++) {
//			int w = i/nmsimg.length;
//			int h = i%nmsimg.length;
//			nmimg.setRGB(w, h, rgb((int)nmsimg[w][h],(int)nmsimg[w][h],(int)nmsimg[w][h]));
//		}
//		try {
//			ImageIO.write(nmimg, "jpg", new File(currentpath+"/../result/nmsimg.jpg"));
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		//hysterisis threshhold処理
		int[][] hyimg = new int[nmsimg.length-2][nmsimg.length-2];
		float[][] nmss=nmsimg;
		for(int i = 0; i<hyimg.length*hyimg.length; i++) {
			int w = i/hyimg.length;
			int h = i%hyimg.length;
			float st = nmsimg[w+2][h+2];
			if(st>t_max) {
				hyimg[w][h]=255;
			}else if(st<t_min) {
				hyimg[w][h]=0;
			}else {
				hyimg[w][h]=0;
				if(nmss[w][h]>t_max||nmss[w+1][h]>t_max||nmss[w+2][h]>t_max||nmss[w][h+1]>t_max||nmss[w+1][h+1]>t_max||nmss[w+2][h+1]>t_max||nmss[w][h+2]>t_max||nmss[w+1][h+2]>t_max||nmss[w+2][h+2]>t_max) {
					hyimg[w][h]=255;
				}
			}
			
		}
		//次元を増やす
		int [][][] preresultimg = new int [hyimg.length][hyimg.length][3];
		for(int i =0; i<hyimg.length*hyimg.length*3; i++) {
			int w = i/hyimg.length%hyimg.length;
			int h = i%hyimg.length;
			int c = i/(hyimg.length*hyimg.length);
			preresultimg[w][h][c] = hyimg[w][h];
		} 
		//画像サイズを戻す
		int [][][] resultimg = new int [imgi.length][imgi.length][3];
		for(int i =0; i<imgi.length*imgi.length*3; i++) {
			int w = i/imgi.length%imgi.length;
			int h = i%imgi.length;
			int c = i/(imgi.length*imgi.length);
			if(4<=w&&imgi.length-5>=w&&4<=h&&imgi.length-5>=h) {
				resultimg[w][h][c]=preresultimg[w-4][h-4][c];
			}else {
				resultimg[w][h][c]=0;
			}
		} 
		return resultimg;	
	}
}
