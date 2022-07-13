from pydexarm import Dexarm
import time
'''windows'''
#dexarm = Dexarm("COM5")
'''mac & linux'''
dexarm = Dexarm("/dev/tty.usbmodem3086337A34381")

if platform.system() = 'Darwin' or platform.system() = 'Linux':
    dexarm = Dexarm("/dev/tty.usbmodem3086337A34381")
else if platform.system() = 'Windows':
    dexarm = Dexarm("COM5")

#パーツの座標決定
def piece_place(parts_place):
    place_x_list = [90,72,54,36,18,0,-18,-36,-54,-72]
    place_y_list = [200,218,236,254,272,290,308,326,344,362]
    parts_place_x_num = parts_place % 10
    parts_place_y_num = parts_place // 10
    parts_place_x = place_x_list[parts_place_x_num]
    parts_place_y = place_y_list[parts_place_y_num]

    return parts_place_x,parts_place_y

#初動
def first_move():
    #0番のパーツを左にずらす
    #dexarm.go_home()
    dexarm.fast_move_to(90, 200, 30)
    dexarm.air_picker_pick()
    dexarm.fast_move_to(90, 200, 0)
    dexarm.fast_move_to(90, 200, 30)
    dexarm.fast_move_to(108, 200, 30)
    dexarm.fast_move_to(108, 200, 0)
    dexarm.air_picker_place()
    dexarm.air_picker_nature()
    dexarm.fast_move_to(108, 200, 30)

#パーツの配置(掴む)
def move1_dexarm(parts_place_x,parts_place_y):

    dexarm.air_picker_pick()
    dexarm.fast_move_to(parts_place_x, parts_place_y, 30)
    dexarm.fast_move_to(parts_place_x, parts_place_y, 0)
    dexarm.fast_move_to(parts_place_x, parts_place_y, 30)

#パーツの配置(置く)
def move2_dexarm(newparts_place_x,newparts_place_y):

    dexarm.fast_move_to(newparts_place_x, newparts_place_y, 30)
    dexarm.fast_move_to(newparts_place_x, newparts_place_y, 0)
    dexarm.air_picker_place()
    dexarm.air_picker_nature()
    dexarm.fast_move_to(newparts_place_x, newparts_place_y, 30)

#最後の配置(掴む)
def last_move1(newparts_place_x,newparts_place_y):
    #0番のパーツを配置する
    dexarm.air_picker_pick()
    dexarm.fast_move_to(108, 200, 30)
    dexarm.fast_move_to(108, 200, 0)
    dexarm.fast_move_to(108, 200, 30)

#最後の配置(置く)
def last_move2(newparts_place_x,newparts_place_y):

    dexarm.fast_move_to(-18,344, 30)
    dexarm.fast_move_to(-18,344, 0)
    dexarm.air_picker_place()
    dexarm.air_picker_nature()
    dexarm.fast_move_to(-18,344, 30)
    dexarm.fast_move_to(-120, 200, 30)

#ターンテーブルへ(置く)
def table_move1():

    dexarm.fast_move_to(132,236, 30)
    dexarm.fast_move_to(132,236, 15)
    dexarm.air_picker_place()
    dexarm.air_picker_nature()
    dexarm.fast_move_to(132,236, 30)

#ターンテーブルへ(掴む)
def table_move2():

    dexarm.air_picker_pick()
    dexarm.fast_move_to(132,236, 5)
    dexarm.fast_move_to(132,236, 30)

"""
y=362

dexarm.fast_move_to(90, y, 13)
time.sleep(10)
dexarm.fast_move_to(72, y, 13)
time.sleep(7)
dexarm.fast_move_to(54, y, 13)
time.sleep(7)
dexarm.fast_move_to(36, y, 13)
time.sleep(7)
dexarm.fast_move_to(18, y, 13)
time.sleep(7)
dexarm.fast_move_to(0, y, 13)
time.sleep(7)
dexarm.fast_move_to(-18, y, 13)
time.sleep(7)
dexarm.fast_move_to(-36, y, 13)
time.sleep(7)
dexarm.fast_move_to(-54, y, 13)
time.sleep(7)
dexarm.fast_move_to(-72, y, 13)
"""
#dexarm.fast_move_to(-36, 272, 30)
#dexarm.fast_move_to(-132, 23, 20)
def fmove(newparts_place_x,newparts_place_y):

    dexarm.fast_move_to(newparts_place_x,newparts_place_y, 30)

#dexarm.air_picker_nature()