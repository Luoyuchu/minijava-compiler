order_o = r'"C:\Program Files\JetBrains\IntelliJ IDEA 2019.3.3\jbr\bin\java.exe"' + r" -Dfile.encoding=UTF-8 " + r"-classpath C:\Users\luoyuchu\Desktop\Currrent_Semester\Compile_practicum\lab1\out\production\lab1 " + r'Main ' + r'C:\Users\luoyuchu\Desktop\Currrent_Semester\Compile_practicum\lab1\testcase'

import os
import json
import tqdm

rf = open("result.txt", "w")

with open("ans.json", "r") as f:
	jj = json.load(f)

lst = list(jj.items())
cnt = 0
for it in tqdm.tqdm(range(len(lst)), ncols=50):
	i = lst[it]
	filename = i[0]
	answer = i[1]
	order = order_o + os.sep + filename
	r = os.popen(order) 
	info = r.readlines()
	firstline = info[0].strip('\r\n')
	if int(firstline) == answer:
		print("Pass!", file=rf)
		cnt = cnt + 1
	else:
		print("Wrong!", file=rf)
	print(filename, end="  :  ", file=rf)
	print(info, file=rf)

rf.close()
print("finished!, Correct:{}/{}".format(cnt, len(lst)))