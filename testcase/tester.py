order_o = r'"C:\Program Files\JetBrains\IntelliJ IDEA 2019.3.3\jbr\bin\java.exe"' + r" -Dfile.encoding=UTF-8 " + r"-classpath C:\Users\luoyuchu\Desktop\Currrent_Semester\Compile_practicum\lab1\out\production\lab1 " + r'Main ' + r'C:\Users\luoyuchu\Desktop\Currrent_Semester\Compile_practicum\lab1\testcase'

import os
import json

with open("ans.json", "r") as f:
	jj = json.load(f)

for i in jj.items():
	filename = i[0]
	answer = i[1]
	order = order_o + os.sep + filename
	r = os.popen(order) 
	info = r.readlines()
	firstline = info[0].strip('\r\n')
	#if int(info) == answer:
	#	print("Pass!")
	#else:
	#	print("Wrong!  " + filename)
	print(filename, end="  :  ")
	print(info)
