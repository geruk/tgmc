fr = open("rights.txt", "r")
ft = open("resource/tgmctrain_added.csv", "a")
#ft = open("resource/test.csv", "a")
fe = open("resource/tgmcevaluation.csv","r")

x = []

for line in fr:
        x.append(line.rstrip())

for line in fe:
	id = line[0:6]
	if id in x:
		ft.write(line.rstrip()+",true\n")


