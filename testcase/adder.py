import os
import json
import re
import shutil
import numpy as np

dict = {}
filelist = os.listdir('./tmp')
for i in filelist:
	shutil.copyfile('./tmp/' + i, i)
	if i.find("error") == -1:
		dict[i] = 1
	else:
		dict[i] = 0

print(dict)
with open("ans.json", "r") as f:
	jj = json.load(f)

jj.update(dict)

with open("ans.json", "w") as f:
	json.dump(jj, f)