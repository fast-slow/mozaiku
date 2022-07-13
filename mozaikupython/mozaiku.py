# This Python file uses the following encoding: utf-8
#!/usr/bin/env python3
# coding=<encoding name>
""" ##################################################################################################################################################################
任意の二枚の画像を用いて，モザイク画を生成します．
一枚目の画像はモザイク画のピースとなる画像です．
二枚目の画像は完成目標となる画像です．

一枚目の画像を分割し，並び替えることで，モザイクで二枚目の画像を表現します．
################################################################################################################################################################## """
from numba import jit
import serial
import cv2
import imgchange
import random
import numpy as np
import time
import shutil
import os
import subprocess
import hist
import threading
import matplotlib.pyplot as plt
import itertools
#import example
import sys
from operator import itemgetter
from PIL import Image
t1 = time.time() #時間計測開始


"""################################################################################################################################################################## 

変更パラメータ

・使用する画像を指定してください

・使用する画像は400*400ピクセルのものにしてください
　もしくは下記range_で指定してください（この時必ず同じ大きさの2枚の画像を使用してください）
↓画像の指定
##################################################################################################################################################################"""

currentpath = str(os.path.dirname(os.path.abspath(__file__)))
'''
parts_image=currentpath+'/image/pikaso.png'    #パーツとなる元の画像の絶対パス
complete_image=currentpath+'/image/hosizukiyo.png' #完成目標とする画像の絶対パス
'''
box=20##解像度　1~400または1～600 の間の約数を指定してください（400*400ピクセルまたは600*600ピクセルの画像をbox*boxの四角で分割するため）
##400*400ピクセルの場合，20ぐらいがいいと思います．10に指定すると，生成に1時間ほどかかります．
range_=int(400)##使用する画像のピクセル数
################################################################################################################################################################## 

"""
＃Dynamixelを動かす際は以下のコードを使用します．
if os.name == 'nt':
    import msvcrt
    def getch():
        return msvcrt.getch().decode()
else:
    import sys, tty, termios
    fd = sys.stdin.fileno()
    old_settings = termios.tcgetattr(fd)
    def getch():
        try:
            tty.setraw(sys.stdin.fileno())
            ch = sys.stdin.read(1)
        finally:
            termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)
        return ch

from dynamixel_sdk import *                    # Uses Dynamixel SDK library

# Control table address
ADDR_MX_TORQUE_ENABLE      = 24               # Control table address is different in Dynamixel model
ADDR_MX_GOAL_POSITION      = 30
ADDR_MX_PRESENT_POSITION   = 36

# Protocol version
PROTOCOL_VERSION            = 1.0               # See which protocol version is used in the Dynamixel

# Default setting
DXL_ID                      = 9                 # Dynamixel ID : 9
BAUDRATE                    = 1000000             # Dynamixel default baudrate : 57600
DEVICENAME                  = "COM4"    # Check which port is being used on your controller
                                                # ex) Windows: "COM1"   Linux: "/dev/ttyUSB0" Mac: "/dev/tty.usbserial-*"

TORQUE_ENABLE               = 1                 # Value for enabling the torque
TORQUE_DISABLE              = 0                 # Value for disabling the torque
DXL_MINIMUM_POSITION_VALUE  = 0           # Dynamixel will rotate between this value
DXL_MAXIMUM_POSITION_VALUE  = 1023            # and this value (note that the Dynamixel would not move when the position value is out of movable range. Check e-manual about the range of the Dynamixel you use.)
DXL_MOVING_STATUS_THRESHOLD = 20                # Dynamixel moving status threshold

index = 0
dxl_goal_position = [DXL_MINIMUM_POSITION_VALUE, DXL_MAXIMUM_POSITION_VALUE]         # Goal position


# Initialize PortHandler instance
# Set the port path
# Get methods and members of PortHandlerLinux or PortHandlerWindows
portHandler = PortHandler(DEVICENAME)

# Initialize PacketHandler instance
# Set the protocol version
# Get methods and members of Protocol1PacketHandler or Protocol2PacketHandler
packetHandler = PacketHandler(PROTOCOL_VERSION)

# Open port
if portHandler.openPort():
    print("Succeeded to open the port")
else:
    print("Failed to open the port")
    print("Press any key to terminate...")
    getch()
    quit()


# Set port baudrate
if portHandler.setBaudRate(BAUDRATE):
    print("Succeeded to change the baudrate")
else:
    print("Failed to change the baudrate")
    print("Press any key to terminate...")
    getch()
    quit()

# Enable Dynamixel Torque
dxl_comm_result, dxl_error = packetHandler.write1ByteTxRx(portHandler, DXL_ID, ADDR_MX_TORQUE_ENABLE, TORQUE_ENABLE)
if dxl_comm_result != COMM_SUCCESS:
    print("%s" % packetHandler.getTxRxResult(dxl_comm_result))
elif dxl_error != 0:
    print("%s" % packetHandler.getRxPacketError(dxl_error))
else:
    print("Dynamixel has been successfully connected")
"""

""" ##################################################################################################################################################################

初期化

################################################################################################################################################################## """


img=[]
img_temp = []
img_temp2 = []
img2=[]
img_temp3 = []
img3= []
img_temp4 = []
img4= []
img_temp5 = []
img5= []
img_temp6 = []
img6= []


width =0
height=0

#一マスの幅
c1=[]
c2=[]
c3=[]
c4=[]#パーツの位置
tile=[]
temp=[]
dst=[]
ran_box_after=[]#ランダムな数字の中でマッチしたパーツを除いた配列
p1=[0,0]#左上
p2=[box,0]#左下
p3=[0,box]#右上
p4=[box,box]#右下
print(range_)
box_num=int(range_/box)#パーツの数
ALL_box=box_num*box_num
box_count=[[-1]* 4 for i in range (ALL_box)]
box_count_after=[]
parts_box=[]

result_box=[]

def register(path1,path2):
    #file_pass1=currentpath+"/result/ss.jpg"
    global img
    global img_temp 
    global img_temp2 
    global img2
    global img_temp3 
    global img3
    global img_temp4 
    global img4
    global img_temp5 
    global img5
    global img_temp6
    global img6
    global h,w,c
    global IMAGE_WIDTH 
    global IMAGE_HEIGHT
            
    img_temp = cv2.imread(path2)
    img = cv2.resize(img_temp,(range_,range_))
    cv2.imwrite(currentpath+'/result/goal.jpg',img)
    

    img_temp2 = cv2.imread(path1)#画像の設定
    img2 = cv2.resize(img_temp2,(range_,range_))
    cv2.imwrite(currentpath+'/result/start.jpg',img2)
    
    img_temp3 = np.ones((300,300, 3),np.uint8)*255
    img3=img_temp3[0:range_,0:range_]
    
    img_temp4 = np.ones((300,300, 3),np.uint8)*255
    img4=img_temp4[0:range_,0:range_]
    
    img_temp5 = np.ones((300,300, 3),np.uint8)*255
    img5=img_temp5[0:range_,0:range_]
    
    img_temp6 = np.ones((300,300, 3),np.uint8)*255
    img6=img_temp6[0:range_,0:range_]
    h,w,c=img.shape
    IMAGE_WIDTH = h
    IMAGE_HEIGHT = w

def register2(startimg,changeimg,goalimg):
    global img
    global img_temp 
    global img_temp2 
    global img2
    global img_temp3 
    global img3
    global img_temp4 
    global img4
    global img_temp5 
    global img5
    global img_temp6
    global img6
    global h,w,c
    global IMAGE_WIDTH 
    global IMAGE_HEIGHT
            
    img_temp = cv2.imread(startimg)
    #img=img_temp[0:range_,0:range_]
    img = cv2.resize(img_temp,(range_,range_))
    cv2.imwrite(currentpath+'/result/start.jpg',img)
    

    img_temp2 = cv2.imread(changeimg)
    #img2=img_temp2[0:range_,0:range_]#画像の設定
    img2 = cv2.resize(img_temp2,(range_,range_))
    cv2.imwrite(currentpath+'/result/change.jpg',img2)
    
    
    img_temp3 = cv2.imread(goalimg)
    #img3=img_temp3[0:range_,0:range_]
    img3 = cv2.resize(img_temp3,(range_,range_))
    cv2.imwrite(currentpath+'/result/goal.jpg',img3)
    
    img_temp4 = np.ones((300,300, 3),np.uint8)*255
    img4=img_temp4[0:range_,0:range_]
    
    img_temp5 = np.ones((300,300, 3),np.uint8)*255
    img5=img_temp5[0:range_,0:range_]
    
    img_temp6 = np.ones((300,300, 3),np.uint8)*255
    img6=img_temp6[0:range_,0:range_]
    h,w,c=img.shape
    IMAGE_WIDTH = h
    IMAGE_HEIGHT = w
    
    


def reset() :
    '''
    shutil.rmtree(currentpath+'/result/result_parts')
    shutil.rmtree(currentpath+'/result/trim')
    shutil.rmtree(currentpath+'/result/result_1')
    shutil.rmtree(currentpath+'/result/result_2')
    shutil.rmtree(currentpath+'/result/trim_target')
    shutil.rmtree(currentpath+'/result/result_parts2')
    shutil.rmtree(currentpath+'/result/result_mv')
    shutil.rmtree(currentpath+'/result/trim2')
    '''
    shutil.rmtree(currentpath+'/result')

    os.makedirs(currentpath+'/result/result_parts')
    os.makedirs(currentpath+'/result/trim')
    os.makedirs(currentpath+'/result/result_1')
    os.makedirs(currentpath+'/result/result_2')
    os.makedirs(currentpath+'/result/trim_target')
    os.makedirs(currentpath+'/result/result_parts2')
    os.makedirs(currentpath+'/result/result_mv')
    os.makedirs(currentpath+'/result/trim2')

""" ##################################################################################################################################################################

画像の分割

################################################################################################################################################################## """
def im_point(img,p1,p2,p3,p4) :
    
    for j in range(ALL_box):
        c1.append((p1[0],p1[1]))
        c2.append((p2[0],p2[1]))
        c3.append((p3[0],p3[1]))
        c4.append((p4[0],p4[1]))

        p1=p3#右に移動
        p2=p4
        p3=(p3[0],p3[1]+box)
        p4=(p4[0],p4[1]+box)

        if p4[1] > IMAGE_WIDTH:#下に移動
            p1=(p2[0],0)
            p2=(p1[0]+box,0)
            p3=(p1[0],p1[1]+box)
            p4=(p2[0],p2[1]+box)
            if p4[0] > IMAGE_WIDTH:
                p1=(p2[0],0)
                p2=(p1[0]+box,0)
                p3=(p1[0],p1[1]+box)
                p4=(p2[0],p2[1]+box)

                break
    for i in range(len(c1)):
        tile.append((c1[i],c4[i]))
        
        trim(img,i)
        trim_target(img2,i)#トリミング

def im_poimt2(img,p1,p2,p3,p4):

    for j in range(ALL_box):
        c1.append((p1[0],p1[1]))
        c2.append((p2[0],p2[1]))
        c3.append((p3[0],p3[1]))
        c4.append((p4[0],p4[1]))

        p1=p3#右に移動
        p2=p4
        p3=(p3[0],p3[1]+box)
        p4=(p4[0],p4[1]+box)

        if p4[1] > IMAGE_WIDTH:#下に移動
            p1=(p2[0],0)
            p2=(p1[0]+box,0)
            p3=(p1[0],p1[1]+box)
            p4=(p2[0],p2[1]+box)
            if p4[0] > IMAGE_WIDTH:
                p1=(p2[0],0)
                p2=(p1[0]+box,0)
                p3=(p1[0],p1[1]+box)
                p4=(p2[0],p2[1]+box)

                break
    for i in range(len(c1)):
        tile.append((c1[i],c4[i]))
        
        trim(img,i)
        trim_target(img3,i)#トリミング
        trim2(img2,i)
        

def trim(img,i):
    
    dst= img[tile[i][0][0]:tile[i][1][0],tile[i][0][1]:tile[i][1][1],:]
    cv2.imwrite(currentpath+'/result/trim/'+ str(i)+'.jpg',dst)

def trim_target(img,i):
    dst= img[tile[i][0][0]:tile[i][1][0],tile[i][0][1]:tile[i][1][1],:]

    cv2.imwrite(currentpath+'/result/trim_target/'+ str(i)+'.jpg',dst)

def trim2(img,i):
    dst= img[tile[i][0][0]:tile[i][1][0],tile[i][0][1]:tile[i][1][1],:]
    cv2.imwrite(currentpath+'/result/trim2/'+ str(i)+'.jpg',dst)
    

""" ##################################################################################################################################################################

パーツ同士の比較・データの格納
→compare
配置
→draw
再配置
→draw2

################################################################################################################################################################## """
def compare():
    Data=[]#比較結果を格納
    
    for i in range(int(ALL_box)):
        data=[]#一時的に比較結果を格納
        
        
        img1_temp=img[tile[i][0][0]:tile[i][1][0],tile[i][0][1]:tile[i][1][1],:]#完成目標画像のパーツ
        
        for j in range(int(ALL_box)):
            
            img2_temp=img2[tile[j][0][0]:tile[j][1][0],tile[j][0][1]:tile[j][1][1],:]#比較元画像のパーツ
            img2_temp_90 = cv2.rotate(img2_temp, cv2.ROTATE_90_CLOCKWISE)
            img2_temp_180 = cv2.rotate(img2_temp, cv2.ROTATE_180)
            img2_temp_270 = cv2.rotate(img2_temp, cv2.ROTATE_90_COUNTERCLOCKWISE)

            #比較・格納[比較結果,完成目標画像のパーツ番号(i),比較元画像のパーツ番号(j),角度]
            result = hist.hist12(img2_temp, img1_temp)
            data.append([result,i,j,0])
            result_90 = hist.hist12(img2_temp_90, img1_temp)
            data.append([result_90,i,j,90])
            result_180 = hist.hist12(img2_temp_180, img1_temp)
            data.append([result_180,i,j,180])
            result_270 = hist.hist12(img2_temp_270, img1_temp)
            data.append([result_270,i,j,270])
        Data.append(data)
    #D=sorted(Data[i],key=lambda x: x[0])
    print(len(Data[0]))
    return Data

def compare2():
    Data=[]#比較結果を格納
    
    for i in range(int(ALL_box)):
        data=[]#一時的に比較結果を格納
        
        
        img3_temp=img3[tile[i][0][0]:tile[i][1][0],tile[i][0][1]:tile[i][1][1],:]#完成目標画像のパーツ
        
        for j in range(int(ALL_box)):
            
            img2_temp=img2[tile[j][0][0]:tile[j][1][0],tile[j][0][1]:tile[j][1][1],:]#比較元画像のパーツ
            img2_temp_90 = cv2.rotate(img2_temp, cv2.ROTATE_90_CLOCKWISE)
            img2_temp_180 = cv2.rotate(img2_temp, cv2.ROTATE_180)
            img2_temp_270 = cv2.rotate(img2_temp, cv2.ROTATE_90_COUNTERCLOCKWISE)

            #比較・格納[比較結果,完成目標画像のパーツ番号(i),比較元画像のパーツ番号(j),角度]
            result = hist.hist12(img2_temp, img3_temp)
            data.append([result,i,j,0])
            result_90 = hist.hist12(img2_temp_90, img3_temp)
            data.append([result_90,i,j,90])
            result_180 = hist.hist12(img2_temp_180, img3_temp)
            data.append([result_180,i,j,180])
            result_270 = hist.hist12(img2_temp_270, img3_temp)
            data.append([result_270,i,j,270])
        Data.append(data)
    #D=sorted(Data[i],key=lambda x: x[0])
    print(len(Data[0]))
    return Data

def draw():
    Data = []
    Data = compare()
    print(len(Data))
    print("比較完了。配置します。")
    #print(list(itertools.chain.from_iterable(Data)))
    D=[]
    used=[]#配置した比較元画像のパーツ番号
    i=0
    bar_data=[]
    bar_result=[]
    bar_num=[]
    order=0 #配置順
    print(ALL_box)
    while len(used) <= ALL_box:
        D=sorted(Data[i],key=lambda x: x[0])#適切なパーツを先頭に
        f=0
        p=0
        place = D[p][2]#配置したい比較元画像のパーツ番号
        while f == 0:
            if place in used and len(used) < ALL_box:
                #print("既に使ったよ")
                p += 1
                place = D[p][2]
                #print("place = "+ str(place))

            else:
                if len(used) == ALL_box:#最後のパーツの時
                    place = 0
                    

                #print(D[p][1])
                Completed=cv2.imread(currentpath+'/result/trim_target/'+ str(place) +'.jpg')

                if D[p][3] == 0:
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed)
                elif D[p][3] == 90:
                    Completed_90 = cv2.rotate(Completed, cv2.ROTATE_90_CLOCKWISE)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_90)
                elif D[p][3] == 180:
                    Completed_180 = cv2.rotate(Completed, cv2.ROTATE_180)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_180)
                elif D[p][3] == 270:
                    Completed_270 = cv2.rotate(Completed, cv2.ROTATE_90_COUNTERCLOCKWISE)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_270)
                else:
                    print("配置error")

                #最初だけ
                if D[p][1] == 0:
                    used.append(0)
                
                used.append(place)
                
                i = place
                order += 1
                bar_data.append([D[p][0],D[p][1],D[p][2],D[p][3],order])#([result,配置された場所,元々の場所,角度,順番])
                bar_result.append(D[p][0])
                bar_num.append(D[p][1])
                f=1
                
                
                
    else:
        picture_data = sorted(bar_data,key=lambda x: x[1])#完成画像の情報([result,配置された場所,元々の場所,角度,順番] × 分割数)

        #パーツ毎の類似度距離グラフの描画
        fig = plt.figure()
        plt.bar(bar_num,bar_result,align="center")
        plt.xlabel("pixel number")
        plt.ylabel("distance")
        plt.grid(True)
        fig.savefig("bar1.jpg")

        linking(box_num,1)

def draw3():
    Data = []
    Data = compare2()
    print(len(Data))
    print("比較完了。配置します。")
    #print(list(itertools.chain.from_iterable(Data)))
    D=[]
    used=[]#配置した比較元画像のパーツ番号
    i=0
    bar_data=[]
    bar_result=[]
    bar_num=[]
    order=0 #配置順
    print(ALL_box)
    while len(used) <= ALL_box:
        D=sorted(Data[i],key=lambda x: x[0])#適切なパーツを先頭に
        f=0
        p=0
        place = D[p][2]#配置したい比較元画像のパーツ番号
        while f == 0:
            if (place in used and len(used) < ALL_box) or (place == 0 and D[p][1]==0) :
                #print("既に使ったよ")
                p += 1
                place = D[p][2]
                #print("place = "+ str(place))

            else:
                if len(used) == ALL_box:#最後のパーツの時
                    place = 0
                
                
                #print(str(place)+'が'+str(D[p][1]))
                Completed=cv2.imread(currentpath+'/result/trim/'+ str(place) +'.jpg')
                
                if D[p][3] == 0:
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed)
                   
                elif D[p][3] == 90:
                    Completed_90 = cv2.rotate(Completed, cv2.ROTATE_90_CLOCKWISE)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_90)
                elif D[p][3] == 180:
                    Completed_180 = cv2.rotate(Completed, cv2.ROTATE_180)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_180)
                elif D[p][3] == 270:
                    Completed_270 = cv2.rotate(Completed, cv2.ROTATE_90_COUNTERCLOCKWISE)
                    cv2.imwrite(currentpath+'/result/result_parts/'+ str(D[p][1])+'.jpg',Completed_270)
                else:
                    print("配置error")

                #最初だけ
                if D[p][1] == 0:
                    used.append(0)
                else:
                    used.append(place)
                #print(used)
                i = place
                
                order += 1
                bar_data.append([D[p][0],D[p][1],D[p][2],D[p][3],order])#([result,配置された場所,元々の場所,角度,順番])
                bar_result.append(D[p][0])
                bar_num.append(D[p][1])
                f = 1
                
                
                
    else:
        picture_data = sorted(bar_data,key=lambda x: x[1])#完成画像の情報([result,配置された場所,元々の場所,角度,順番] × 分割数)

        #パーツ毎の類似度距離グラフの描画
        fig = plt.figure()
        plt.bar(bar_num,bar_result,align="center")
        plt.xlabel("pixel number")
        plt.ylabel("distance")
        plt.grid(True)
        fig.savefig("bar1.jpg")

        linking(box_num,1)
    
"""
    #配置(パーツ数10*10用)　＊DexArm・Dynamixelを用いる場合は以下の制御を行います．＊
    import example
    parts_data = sorted(picture_data,key=lambda x: x[4])
    print(parts_data)
    print("DexArmを動かします")
    
    #0番のパーツを左にずらす
    
    #example.first_move()

    for num in range(100):
        if num >= 100:
            input_sign = input()
            if input_sign == 1:
                num = num - 1
            else:
                pass
        

            parts_place_x,parts_place_y = piece_place(parts_data[num][2])
            newparts_place_x,newparts_place_y = piece_place(parts_data[num][1])
            #example.fmove(newparts_place_x,newparts_place_y)
            example.move1_dexarm(parts_place_x,parts_place_y)

            if parts_data[num][3] == 90:
                example.table_move1()
                time.sleep(1.5)
                dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 378)
                example.table_move2()
            elif parts_data[num][3] == 180:
                example.table_move1()
                time.sleep(1.5)
                dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 63)
                example.table_move2()
            elif parts_data[num][3] == 270:
                example.table_move1()
                time.sleep(1.5)
                dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 979)
                example.table_move2()
            else:
                pass

            example.move2_dexarm(newparts_place_x,newparts_place_y)
            dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 682)
            print(parts_place_x,parts_place_y,newparts_place_x,newparts_place_y,num)
        else:
            pass

    
    newparts_place_x,newparts_place_y = piece_place(parts_data[98][2])
    #0番のパーツを配置する
    example.last_move1(newparts_place_x,newparts_place_y)
    if parts_data[99][3] == 90:
        example.table_move1()
        time.sleep(1.5)
        dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 378)
        example.table_move2()
    elif parts_data[99][3] == 180:
        example.table_move1()
        time.sleep(1.5)
        dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 63)
        example.table_move2()
    elif parts_data[99][3] == 270:
        example.table_move1()
        time.sleep(1.5)
        dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 979)
        example.table_move2()
    else:
        pass
    
    example.last_move2(newparts_place_x,newparts_place_y)
    dxl_comm_result = packetHandler.write4ByteTxRx(portHandler, DXL_ID, ADDR_MX_GOAL_POSITION, 682)

    return Data,picture_data
"""


#パーツの現実座標決定
def piece_place(parts_place):
    place_x_list = [90,72,54,36,18,0,-18,-36,-54,-72]
    place_y_list = [200,218,236,254,272,290,308,326,344,362]
    parts_place_x_num = parts_place % 10
    parts_place_y_num = parts_place // 10
    parts_place_x = place_x_list[parts_place_x_num]
    parts_place_y = place_y_list[parts_place_y_num]

    return parts_place_x,parts_place_y



#再配置(未完成)
def draw2(border):
    Data,picture_data = draw()
    max_d=sorted(picture_data,reverse=True,key=lambda x: x[0])
    max_distance = max_d[0][0]#最も適していないパーツのresult
    count = 0
    print('max_distance1='+ str(max_distance))

    while max_distance >= int(border):
        max_d=sorted(picture_data,reverse=True,key=lambda x: x[0])
        max_distance = max_d[0][0]#最も適していないパーツのresult
        bef_place = max_d[0][1]#最も適していないパーツの配置されていた場所
        origin_place = max_d[0][2]#最も適していないパーツの元々の場所
        data = list(itertools.chain.from_iterable(Data))

        pix_count = 0
        i = 0
        replaces = []#最も適していないパーツの他場所での比較情報
        while pix_count < int(ALL_box*4):
            if data[i][2] == origin_place:
                replaces.append(data[i])
                pix_count += 1
            else:
                pass
            i += 1
        Replaces = sorted(replaces,key=lambda x: x[0])

        f = 0
        p = 0
        while f == 0:
            replace = Replaces[p]#最も適していないパーツが配置されるべき場所(を含む配列[result,配置されるべき場所,元々の場所,角度])
            #print(replace[0])
            if replace[0] < picture_data[bef_place][0] and replace[0] < picture_data[replace[1]][0]:#入れ替え
                print("入れ替えます")
                    
                j = 0
                k = 0
                while k == 0:
                    if data[j][1] == bef_place and data[j][3] == picture_data[replace[1]][3] and data[j][2] == picture_data[replace[1]][2]:
                        replace_terget = data[j]
                        k = 1
                    else:
                        j += 1
                    
                if replace_terget[0] < picture_data[bef_place][0]:
                    
                    Exchnge = cv2.imread(currentpath+'/result/'+ str(bef_place)+'.jpg')
                    Exchnge_terget = cv2.imread(currentpath+'/result/result_parts/'+ str(replace[1])+'.jpg')
                    
                    cv2.imwrite(currentpath+'/result/'+ str(bef_place)+'.jpg',Exchnge_terget)
                    
                    if replace[3] == 0:
                        cv2.imwrite(currentpath+'/result/'+ str(replace[1])+'.jpg',Exchnge)
                    elif replace[3] == 90:
                        Exchnge_90 = cv2.rotate(Exchnge, cv2.ROTATE_90_CLOCKWISE)
                        cv2.imwrite(currentpath+'/result/'+ str(replace[1])+'.jpg',Exchnge_90)
                    elif replace[3] == 180:
                        Exchnge_180 = cv2.rotate(Exchnge, cv2.ROTATE_180)
                        cv2.imwrite(currentpath+'/result/'+ str(replace[1])+'.jpg',Exchnge_180)
                    elif replace[3] == 270:
                        Exchnge_270 = cv2.rotate(Exchnge, cv2.ROTATE_90_COUNTERCLOCKWISE)
                        cv2.imwrite(currentpath+'/result/'+ str(replace[1])+'.jpg',Exchnge_270)
                    else:
                        print("配置error")
                        

                    print(picture_data[bef_place],picture_data[replace[1]])
                    picture_data[bef_place],picture_data[replace[1]] = replace_terget,replace
                    print(picture_data[bef_place],picture_data[replace[1]])
                    f = 1
                else:
                    p += 1

            else:
                p += 1
                #print(p)

        max_d=sorted(picture_data,reverse=True,key=lambda x: x[0])
        max_distance = max_d[0][0]#最も適していないパーツのresult
        count += 1
        print('max_distance2='+ str(max_distance))

    else:
        print(count)
        bar_num = list(range(len(picture_data)))
        bar_result = []
        for i in range(len(picture_data)):
            bar_result.append(picture_data[i][0])

        #パーツ毎の類似度グラフの描画
        fig = plt.figure()
        plt.bar(bar_num,bar_result,align="center")
        plt.xlabel("pixel number")
        plt.ylabel("distance")
        plt.grid(True)
        fig.savefig("bar2.jpg")




""" ##################################################################################################################################################################

パーツ画像を一枚につなげる

################################################################################################################################################################## """

def linking(box_num,z):#結果の画像を繋げ合わせて一枚に
    
    for i in range(box_num):
        w=i*(box_num)
        for j in range(box_num):
            if j==0:
                img_1=cv2.imread(currentpath+'/result/result_parts/'+ str(j+w)+'.jpg')
                
                cv2.imwrite(currentpath+'/result/result_parts/fin_h'+ str(j+w)+'.jpg',img_1)

            else:
                img_2=cv2.imread(currentpath+'/result/result_parts/'+ str(j+w)+'.jpg')
                link_img_w=cv2.hconcat([img_1,img_2])
                
                
                img_1=link_img_w
                link_img_h=img_1
                cv2.imwrite(currentpath+'/result/result_parts/fin_h'+ str(j+w)+'.jpg',link_img_h)

                if j==(box_num-1):
                    cv2.imwrite(currentpath+'/result/result_parts/fin_w'+ str(i)+'.jpg',link_img_h)

        if i==0:
            cv2.imwrite(currentpath+'/result/fin' + str(z) + '.jpg',link_img_h)

        else :
            link_img=cv2.imread(currentpath+'/result/fin' + str(z) + '.jpg')
            
            link_img_h=cv2.imread(currentpath+'/result/result_parts/fin_w'+ str(i)+'.jpg')
            link_img= cv2.vconcat([link_img,link_img_h])
            
            cv2.imwrite(currentpath+'/result/fin' + str(z) + '.jpg',link_img)



def mozaiku(startpath,goalpath):
        
        reset()#各ファイルの初期化
        register(startpath,goalpath)
        print("お待ちください")
        im_point(img,p1,p2,p3,p4)#マスの座標取得
        draw()#比較＆振り分け
        linking(box_num,2)#結果の結合
        tempp=sorted(box_count,key=itemgetter(1))
        t2 = time.time()
        elapsed_time = t2-t1
        #print(f"経過時間：{elapsed_time}")#時間の計測
        fin=cv2.imread(currentpath+'/result/fin2.jpg')
        
        startimg = cv2.imread(currentpath+'/result/start.jpg')
        goalimg = cv2.imread(currentpath+'/result/goal.jpg')
        sth,stw,stc = startimg.shape
        fin = cv2.resize(fin,(sth,stw))
        im_h = cv2.hconcat([startimg, goalimg])
        im_h = cv2.hconcat([im_h, fin])
        cv2.imwrite(currentpath+'/result/compare.jpg',im_h)
        path = os.getcwd()
        files = os.listdir(currentpath+'/result/result_1')
        count = len(files)
        files2 = os.listdir(currentpath+'/result/result_2')
        count2 = len(files2)
        print(count)
        #portHandler.closePort()
        #cv2.imshow('frame',frame)
        
        k = cv2.waitKey(100)
        
def mozaiku2(startpath,goalpath):
    reset()#各ファイルの初期化
    imgc = imgchange.imgchange()
    imgc.changeavedeviation(startpath,goalpath)
    register2(startpath,currentpath+'/result/changeimg.jpg',goalpath)
    print("画像取得完了，お待ちください")
    im_point(img,p1,p2,p3,p4)#マスの座標取得
    draw3()#比較＆振り分け
    linking(box_num,2)#結果の結合
    tempp=sorted(box_count,key=itemgetter(1))
    t2 = time.time()
    elapsed_time = t2-t1
    #print(f"経過時間：{elapsed_time}")#時間の計測
    fin=cv2.imread(currentpath+'/result/fin2.jpg')
    startimg = cv2.imread(currentpath+'/result/start.jpg')
    goalimg = cv2.imread(currentpath+'/result/goal.jpg')
    changeimg = cv2.cv2.imread(currentpath+'/result/changeimg.jpg')
    sth,stw,stc = startimg.shape
    changeimg = cv2.resize(changeimg,(sth,stw))
    im_h = cv2.hconcat([startimg,changeimg])
    im_h = cv2.hconcat([im_h, goalimg])
    im_h = cv2.hconcat([im_h, fin])
    
    cv2.imwrite(currentpath+'/result/compare.jpg',im_h)
    path = os.getcwd()
    files = os.listdir(currentpath+'/result/result_1')
    count = len(files)
    files2 = os.listdir(currentpath+'/result/result_2')
    count2 = len(files2)
    print(count)
    #portHandler.closePort()
    #cv2.imshow('frame',frame)
    
    k = cv2.waitKey(100)

def mozaiku3(startpath,goalpath):
    reset()#各ファイルの初期化
    imgc = imgchange.imgchange()
    imgc.changedeviation2(startpath,goalpath)
    register2(startpath,currentpath+'/result/changeimg.jpg',goalpath)
    print("画像取得完了，お待ちください")
    im_point(img,p1,p2,p3,p4)#マスの座標取得
    draw3()#比較＆振り分け
    linking(box_num,2)#結果の結合
    tempp=sorted(box_count,key=itemgetter(1))
    t2 = time.time()
    elapsed_time = t2-t1
    #print(f"経過時間：{elapsed_time}")#時間の計測
    fin=cv2.imread(currentpath+'/result/fin2.jpg')
    startimg = cv2.imread(currentpath+'/result/start.jpg')
    goalimg = cv2.imread(currentpath+'/result/goal.jpg')
    changeimg = cv2.imread(currentpath+'/result/changeimg.jpg')
    sth,stw,stc = startimg.shape
    changeimg = cv2.resize(changeimg,(sth,stw))
    im_h = cv2.hconcat([startimg,changeimg])
    im_h = cv2.hconcat([im_h, goalimg])
    im_h = cv2.hconcat([im_h, fin])
    
    cv2.imwrite(currentpath+'/result/compare.jpg',im_h)
    path = os.getcwd()
    files = os.listdir(currentpath+'/result/result_1')
    count = len(files)
    files2 = os.listdir(currentpath+'/result/result_2')
    count2 = len(files2)
    print(count)
    #portHandler.closePort()
    #cv2.imshow('frame',frame)
    
    k = cv2.waitKey(100)
'''
if __name__ == "__main__" :
    
    
    
    register(parts_image,complete_image)
    reset()#各ファイルの初期化
    
    print("お待ちください")
    print(img)
    im_point(img,p1,p2,p3,p4)#マスの座標取得
    draw()#比較＆振り分け
    linking(box_num,2)#結果の結合
    tempp=sorted(box_count,key=itemgetter(1))
    print(tempp)

    t2 = time.time()
    elapsed_time = t2-t1


    print(f"経過時間：{elapsed_time}")#時間の計測
    fin=cv2.imread(currentpath+'/result/fin2.jpg')
    startimg = cv2.imread(currentpath+'/result/Start.jpg')
    goalimg = cv2.imread(currentpath+'/result/Goal.jpg')
    im_h = cv2.hconcat([startimg, goalimg])
    
    im_h = cv2.hconcat([im_h, fin])
    cv2.imwrite(currentpath+'/result/compare.jpg',im_h)
    path = os.getcwd()

    files = os.listdir(currentpath+'/result/result_1')
    count = len(files)

    files2 = os.listdir(currentpath+'/result/result_2')
    count2 = len(files2)

    
    print(count)

    
    #portHandler.closePort()





    #cv2.imshow('frame',frame)
    
    k = cv2.waitKey(100)
    #cv2.destroyAllWindows()
    
'''
