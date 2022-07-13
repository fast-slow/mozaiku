#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat May 28 22:44:10 2022

@author: sakamakiharuto
"""
import cv2
import numpy as np
import os
import math
class imgchange:
    size = 400
    def multi(x,y):
        return x*y
    def plus(x,y):
        return x+y
   
    ch2border = 30
   
    def changeavedeviation(self,startpath,goalpath):
        currentpath = str(os.path.dirname(os.path.abspath(__file__)))
        startimg = cv2.imread(startpath)
        goalimg = cv2.imread(goalpath)
        h,w,c = startimg.shape
        smean = [np.average(startimg[:,:,0]),np.average(startimg[:,:,1]),np.average(startimg[:,:,2])]
        gmean = [np.average(goalimg[:][:][0]),np.average(goalimg[:][:][1]),np.average(goalimg[:][:][2])]
        sdeviation = [np.std(startimg[:][:][0]),np.std(startimg[:][:][1]),np.std(startimg[:][:][2])]
        gdeviation = [np.std(goalimg[:,:,0]),np.std(goalimg[:,:,1]),np.std(goalimg[:,:,2])]
        sdiffmean = np.empty((h,w,c))
        sdiffsigma = np.empty((h,w,c))
        prechangeimg = np.empty((h,w,c))
        changeimg = np.empty((h,w,c))
        cv2.imshow("ii",goalimg)
        print(smean)
        print(gmean)
        print(sdeviation)
        print(gdeviation)
        
        #cv2.imwrite(currentpath+'/result/prechangeimg.jpg',prechangeimg)
        for i in range(h*w*c):
            
            
            hi = int((i/w)%h)
            wi = int((i%w)%w)
            ci = int(i/h/w)
            
            sdiffmean[hi][wi][ci] = startimg[hi][wi][ci] - smean[ci]
            sdiffsigma[hi][wi][ci] = sdiffmean[hi][wi][ci]/sdeviation[ci]
            #print(sdiffsigma[hi][wi][ci])
            changeimg[hi][wi][ci] = (sdiffsigma[hi][wi][ci]*gdeviation[ci]*1)+gmean[ci]
    
        cv2.imshow('img',changeimg)
        #print(changeimg)
        cv2.imwrite(currentpath+'/result/changeimg.jpg',changeimg)
        k = cv2.waitKey(10)
        
    def changedeviation2(self,startpath,goalpath):
        size = self.size
        currentpath = str(os.path.dirname(os.path.abspath(__file__)))
        startimg = cv2.imread(startpath)
        goalimg = cv2.imread(goalpath)
        startimg = cv2.resize(startimg,(size,size))
        h,w,c = startimg.shape
        goalimg = cv2.resize(goalimg,(w,h))
        scount=0
        gcount=0
        ch2border = self.ch2border
        startsum = goalsum = startdevi = goaldevi = startsig = [0,0,0]
        sin = []
        gin = []
        #合計値の計算
        for i in range(h*w):
            hi = int((i/w)%h)
            wi = int(i%w)
            
            if not((startimg[hi,wi,0]>255-ch2border and startimg[hi,wi,1]>255-ch2border and startimg[hi,wi,2]>255-ch2border) or (startimg[hi,wi,0]<ch2border and startimg[hi,wi,1]<ch2border and startimg[hi,wi,2]<ch2border)) :
                startsum = [startsum[0]+startimg[hi,wi,0],startsum[1]+startimg[hi,wi,1],startsum[2]+startimg[hi,wi,2]]
                scount = scount+1
                sin.append([hi,wi])
                
            if not((goalimg[hi,wi,0]>255-ch2border and goalimg[hi,wi,1]>255-ch2border and goalimg[hi,wi,2]>255-ch2border) or (goalimg[hi,wi,0]<ch2border and goalimg[hi,wi,1]<ch2border and goalimg[hi,wi,2]<ch2border)) :
                goalsum = [goalsum[0]+goalimg[hi,wi,0],goalsum[1]+goalimg[hi,wi,1],goalsum[2]+goalimg[hi,wi,2]]
                gcount = gcount+1
                gin.append([hi,wi])
        
                
        startave = [startsum[0]/scount,startsum[1]/scount,startsum[2]/scount]
        goalave = [goalsum[0]/gcount,goalsum[1]/gcount,goalsum[2]/gcount]
        
        #標準偏差を求める
        for i in sin:
            hi = i[0]
            wi = i[1]
            startdevi = [startdevi[0]+(startave[0]-startimg[hi,wi,0])**2,startdevi[1]+(startave[1]-startimg[hi,wi,1])**2,startdevi[2]+(startave[2]-startimg[hi,wi,2])**2]
        startdevi = [startdevi[0]/scount,startdevi[1]/scount,startdevi[2]/scount]
        startdevi = [math.sqrt(startdevi[0]),math.sqrt(startdevi[1]),math.sqrt(startdevi[2])]
        for i in gin:
            hi = i[0]
            wi = i[1]
            goaldevi = [goaldevi[0]+((goalave[0]-goalimg[hi,wi,0])**2),goaldevi[1]+((goalave[1]-goalimg[hi,wi,1])**2),goaldevi[2]+((goalave[2]-goalimg[hi,wi,2])**2)]
            
        goaldevi = [goaldevi[0]/gcount,goaldevi[1]/gcount,goaldevi[2]/gcount]
        goaldevi = [math.sqrt(goaldevi[0]),math.sqrt(goaldevi[1]),math.sqrt(goaldevi[2])]
        
        #sigmaと合わせる
        
        for i in sin:
            hi = i[0]
            wi = i[1]
            startimg[hi,wi,0] = np.clip(goalave[0] + (goaldevi[0]*((startimg[hi,wi,0]-startave[0])/startdevi[0])),0,255)
            startimg[hi,wi,1] = np.clip(goalave[1] + (goaldevi[1]*((startimg[hi,wi,1]-startave[1])/startdevi[1])),0,255)
            startimg[hi,wi,2] = np.clip(goalave[2] + (goaldevi[2]*((startimg[hi,wi,2]-startave[2])/startdevi[2])),0,255)
        print(startave)
        print(goalave)
        print(startdevi)
        print(goaldevi)
        print(len(sin))
        print (len(gin))
        changeimg = startimg
        cv2.imshow('img',changeimg)
        cv2.imwrite(currentpath+'/result/changeimg.jpg',changeimg)

        k = cv2.waitKey(10)                           
            
    