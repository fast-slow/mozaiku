# This Python file uses the following encoding: utf-8
#!/usr/bin/env python3
# coding=<encoding name>
"""
Created on Fri May 27 17:35:08 2022
@author: sakamakiharuto
"""
from numba import jit
import mozaiku
import os
import imgchange

@jit
def startup(startimg,goalimg):
    print(startimg);
    mozaiku.mozaiku3(startimg,goalimg)
    
if __name__ == "__main__" :
    currentpath = str(os.path.dirname(os.path.abspath(__file__)))
    parts_image=currentpath+'/image/hugaku.jpg'
    complete_image=currentpath+'/image/venus.jpg'
    mozaiku.mozaiku3(parts_image,complete_image)
    
    
    