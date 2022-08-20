package model;

import java.util.ArrayList;

public class javabeans implements java.io.Serializable{
	private String goalimgpath;
	private String startimgpath ;
	private String apppath;
    private ArrayList<String> imgpath = new ArrayList<>();
    private String action = "";
    private String resultpath;
    private String imginpath;
    private String resultfoldername;
    //(2)変数にデータを保存するsetメソッドを宣言します。
    public void setstartimgpath(String startimgpath) {
        this.startimgpath = startimgpath;
    }
    public String getstartimgpath() {
    	return startimgpath;
    }
    public void setgoalimgpath(String startimgpath) {
        this.goalimgpath = startimgpath;
    }
    public String getgoalimgpath() {
    	return goalimgpath;
    }
    public  void setimgpath(ArrayList<String> startimgpath) {
        this.imgpath = startimgpath;
    }
    
    public ArrayList<String> getimgpath() {
        return this.imgpath;
    }
    public void setapppath(String apppath) {
    	this.apppath = apppath;
    }
    public String getapppath() {
    	return this.apppath;
    }
    public void setaction(String action) {
    	this.action = action;
    }
    public String getaction() {
    	return this.action;
    }
    public void setresultpath(String resultpath) {
    	this.resultpath = resultpath;
    }
    public String getresultpath() {
    	return this.resultpath;
    }
    public void setimginpath(String imginpath) {
    	this.imginpath = imginpath;
    }
    public String getimginpath() {
    	return this.imginpath;
    }
    public void setresultfoldername(String resultfoldername) {
    	this.resultfoldername = resultfoldername;
    }
    public String getresultfoldername() {
    	return this.resultfoldername;
    }
}
