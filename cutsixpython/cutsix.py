#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Oct 12 18:28:06 2022

@author: sakamakiharuto
"""

import cv2
import math

imgsize = 3200;
sidesize = 192;
shape = 6;

im = cv2.imread('img/2.jpg')

img = cv2.resize(im , dsize=(imgsize, imgsize))
resultimg = img;



width = int((imgsize-(sidesize*math.sqrt(3)/2))/(sidesize*math.sqrt(3)))
height = int((imgsize-sidesize*0.5)/(sidesize*1.5))

centerpointw = [[0] * height for i in range(width)] 
centerpointh = [[0] * height for i in range(width)] 

for i in range(width*height):
    w = int(i%width)
    h = int(i/width)
    
    if(h%2==0):
        centerpointw[w][h] = (w*sidesize*math.sqrt(3))+(sidesize*math.sqrt(3)*0.5)
    else:
        centerpointw[w][h] = (w*sidesize*math.sqrt(3))+(sidesize*math.sqrt(3)*1)
     
    centerpointh[w][h] = (h*sidesize*1.5)+(sidesize*1)
                          
                          
    for j in range(shape):
       revolution = j*2
       pi = math.pi
       h1 = centerpointh[w][h]+sidesize*(math.sin(((1/6)+(revolution/shape))*pi))
       h2 = centerpointh[w][h]+sidesize*(math.sin(((1/6)+((revolution+2)/shape))*pi))
       w1 = centerpointw[w][h]+sidesize*(math.cos(((1/6)+(revolution/shape))*pi))
       w2 = centerpointw[w][h]+sidesize*(math.cos(((1/6)+((revolution+2)/shape))*pi))
       img = cv2.line(resultimg,(int(w1),int(h1)),(int(w2),int(h2)),(0,0, 0),1)
    
print("完了")
cv2.imshow("resultimg", resultimg)
cv2.imwrite('result.jpg', resultimg)
cv2.waitKey(5)