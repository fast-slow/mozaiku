#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Oct  4 12:11:30 2019

@author: yamazoetatsuhiro
"""

from numba import jit
from PIL import Image
import cv2  #OpenCVのインポート
import imagehash
import math
import numpy
import scipy
import scipy.fftpack
import numpy as np #matplotlib.pyplotのインポート
import sys
range_=400

img1= cv2.imread('C:\\Users\\taise\\Desktop\\compair\\camera_capture_3.jpg')
img2= cv2.imread('C:\\Users\\taise\\Desktop\\compair\\camera_capture_3.jpg')

def hist(img1,img2):
    scores,scores2,scores3 ,hists1, hists2 = [], [], [],[],[]

    img_g_=comvert(img1)
    img2_g_=comvert(img2)
    
    target_hash = imagehash.dhash(img_g_,8)
    hash = imagehash.dhash(img2_g_,8)
    
    score = target_hash - hash
    
    ch_names = {0: 'B', 1: 'R', 2: 'G'}
    
        # HSV に変換する。
    hsv1 = cv2.cvtColor(img1, cv2.COLOR_BGR2HSV)
    hsv2 = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)
    
        # 各チャンネルごとにヒストグラムの類似度を算出する。
    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result=abs(img1_-img2_)
    score2=np.mean(result)

    for ch in ch_names:
        h1 = cv2.calcHist([hsv1], [ch], None, histSize=[256], ranges=[0, 256])
        h2 = cv2.calcHist([hsv2], [ch], None, histSize=[256], ranges=[0, 256])
        #score2 = abs(cv2.compareHist(h1, h2, cv2.HISTCMP_CORREL )-1)
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_CHISQR_ALT)*0.00001
        score3 = cv2.compareHist(h1, h2, cv2.HISTCMP_HELLINGER )
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_BHATTACHARYYA)
        #score = cv2.compareHist(h1, h2, cv2.HISTCMP_INTERSECT)
        
        h1_ = img1.T[ch].flatten().mean()#各RGBの値の平均
        
        h2_ = img2.T[ch].flatten().mean()
        
       
        #result=abs(h1_ - h2_)
        #score2=np.mean(result)
        
        
       
        
        hists1.append(h1)
        hists2.append(h2)
        scores.append(score)
        scores2.append(score2)
        scores3.append(score3)
        
     
    mean1 = np.mean(scores)*0.1
    mean2= np.mean(scores2)*0.01
    
    mean3= np.mean(scores3)*0.1
    
    #print('hash=',mean1,'RGB=',mean2,'hist=',mean3)    
    mean =(mean1+mean2+mean3)/3
  
    
    
    
#    print('hash=',mean1,'hist=',mean2)    
#    fig, axes = plt.subplots(2, 2, figsize=(8, 8))
#    for [axL, axR], hist, img in zip(axes, [hists1, hists2], [img1, img2]):
#            # 画像を描画する。
#        axL.imshow(img[..., ::-1])
#        axL.axis('off')
#            # ヒストグラムを作成する。
#        for i in range(3):
#            axR.plot(hist[i], label=ch_names[i])
#            axR.legend()
#    fig.suptitle('similarity={:.5f}'.format(mean))
#    plt.show()
#    
    return mean


def hist2(img1,img2):
    
    scores,scores2,scores3 ,hists1, hists2 = [], [], [],[],[]

    img_g_=comvert(img1)
 #   img_g=dhash(img_g_, hash_size=8)
    img2_g_=comvert(img2)
    
    
    target_hash = imagehash.dhash(img_g_,8)
    hash = imagehash.dhash(img2_g_,8)
    
    score = target_hash - hash
    
   
    
    ch_names = {0: 'B', 1: 'R', 2: 'G'}
    
        # HSV に変換する。
    hsv1 = cv2.cvtColor(img1, cv2.COLOR_BGR2HSV)
    hsv2 = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)
    
        # 各チャンネルごとにヒストグラムの類似度を算出する。
   
    for ch in ch_names:
        h1 = cv2.calcHist([hsv1], [ch], None, histSize=[256], ranges=[0, 256])
        h2 = cv2.calcHist([hsv2], [ch], None, histSize=[256], ranges=[0, 256])
        #score2 = abs(cv2.compareHist(h1, h2, cv2.HISTCMP_CORREL )-1)
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_CHISQR_ALT)*0.00001
        score3 = cv2.compareHist(h1, h2, cv2.HISTCMP_HELLINGER )
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_BHATTACHARYYA)
        #score = cv2.compareHist(h1, h2, cv2.HISTCMP_INTERSECT)
        
        h1_ = img1.T[ch].flatten().mean()#各RGBの値の平均
        
        h2_ = img2.T[ch].flatten().mean()
        
        #result= img1-img2
        #print(result)
        
        result=abs(h1_ - h2_)
        score2=np.mean(result)
        if ch ==0 :#色相（H）
            dh = min(abs(h2_-h1_), 360-abs(h2_-h1_)) / 180.0
        
        if ch ==1 :#彩度（S）    
            ds = abs(h2_-h1_)
        
        if ch== 2:#明度（V）
            dv = abs(h2_-h1_) / 255.0
        
        hists1.append(h1)
        hists2.append(h2)
        scores.append(score)
        scores2.append(score2)
        scores3.append(score3)
    distance = math.sqrt(dh*dh+ds*ds+dv*dv)
    distance=distance * 0.0
    #print('a',np.mean(hists1),np.mean(hists2)) 
    mean1 = np.mean(scores)*0.01
    mean2= np.mean(scores2)*0.01
    mean3= np.mean(scores3)*1
    
    #print(mean1,mean2,mean3,distance)
    mean =(mean1+mean2+mean3+distance)/3
  
    #print(mean)
    
#    
#    print('hash=',mean1,'hist=',mean2)    
#    fig, axes = plt.subplots(2, 2, figsize=(8, 8))
#    for [axL, axR], hist, img in zip(axes, [hists1, hists2], [img1, img2]):
#            # 画像を描画する。
#        axL.imshow(img[..., ::-1])
#        axL.axis('off')
#            # ヒストグラムを作成する。
#        for i in range(3):
#            axR.plot(hist[i], label=ch_names[i])
#            axR.legend()
#    fig.suptitle('similarity={:.5f}'.format(mean))
#    plt.show()
#    
    return mean

def hist3(img1,img2):    
    scores,scores2,scores3 ,hists1, hists2 = [], [], [],[],[]

    img_g_=comvert(img1)
    img2_g_=comvert(img2)
    
    target_hash = imagehash.dhash(img_g_,8)
    hash = imagehash.dhash(img2_g_,8)
    
    score = target_hash - hash
    
    ch_names = {0: 'B', 1: 'R', 2: 'G'}
    
        # HSV に変換する。
    hsv1 = cv2.cvtColor(img1, cv2.COLOR_BGR2HSV)
    hsv2 = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)
    
        # 各チャンネルごとにヒストグラムの類似度を算出する。
    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result=abs(img1_-img2_)
    score2=np.mean(result)

    for ch in ch_names:
        h1 = cv2.calcHist([hsv1], [ch], None, histSize=[256], ranges=[0, 256])
        h2 = cv2.calcHist([hsv2], [ch], None, histSize=[256], ranges=[0, 256])
        #score2 = abs(cv2.compareHist(h1, h2, cv2.HISTCMP_CORREL )-1)
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_CHISQR_ALT)*0.00001
        score3 = cv2.compareHist(h1, h2, cv2.HISTCMP_HELLINGER )
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_BHATTACHARYYA)
        #score = cv2.compareHist(h1, h2, cv2.HISTCMP_INTERSECT)
        
        h1_ = img1.T[ch].flatten().mean()#各RGBの値の平均
        
        h2_ = img2.T[ch].flatten().mean()
        
       
        #result=abs(h1_ - h2_)
        #score2=np.mean(result)
        
        
       
        
        hists1.append(h1)
        hists2.append(h2)
        scores.append(score)
        scores2.append(score2)
        scores3.append(score3)
        
     
    mean1 = np.mean(scores)*1
    mean2= np.mean(scores2)*1
    
    mean3= np.mean(scores3)*0.1
    
    #print('hash=',mean1,'RGB=',mean2,'hist=',mean3) 
    mean =(mean1+mean2+mean3)/3
  
    
  
    return mean



def hist4(img1,img2):    
    scores,scores2,scores3 ,hists1, hists2 = [], [], [],[],[]

    img_g_=comvert(img1)
    img2_g_=comvert(img2)
    
    target_hash = imagehash.dhash(img_g_,8)
    hash = imagehash.dhash(img2_g_,8)
    
    score = target_hash - hash
    
    ch_names = {0: 'B', 1: 'R', 2: 'G'}
    
        # HSV に変換する。
    hsv1 = cv2.cvtColor(img1, cv2.COLOR_BGR2HSV)
    hsv2 = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)
    
        # 各チャンネルごとにヒストグラムの類似度を算出する。
    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result=abs(img1_-img2_)
    score2=np.mean(result)

    for ch in ch_names:
        h1 = cv2.calcHist([hsv1], [ch], None, histSize=[256], ranges=[0, 256])
        h2 = cv2.calcHist([hsv2], [ch], None, histSize=[256], ranges=[0, 256])
        #score2 = abs(cv2.compareHist(h1, h2, cv2.HISTCMP_CORREL )-1)
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_CHISQR_ALT)*0.00001
        score3 = cv2.compareHist(h1, h2, cv2.HISTCMP_HELLINGER )
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_BHATTACHARYYA)
        #score = cv2.compareHist(h1, h2, cv2.HISTCMP_INTERSECT)
        
        h1_ = img1.T[ch].flatten().mean()#各RGBの値の平均
        
        h2_ = img2.T[ch].flatten().mean()
        
       
        #result=abs(h1_ - h2_)
        #score2=np.mean(result)
        
        
       
        
        hists1.append(h1)
        hists2.append(h2)
        scores.append(score)
        scores2.append(score2)
        scores3.append(score3)
        
     
    mean1 = np.mean(scores)*0
    mean2= np.mean(scores2)*1
    
    mean3= np.mean(scores3)*0
    
    #print('hash=',mean1,'RGB=',mean2,'hist=',mean3)    
    mean =(mean1+mean2+mean3)/3
  
    
  
    return mean


#Hash値

def hist5(img1,img2):
    img_g_=comvert(img1)
    img2_g_=comvert(img2)
    
    
    target_hash = imagehash.dhash(img_g_,8)
    hash = imagehash.dhash(img2_g_,8)
    
    score = target_hash - hash
    mean = np.mean(score)
    return mean

#画像の差分

def hist6(img1,img2):
    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result=abs(img1_-img2_)
    score1=np.mean(result)
    mean = np.mean(score1)

    return mean

#へリンガー

def hist7(img1,img2):
    scores3 = []
    ch_names = {0: 'B', 1: 'R', 2: 'G'}
    
        # HSV に変換する。
    hsv1 = cv2.cvtColor(img1, cv2.COLOR_BGR2HSV)
    hsv2 = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)

    for ch in ch_names:
        h1 = cv2.calcHist([hsv1], [ch], None, histSize=[256], ranges=[0, 256])
        h2 = cv2.calcHist([hsv2], [ch], None, histSize=[256], ranges=[0, 256])
        #score2 = abs(cv2.compareHist(h1, h2, cv2.HISTCMP_CORREL )-1)
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_CHISQR_ALT)*0.00001
        score3 = cv2.compareHist(h1, h2, cv2.HISTCMP_HELLINGER )
        #score2 = cv2.compareHist(h1, h2, cv2.HISTCMP_BHATTACHARYYA)
        #score = cv2.compareHist(h1, h2, cv2.HISTCMP_INTERSECT)
        
        h1_ = img1.T[ch].flatten().mean()#各RGBの値の平均
        
        h2_ = img2.T[ch].flatten().mean()
        
        #result= img1-img2
        #print(result)
        
        if ch ==0 :#色相（H）
            dh = min(abs(h2_-h1_), 360-abs(h2_-h1_)) / 180.0
        
        if ch ==1 :#彩度（S）    
            ds = abs(h2_-h1_)
        
        if ch== 2:#明度（V）
            dv = abs(h2_-h1_) / 255.0
        
        scores3.append(score3)
    distance = math.sqrt(dh*dh+ds*ds+dv*dv)
    distance=distance * 0.0
    #print('a',np.mean(hists1),np.mean(hists2)) 
    mean= np.mean(scores3)*1
    return mean

#エッジ画像の差分

def hist8(img1,img2):
    img3 = cv2.cvtColor(img1,cv2.COLOR_BGR2GRAY)
    img4 = cv2.cvtColor(img2,cv2.COLOR_BGR2GRAY)
    edges1 = cv2.Canny(img3,0,50)
    edges2 = cv2.Canny(img4,0,5)

    result=abs(edges1-edges2)
    score1=np.mean(result)
    mean = np.mean(score1)

    return mean

#エッジ画像の差分＋画像の差分

def hist9(img1,img2):
    img3 = cv2.cvtColor(img1,cv2.COLOR_BGR2GRAY)
    img4 = cv2.cvtColor(img2,cv2.COLOR_BGR2GRAY)
    edges1 = cv2.Canny(img3,30,80)
    edges2 = cv2.Canny(img4,0,20)

    result1=abs(edges1-edges2)
    score1=np.mean(result1)
    mean1 = np.mean(score1)


    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result2=abs(img1_-img2_)
    score2=np.mean(result2)
    mean2 = np.mean(score2)

    mean =(mean1+mean2)/2    
    return mean


def hist10(img1,img2):#ヒストグラム(濃淡)
    img3 = cv2.cvtColor(img1,cv2.COLOR_BGR2GRAY)
    img4 = cv2.cvtColor(img2,cv2.COLOR_BGR2GRAY)

    hist1 = cv2.calcHist([img3],[0],None,[256],[0,256])
    hist2 = cv2.calcHist([img4],[0],None,[256],[0,256])

    mean = cv2.compareHist(hist1, hist2, cv2.HISTCMP_BHATTACHARYYA)

    return mean


def hist11(img1,img2):#ヒストグラム(カラー)
    hist1_B = cv2.calcHist([img1],[0],None,[256],[0,256])
    hist2_B = cv2.calcHist([img2],[0],None,[256],[0,256])
    score_B = cv2.compareHist(hist1_B, hist2_B, cv2.HISTCMP_BHATTACHARYYA)

    hist1_G = cv2.calcHist([img1],[1],None,[256],[0,256])
    hist2_G = cv2.calcHist([img2],[1],None,[256],[0,256])
    score_G = cv2.compareHist(hist1_G, hist2_G, cv2.HISTCMP_BHATTACHARYYA)

    hist1_R = cv2.calcHist([img1],[2],None,[256],[0,256])
    hist2_R = cv2.calcHist([img2],[2],None,[256],[0,256])
    score_R = cv2.compareHist(hist1_R, hist2_R, cv2.HISTCMP_BHATTACHARYYA)

    mean = (score_B + score_G + score_R) / 3

    return mean


def hist12(img1,img2):#エッジ画像の差分＋画像の差分＋ヒストグラム(カラー)
    mean1weight = 1
    mean2weight = 2
    mean3weight = 1
    img3 = cv2.cvtColor(img1,cv2.COLOR_BGR2GRAY)
    img4 = cv2.cvtColor(img2,cv2.COLOR_BGR2GRAY)
    edges1 = cv2.Canny(img3,40,150)
    edges2 = cv2.Canny(img4,5,40)

    result1=abs(edges1-edges2)
    score1=np.mean(result1)
    mean1 = np.mean(score1)


    img1_ = img1.astype(int)
    img2_ = img2.astype(int)
    
    result2=abs(img1_-img2_)
    score2=np.mean(result2)
    mean2 = np.mean(score2)

    hist1_B = cv2.calcHist([img1],[0],None,[256],[0,256])
    hist2_B = cv2.calcHist([img2],[0],None,[256],[0,256])
    score_B = cv2.compareHist(hist1_B, hist2_B, cv2.HISTCMP_BHATTACHARYYA)

    hist1_G = cv2.calcHist([img1],[1],None,[256],[0,256])
    hist2_G = cv2.calcHist([img2],[1],None,[256],[0,256])
    score_G = cv2.compareHist(hist1_G, hist2_G, cv2.HISTCMP_BHATTACHARYYA)

    hist1_R = cv2.calcHist([img1],[2],None,[256],[0,256])
    hist2_R = cv2.calcHist([img2],[2],None,[256],[0,256])
    score_R = cv2.compareHist(hist1_R, hist2_R, cv2.HISTCMP_BHATTACHARYYA)

    mean3 = (score_B + score_G + score_R) / 3

    mean = (mean1*mean1weight + mean2*mean2weight + mean3*mean3weight) / (mean1weight+mean2weight+mean3weight)   
    
    return mean


def comvert(img1):
    new_image = img1.copy()
    if new_image.ndim == 2:  # モノクロ
        pass
    elif new_image.shape[2] == 3:  # カラー
        new_image = cv2.cvtColor(new_image, cv2.COLOR_BGR2RGB)
    elif new_image.shape[2] == 4:  # 透過
        new_image = cv2.cvtColor(new_image, cv2.COLOR_BGRA2RGBA)
    img_g_ = Image.fromarray(new_image)
    return img_g_
    

#hist9(img1,img2)