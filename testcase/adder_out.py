import os
import json
import re
import shutil
import numpy as np

id_start = 0
dict = {}
namedict = {}

filelist = os.listdir('./tmp')

filenamelist = [(i.split('.'))[0] for i in filelist]
filenamelist = np.unique(filenamelist)
filenamelist = filenamelist.tolist()
index = id_start
for i in filenamelist:
	namedict[i] = str(index)
	dict[str(index)] = 0
	index += 1

for i in filelist:
	pp = i.split('.')
	if len(pp) == 1:
		pp.append("")
	else:
		with open('./tmp/' + i, "r") as f:
			content = f.read()
			if content.find("success") != -1:
				dict[namedict[pp[0]]] = 1
		pp[1] = '.' + pp[1]
	shutil.copyfile('./tmp/' + i, namedict[pp[0]] + pp[1])


with open("ans.json", "r") as f:
	jj = json.load(f)

jj.update(dict)

with open("ans.json", "w") as f:
	json.dump(jj, f)