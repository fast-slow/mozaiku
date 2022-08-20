package mozaikujava;

public class histsix {
		
	public int[][] sixstart;
	public int[][] sixgoal;
	public int[][] sixchange;
	public int[][] sixcannystart;
	public int[][] sixcannygoal;
	public int rotate;
	public int[] size;
	public int wholesize;
	public int sixsize;
	public float hist(float[] startcenterposition,float[]  goalcenterposition ,int[][][] startimg,int[][][] changeimg,int[][][] goalimg,int[][][] startcanny,int[][][] goalcanny,int sixsize ,int rotate) {
		int mean1weight=1; int mean2weight=10; int mean3weight=1;
		this.rotate = rotate;
		this.sixsize = sixsize;
		histregister(startcenterposition,goalcenterposition,startimg,changeimg,goalimg,startcanny,goalcanny);
		float mean1 = cannydiff(sixcannystart,sixcannygoal);
		float mean2 = colordiff(sixchange,sixgoal);
		float mean3 = histdiff(sixchange,sixgoal);
		float result = (mean1*mean1weight+mean2*mean2weight+mean3*mean3weight)/(mean1weight+mean2weight+mean3weight);
		//System.out.println(result*100000);
		return result*100000;
	}
	public float hist(float[] startcenterposition,float[]  goalcenterposition ,int[][][] startimg,int[][][] changeimg,int[][][] goalimg,int[][][] startcanny,int[][][] goalcanny ,int rotate) {
		return hist(startcenterposition, goalcenterposition, startimg, changeimg, goalimg, startcanny, goalcanny, 20, rotate);
	}
	
	void histregister(float[] startcenterposition,float[]  goalcenterposition ,int[][][] startimg,int[][][] changeimg,int[][][] goalimg,int[][][] startcanny,int[][][] goalcanny) {
		float[] top = {startcenterposition[0],startcenterposition[1]+sixsize};
		float subheight = sixsize/2;
		//size..上から数えた時の横幅
		size = new int[sixsize*2];
		for(int i=0; i<sixsize*2;i++) {
			if(i<sixsize/2) {
				size[i]=(int) ((0.5+i)*Math.sqrt(12));
			}else if(i<=sixsize*1.5) {
				size[i]=(int)(sixsize*Math.sqrt(3));
			}else if(i<sixsize*2) {
				size[i]=(int)((0.5+sixsize*2-i-1)*Math.sqrt(12));
			}
		}
		wholesize =0;
		for(int i=0;i<size.length;i++) {
			wholesize += size[i];
		}
		sixstart= new int[wholesize][3];
		sixgoal= new int[wholesize][3];
		sixchange= new int[wholesize][3];
		sixcannystart = new int[wholesize][3];
		sixcannygoal= new int[wholesize][3];
		//画像の当てはめ
		sixstart = fitting(startcenterposition,startimg,rotate);
		sixgoal = fitting(goalcenterposition,goalimg,0);
		sixchange = fitting(startcenterposition,changeimg,rotate);
		sixcannystart = fitting(startcenterposition,startcanny,rotate);
		sixcannygoal = fitting(goalcenterposition,goalcanny,0);
	}
	
	public int[][] fitting(float[] centerposition, int[][][] img,int rotate) {
		
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
				//System.out.println("フィット一つ開始");
				float wholewidth = size[i];
				float height = (float) (sixsize-0.5-i);
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
					}
					fittingimg[count][k] = (int) positioncolor[k];
				}
				count++;
			}
		}
		return fittingimg;
	}
	
	public float cannydiff(int[][] startcanny,int[][] goalcanny) {
		
		return histdiff(startcanny,goalcanny);
	}
	
	public float histdiff(int[][] changeimg,int[][] goalimg) {
		int width = changeimg.length;
		float[] diffsum = {0,0,0};
		for(int i =0; i < width*3; i++) {
			int w = i%width;
			int c = i/width;
			diffsum[c]=diffsum[c]+Math.abs(changeimg[w][c]-goalimg[w][c]);
		}
		float diff = ((diffsum[0]/(width))+(diffsum[1]/(width))+(diffsum[2]/(width)))/(3*255);
		
		float result = 1-diff;
		return result;
	}
	
	public float colordiff(int[][]changeimg,int[][]goalimg) {
		int width = changeimg.length;
		int[] sumc= {0,0,0}; int[] sumg = {0,0,0};
		for(int i = 0; i<width*3;i++) {
			int w = i%width;
			int c = i/width;
			sumc[c] = sumc[c]+changeimg[w][c];		
			sumg[c] = sumg[c]+goalimg[w][c];
		}
		float[] avec = {0,0,0};float[] aveg = {0,0,0};
		for(int j=0; j<3; j++) {
			avec[j] = sumc[j]/width;
			aveg[j] = sumg[j]/width;
		}
		float diff = (Math.abs(avec[0]-aveg[0])+Math.abs(avec[1]-aveg[1])+Math.abs(avec[2]-aveg[2]))/3;
		float result = 1-(diff/255);
		return result;
		
		
	}

	public int[][][] tocanny(int[][][] img) {
		return tocanny(img,700,2500);
	}
	
	public int[][][] tocanny(int[][][] imgi,float t_min , float t_max){
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
		//勾配の大きさと角度の算出
		float[][] slimg = new float[sximg.length][sximg.length];
		float[][] siimg = new float[sximg.length][sximg.length];
		for (int i = 0; i < sximg.length*sximg.length ; i++ ) {
			int w = i/sximg.length;
			int h = i%sximg.length;
			slimg[w][h] = (float) Math.sqrt((sximg[w][h]*sximg[w][h])+(syimg[w][h]*syimg[w][h]));
			siimg[w][h] = (float) Math.atan(((sximg[w][h]+0.000001))/((syimg[w][h]+0.000001)));
		}
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
