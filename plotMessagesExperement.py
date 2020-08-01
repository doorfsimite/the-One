import matplotlib.pyplot as plt
import numpy as np
import re 


def SelfishNodesProportion(path):
    arq = open(path)
    text = arq.readlines()
    return re.split(r' ',text[0])[2]
    

def hostsSize(path):
    arq = open(path)
    text = arq.readlines()
    return int(re.split(r' ',text[1])[1])
    


def normalConsumedEnergy(path,selfishLevel,fileName):
    meanTotalNormalConsume = []
    notFirst = False
    for run in range(0,3):
        arq = open(path+str(run)+"/"+str(selfishLevel)+"/"+fileName)
        text = arq.readlines()
        consume = []
        if(text[2] == "No egoist Host Data\n"):
            for i in range(3,6):
                linha = re.split(r' ',text[i])
                consume.append(float(linha[-1][:-1]))
        else:
            for i in range(5,8):
                linha = re.split(r' ',text[i])
                consume.append(float(linha[-1][:-1]))
                if(linha[-1][:-1] == "NaN"):
                    return [0,0,0]
        if (notFirst):
            for i in range(0,len(meanTotalNormalConsume)):
                meanTotalNormalConsume[i] += consume[i]
        else:
            meanTotalNormalConsume = consume
            notFirst = True
    for i in range(0,len(meanTotalNormalConsume)):
        meanTotalNormalConsume[i] = meanTotalNormalConsume[i]/3
    return meanTotalNormalConsume
        


def selfishConsumedEnergy(path,selfishLevel,fileName):
    meanTotalSelfishConsume = []
    notFirst = False
    for run in range(0,3):
        arq = open(path+str(run)+"/"+str(selfishLevel)+"/"+fileName)
        text = arq.readlines()
        consume = []
        if(text[2] == "No egoist Host Data\n"):
            return [0,0,0]
        else:
            for i in range(2,5):
                linha = re.split(r' ',text[i])
                consume.append(float(linha[-1][:-1]))
        if(notFirst):
            for i in range(0,len(meanTotalSelfishConsume)):
                meanTotalSelfishConsume[i] += consume[i]
        else:
            meanTotalSelfishConsume = consume
            notFirst = True
    for i in range(0,len(meanTotalSelfishConsume)):
        meanTotalSelfishConsume[i] = meanTotalSelfishConsume[i]/3
    return meanTotalSelfishConsume
    

def getHostConsume(path,selfishLevel,fileName):
    totalConsume = []
    notFirst = False
    for run in range(0,3):
        arq = open(path+str(run)+"/"+str(selfishLevel)+"/"+fileName)
        text = arq.readlines()
        consume = []
        if(text[2] == "No egoist Host Data\n"):
            i = 12
            while(i < len(text)):
                linha = re.split(r' ',text[i])
                linha[-1] = linha[-1][:-1]
                for l in range(0,len(linha)):
                    linha[l] = float(linha[l])
                consume.append(linha)
                i = i+3
        else:
            i = 14
            while(i < len(text)):
                linha = re.split(r' ',text[i])
                linha[-1] = linha[-1][:-1]
                for l in range(0,len(linha)):
                    linha[l] = float(linha[l])
                consume.append(linha)
                i = i+3
        if(notFirst):

            for i in range(0,len(totalConsume)):
                for j in range(0,3):
                    totalConsume[i][j] += consume[i][j]
        else:
            totalConsume = consume
            notFirst = True
    for i in range(0,len(totalConsume)):
        for j in range(0,3):
            totalConsume[i][j] = totalConsume[i][j]/3
    return totalConsume


def getSelfishNodes(path):
    arq = open(path)
    text = arq.readlines()
    selfishNodes = []
    if(text[2] == "No egoist Host Data\n"):
        i = 11
        while(i < len(text)-1):
            host = re.split(r' ',text[i])
            if(host[2] == "Selfish\n"):
                selfishNodes.append(host[1])
            i = i+3
    else:
        i = 13
        while(i < len(text)-1):
            host = re.split(r' ',text[i])
            if(host[2] == "Selfish\n"):
                selfishNodes.append(host[1])
            i = i+3
    return selfishNodes
    



def getNormalNodes(path):
    arq = open(path)
    text = arq.readlines()
    normalNodes = []
    if(text[2] == "No egoist Host Data\n"):
        i = 11
        while(i < len(text)-1):
            host = re.split(r' ',text[i])
            if(host[2] == "Normal"):
                normalNodes.append(host[1])
            i = i+3
    else:
        i = 13
        while(i < len(text)-1):
            host = re.split(r' ',text[i])
            if(host[2] == "Normal"):
                normalNodes.append(host[1])
            i = i+3
    return normalNodes
    



def getHostsDischarges(path):
    arq = open(path)
    text = arq.readlines()
    discharges = []
    if(text[2] == "No egoist Host Data\n"):
        i = 13
        while(i < len(text)):
            linha = re.split(r' ',text[i])
            linha[-1] = linha[-1][:-1]
            linha = linha[1:]
            discharge = 0
            singleHostDischarges = []
            while(discharge < len(linha)-2):
                singleHostDischarges.append((linha[discharge],linha[discharge+1]))
                discharge+= 2
            discharges.append(singleHostDischarges)
            i = i+3
    else:
        i = 15
        while(i < len(text)):
            linha = re.split(r' ',text[i])
            linha[-1] = linha[-1][:-1]
            linha = linha[1:]
            discharge = 0
            singleHostDischarges = []
            while(discharge < len(linha)-2):
                singleHostDischarges.append((linha[0],linha[1]))
                discharge += 2
            discharges.append(singleHostDischarges)
            i = i+3
    return discharges

path = "/home/simite/Documents/the-One/reports/PIBIC/intel/messageSize/500MB"
trace = "Intel"

path = "/home/simite/Documents/the-One/reports/PIBIC/infoCom2006/messageSize/500MB"
trace = "InfoCom2006"
'''
path = "/home/simite/Documents/the-One/reports/PIBIC/cambridge/messageSize/500MB"
trace = "Cambridge"
'''
#0 mean energy consume
#1 energy consume
#2 Average recharge time

normalEnergyConsume = []
selfishEnergyConsume = []

#selfish node proportion
proportion = []

#Individual host data
selfishNodes = []
normalNodes = []
hostsConsume = []
hostsDischarges = []
buffers = []
selfishLevel = 0


hosts = hostsSize(path+"0/0/"+trace+"_trace_SelfishReport.txt")

allnormalEnergyConsume = []
allselfishEnergyConsume = []
allselfishNodes = []
allnormalNodes = []
allhostsConsume = []

messageSize = ["100","250","500"]

for run in range(0,3):
    trace = "InfoCom2006"
    if(run == 0):
        path = "/home/simite/Documents/the-One/reports/PIBIC/infoCom2006/selfishLevel"
    if(run == 1):
        path = "/home/simite/Documents/the-One/reports/PIBIC/infoCom2006/selfishLevel"
    if(run == 2):
        path = "/home/simite/Documents/the-One/reports/PIBIC/infoCom2006/selfishLevel"

    hosts = hostsSize(path+"/0/"+trace+"_trace_SelfishReport.txt")
    print(path)
    for i in range(0,11):#numero de niveis de egoismo
        #INFORMACAO GERAL DA REDE
        #proportion.append(SelfishNodesProportion(path+"/"+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
        normalEnergyConsume.append(normalConsumedEnergy(path,selfishLevel,trace+"_trace_SelfishReport.txt"))
        selfishEnergyConsume.append(selfishConsumedEnergy(path,selfishLevel,trace+"_trace_SelfishReport.txt"))


        #ESPECIFICO DE CADA NO
        selfishNodes.append(getSelfishNodes(path+"/"+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
        normalNodes.append(getNormalNodes(path+"/"+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
        hostsConsume.append(getHostConsume(path,selfishLevel,trace+"_trace_SelfishReport.txt"))
        #hostsDischarges.append(getHostsDischarges(path+messageSize[run]+"MB"+str(run)+"/"+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))

        selfishLevel += 10
    selfishLevel = 0
    allnormalEnergyConsume.append(normalEnergyConsume)
    allselfishEnergyConsume.append(selfishEnergyConsume)
    allselfishNodes.append(selfishNodes)
    allnormalNodes.append(normalNodes)
    allhostsConsume.append(hostsConsume)


    normalEnergyConsume = []
    selfishEnergyConsume = []
    proportion = []
    selfishNodes = []
    normalNodes = []
    hostsConsume = []
    hostsDischarges = []
    buffers = []
    selfishLevel = 0

selfishLevel = [0,10,20,30,40,50,60,70,80,90,100]
marks = [".","^","2","s","p","*","h","+","|","_","1"]

#Mean consume

#0 mean energy consume
#1 energy consume
#2 Average recharge time
'''
meanNormalEnergy = []
meanSelfishEnergy = []

totalNormalEnergy = []
totalSelfishEnergy = []

normalDischarges = []
selfishDischarges = []

for nivel in range(0,len(selfishLevel)):
    meanNormalEnergy.append(0)    
    meanSelfishEnergy.append(0)
    totalNormalEnergy.append(0)
    totalSelfishEnergy.append(0)

    meanNormalEnergy[nivel] = (float(normalEnergyConsume[nivel][1]))
    meanSelfishEnergy[nivel] = (float(selfishEnergyConsume[nivel][1]))

    for h in range(0,hosts):
        if(allselfishNodes[run][nivel].__contains__(str(h))):
            totalSelfishEnergy[nivel] += float(hostsConsume[nivel][h][1])
        else:
            totalNormalEnergy[nivel] +=  float(hostsConsume[nivel][h][1])

print(totalNormalEnergy)
print(totalSelfishEnergy)

'''


totalNormalEnergy100 = []
totalSelfishEnergy100 = []
totalNormalEnergy250 = []
totalSelfishEnergy250 = []
totalNormalEnergy500 = []
totalSelfishEnergy500 = []

print(allselfishEnergyConsume[1])
print()
print(allselfishEnergyConsume[2])

for nivel in range(0,len(selfishLevel)):

    totalNormalEnergy100.append(0)
    totalSelfishEnergy100.append(0)
    totalNormalEnergy250.append(0)
    totalSelfishEnergy250.append(0)
    totalNormalEnergy500.append(0)
    totalSelfishEnergy500.append(0) 

    totalSelfishEnergy100[nivel] += float(allselfishEnergyConsume[0][nivel][1])
    totalNormalEnergy100[nivel] +=  float(allnormalEnergyConsume[0][nivel][1])
    totalSelfishEnergy250[nivel] += float(allselfishEnergyConsume[1][nivel][1])
    totalNormalEnergy250[nivel] +=  float(allnormalEnergyConsume[1][nivel][1])
    totalSelfishEnergy500[nivel] += float(allselfishEnergyConsume[2][nivel][1])
    totalNormalEnergy500[nivel] +=  float(allnormalEnergyConsume[2][nivel][1])
'''

media = 0
loop = 0
for i in range(0,len(meanNormalEnergy)):
    if(meanNormalEnergy[i] == 0.0 or meanSelfishEnergy[i] == 0.0):
        continue
    print(meanNormalEnergy[i]," -- ", meanSelfishEnergy[i], " -- ", abs(meanNormalEnergy[i] - meanSelfishEnergy[i]))
    media += abs(meanNormalEnergy[i] - meanSelfishEnergy[i])
    loop +=1
media = media / loop
print(media)

for nd in normalEnergyConsume:
    normalDischarges.append(float(nd[2]))

for sd in selfishEnergyConsume:
    selfishDischarges.append(float(sd[2]))

fig, ax = plt.subplots()
ax.plot(selfishLevel,meanSelfishEnergy,label="Média do consumo egoísta",marker=marks[1])
ax.plot(selfishLevel,meanNormalEnergy,label="Média do consumo normal",marker=marks[0])

plt.xlabel('nível de egoísmo',fontsize=14)
plt.ylabel('\nEnergia consumida ( J )',fontsize=14)
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()

totalNormalEnergy100
totalSelfishEnergy100
totalNormalEnergy250
totalSelfishEnergy250
totalNormalEnergy500
totalSelfishEnergy500 

'''

fig, ax = plt.subplots()

ax.plot(selfishLevel,totalNormalEnergy100,label='Egoísta',marker=marks[0])
ax.plot(selfishLevel,totalSelfishEnergy100,label="Normal",marker=marks[1])
#ax.plot(selfishLevel,totalNormalEnergy250,label="normal 250 MB",marker=marks[2],color='blue')
#ax.plot(selfishLevel,totalSelfishEnergy250,label="egoísta 250 MB",marker=marks[3],color='orange')
#ax.plot(selfishLevel,totalNormalEnergy500,label="normal 500 MB",marker=marks[4],color='blue')
#ax.plot(selfishLevel,totalSelfishEnergy500,label="egoísta 500 MB",marker=marks[5],color='orange')

plt.xlabel('nível de egoísmo',fontsize=16)
plt.ylabel('Energia consumida (J)',fontsize=16)
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()

'''
#print(normalDischarges)
fig, ax = plt.subplots()

ax.plot(selfishLevel,normalDischarges,label="média de descargas de nós normais",marker=marks[0])
ax.plot(selfishLevel,selfishDischarges,label="Média de descargas de nós egoístas",marker=marks[1])

legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.xlabel('nível de egoísmo')
plt.ylabel('Numero de descargas')
plt.show()
'''