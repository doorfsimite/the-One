import matplotlib.pyplot as plt

import numpy as np
import re 
#seta a fonte dos graficos em times new roman
def messageStatsReport_deliveryRate(path,fileName,files = 1):
    total = 0
    for i in range(0,files):
        arq = open(path+str(i)+"/"+fileName)
        text = arq.readlines()
        total += float (re.split(r' ',text[9])[1][:-1])
    return total/files
def messageStatsReport_ropAvr(path,fileName,files = 1):
    total = 0
    for i in range(0,files):
        arq = open(path+str(i)+"/"+fileName)
        text = arq.readlines()
        total += float (re.split(r' ',text[14])[1][:-1])
    return total/files

def messageStatsReport_transferedMessages(path,fileName,files = 1):
    total = 0
    for i in range(0,files):
        arq = open(path+str(i)+"/"+fileName)
        text = arq.readlines()
        total += float (re.split(r' ',text[3])[1][:-1])
    return total/files

def messageStatsReport_createdMessages(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[2])[1][:-1])


def messageStatsReport_latencyAverage(path,fileName,files = 1):
    total = 0
    for i in range(0,files):
        arq = open(path+str(i)+"/"+fileName)
        text = arq.readlines()
        total += float (re.split(r' ',text[12])[1][:-1])
    return total/files

def messageStatsReport_latencyMedian(path,fileName,files = 1):
    total = 0
    for i in range(0,files):
        arq = open(path+str(i)+"/"+fileName)
        text = arq.readlines()
        total += float (re.split(r' ',text[13])[1][:-1])
    return total/files


def totalEncountersReport(path):
    arq = open(path)
    lines = arq.readlines()[:-1]
    enconters = []
    for i in lines:
        enconter = re.split(r' ',i)
        enconters.append(enconter[1][:-1])
    
    return enconters


def bufferOccupancy(path,fileName,file = 0):
    total = 0
    buffers = []
    arq = open(path+str(file)+"/"+fileName)
    text = arq.readlines()

    for i in range (5,len(text)):
        buffer = re.split(r' ',text[i][:-1])
        buffer = buffer[1:]
        for f in range(0,len(buffer)):
            buffer[f] = float(buffer[f])
        buffers.append(buffer)

    return buffers

def bufferOccupancyTimes(path):
    arq = open(path)
    text = arq.readlines()
    return re.split(r' ',text[4][2:-1])
    

def transferMessagesReport(path):
    arq = open(path)
    text = arq.readlines()
    allTransfers = []
    for line in range(2,len(text)):
        transfers = re.split(r' ',text[line][:-1])
        transfers = transfers[1:]
        allTransfers.append(transfers)
    return allTransfers    


def SelfishNodesProportion(path):
    arq = open(path)
    text = arq.readlines()
    return re.split(r' ',text[0])[2]
    

def hostsSize(path):
    arq = open(path)
    text = arq.readlines()
    return int(re.split(r' ',text[1])[1])
    


def normalConsumedEnergy(path):
    arq = open(path)
    text = arq.readlines()
    consume = []
    if(text[2] == "No egoist Host Data\n"):
        for i in range(3,6):
            linha = re.split(r' ',text[i])
            consume.append(linha[-1][:-1])
    else:
        for i in range(5,8):
            linha = re.split(r' ',text[i])
            consume.append(linha[-1][:-1])
            if(consume[0] == "NaN"):
                return [0,0,0]
    return consume
    


def selfishConsumedEnergy(path):
    arq = open(path)
    text = arq.readlines()
    if(text[2] == "No egoist Host Data\n"):
        return [0,0,0]
    else:
        consume = []
        for i in range(2,5):
            linha = re.split(r' ',text[i])
            consume.append(linha[-1][:-1])
    return consume
    

def getSelfishNodes(path,fileName,files = 0):
    allRunsSelfishNode = []
    eachRunSelfishNodes = []
    for f in range(0,files):
        for selfishLevel in range(0,11):
            arq = open(path+str(f)+"/"+str(selfishLevel*10)+fileName)
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
            eachRunSelfishNodes.append(selfishNodes)
        allRunsSelfishNode.append(eachRunSelfishNodes)
        eachRunSelfishNodes = []
    return allRunsSelfishNode
    



def getNormalNodes(path,fileName,files = 0):
    allRunsNormalNode = []
    eachRunNormalNodes = []
    for f in range(0,files):
        for selfishLevel in range(0,11):
            arq = open(path+str(f)+"/"+str(selfishLevel*10)+fileName)
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
            eachRunNormalNodes.append(normalNodes)
        allRunsNormalNode.append(eachRunNormalNodes)
        eachRunNormalNodes = []
    return allRunsNormalNode
    

def getHostConsume(path):
    arq = open(path)
    text = arq.readlines()
    consume = []
    if(text[2] == "No egoist Host Data\n"):
        i = 12
        while(i < len(text)):
            linha = re.split(r' ',text[i])
            linha[-1] = linha[-1][:-1]
            consume.append(linha)
            i = i+3
    else:
        i = 14
        while(i < len(text)):
            linha = re.split(r' ',text[i])
            linha[-1] = linha[-1][:-1]
            consume.append(linha)
            i = i+3
    return consume
    



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


path = "/home/simite/Documents/the-One/reports/PIBIC/intel/selfishLevel"
trace = "Intel"

path = "/home/simite/Documents/the-One/reports/PIBIC/infoCom2006/selfishLevel"
trace = "InfoCom2006"

path = "/home/simite/Documents/the-One/reports/PIBIC/cambridge/selfishLevel"
trace = "Cambridge"

selfishLevel = 0

#createdMessages = messageStatsReport_createdMessages(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt")

deliveryRatePerSelfishLevel = []
ropCountAvrPerSelfishLevel = []
transferedMessages = []
latencyAverage = []
latencyMedian = []
buffers = []
buffersTimes = []
enconters = []
#0 receved mesages
#1 transfered messages
#2 receved (destiny)
#3 delivered messages
hostTransferedMessages = []

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

#totalEncounters = (totalEncountersReport(path+str(selfishLevel)+"/"+trace+"_trace_TotalEncountersReport.txt"))
buffersTimes = (bufferOccupancyTimes(path+'0/0/'+trace+"_trace_BufferOccupancyReport.txt"))
hosts = hostsSize(path+str(selfishLevel)+"/0/"+trace+"_trace_SelfishReport.txt")


for i in range(0,11):#numero de niveis de egoismo
    
    #EM RELAÇÃO A EFICIENCIA DE TRANSMISSÃO DE MENSAGENS
    
    #MessageStatsReports
    #deliveryRatePerSelfishLevel.append(messageStatsReport_deliveryRate(path,str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt",10))
    #ropCountAvrPerSelfishLevel.append(messageStatsReport_ropAvr(path,str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt",10))
    #transferedMessages.append(messageStatsReport_transferedMessages(path,str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt",10))
    #latencyAverage.append(messageStatsReport_latencyAverage(path,str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt",10))
    #latencyMedian.append(messageStatsReport_latencyMedian(path,str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt",10))

    
    #EM RELAÇÃO A RECURSOS E EGOISMO
    #buffers.append(bufferOccupancy(path,str(selfishLevel)+"/"+trace+"_trace_BufferOccupancyReport.txt",0))
    #hostTransferedMessages.append(transferMessagesReport(path+str(selfishLevel)+"/"+trace+"_trace_TransferMessagesReport.txt"))

    #INFORMACAO GERAL DA REDE
    #proportion.append(SelfishNodesProportion(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    #normalEnergyConsume.append(normalConsumedEnergy(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    #selfishEnergyConsume.append(selfishConsumedEnergy(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))


    #ESPECIFICO DE CADA NO
    #hostsConsume.append(getHostConsume(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    #hostsDischarges.append(getHostsDischarges(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))

    selfishLevel += 10


globalBuffers = []
for run in range(0,10):
    for nivel in range(0,11):
        globalBuffers.append( bufferOccupancy(path,str(nivel*10)+"/"+trace+"_trace_BufferOccupancyReport.txt",run))

selfishNodes = getSelfishNodes(path,"/"+trace+"_trace_SelfishReport.txt",10)
normalNodes = getNormalNodes(path,"/"+trace+"_trace_SelfishReport.txt",10)


selfishLevel = [0,10,20,30,40,50,60,70,80,90,100]
'''
#plt.figure(figsize=(1.5748,2.16535))
plt.plot(selfishLevel,deliveryRatePerSelfishLevel)
plt.ylabel('Probabilidade de entrega', fontsize=16)
plt.xlabel('Nível de egoísmo',fontsize=16)
plt.show()
#print("Created messages: ",createdMessages)
#print("Delivery Rate: ",deliveryRatePerSelfishLevel)

plt.plot(selfishLevel,ropCountAvrPerSelfishLevel)
plt.ylabel('Média da quantidade de saltos', fontsize=16)
plt.xlabel('Nível de egoísmo', fontsize=16)
plt.show()
#print("rop count average: ",ropCountAvrPerSelfishLevel)


plt.plot(selfishLevel,transferedMessages)
plt.ylabel('Messagens transferidas', fontsize=16)
plt.xlabel('Nível de egoísmo', fontsize=16)
plt.show()
#print("Transfered messages: ",transferedMessages)


print("Latency Average: ",latencyAverage)
print("Latency Median: ",latencyMedian)

fig, ax = plt.subplots()
la = ax.plot(selfishLevel,latencyAverage,label="Media",marker="s")
lm = ax.plot(selfishLevel,latencyMedian,label="Mediana",marker="^")
#plt.legend([(la,lm)], ['Media','mediana'], numpoints=2)
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Tempo (segundos)', fontsize=16)
plt.xlabel('Nível de egoísmo', fontsize=16)
plt.show()




#BUFFERS:
'''
for b in range(0,len(buffersTimes)):
    buffersTimes[b] = int(buffersTimes[b][:-2])


marks = [".","^","2","s","p","*","h","+","|","_","1"]
#NORMAL HOSTS
fig, ax = plt.subplots()

for nivel in range(0,11):
    totalNormalBuffer = []
    totalSelfishBuffer = []
    for t in range (0,len(buffersTimes)):
        totalNormalBuffer.append(0)
        totalSelfishBuffer.append(0)
    for tempo in range(0,len(buffersTimes)):
        for h in range(0,hosts):
            for run in range(0,10):
                if(selfishNodes[run][nivel].__contains__(str(h))):
                    totalSelfishBuffer[tempo] += globalBuffers[nivel][h][tempo]/10
                else:
                    totalNormalBuffer[tempo] += globalBuffers[nivel][h][tempo]/10
    if(len(normalNodes) > 0):
        for i in range (0,len(totalNormalBuffer)):
            totalNormalBuffer[i] = totalNormalBuffer[i]/len(normalNodes)
    if(nivel > 0 ):
        ax.plot(buffersTimes,totalNormalBuffer,label=str(nivel)+"0%",marker=marks[nivel])
    else:
        ax.plot(buffersTimes,totalNormalBuffer,label=str(nivel)+"%",marker=marks[nivel])

print(totalNormalBuffer)
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Buffer usado (bytes)', fontsize=16)
plt.xlabel('Tempo de simulação', fontsize=16)
plt.show()
'''


#SELFISH NODES
fig, ax = plt.subplots()
for nivel in range(0,11):
    totalNormalBuffer = []
    totalSelfishBuffer = []
    for t in range (0,len(buffersTimes)):
        totalNormalBuffer.append(0)
        totalSelfishBuffer.append(0)
    for tempo in range(0,len(buffersTimes)):
        for h in range(0,hosts):
            for run in range(0,10):
                if(selfishNodes[run][nivel].__contains__(str(h))):
                    totalSelfishBuffer[tempo] += globalBuffers[nivel][h][tempo]/10
                else:
                    totalNormalBuffer[tempo] += globalBuffers[nivel][h][tempo]/10
    if(len(selfishNodes) > 0):
        for i in range (0,len(totalNormalBuffer)):
            totalSelfishBuffer[i] = totalSelfishBuffer[i]/len(selfishNodes)
    if(nivel > 0 ):
        ax.plot(buffersTimes,totalSelfishBuffer,label=str(nivel)+"0%",marker=marks[nivel])
    else:
        ax.plot(buffersTimes,totalSelfishBuffer,label=str(nivel)+"%",marker=marks[nivel])
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Buffer usado (bytes)',fontsize=16)
plt.xlabel('Tempo de simulação (segundos)',fontsize=16)
plt.show()

#Mean consume

#0 mean energy consume
#1 energy consume
#2 Average recharge time

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
        if(selfishNodes[nivel].__contains__(str(h))):
            totalSelfishEnergy[nivel] += float(hostsConsume[nivel][h][1])
        else:
            totalNormalEnergy[nivel] +=  float(hostsConsume[nivel][h][1])

print(totalNormalEnergy)
print(totalSelfishEnergy)

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

plt.xlabel('nível de egoísmo')
plt.ylabel('Energia consumida (W)')
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()



fig, ax = plt.subplots()

ax.plot(selfishLevel,totalNormalEnergy,label="Total do consumo normal",marker=marks[0])
ax.plot(selfishLevel,totalSelfishEnergy,label="Total do consumo egoísta",marker=marks[1])
plt.xlabel('nível de egoísmo')
plt.ylabel('Energia consumida (W)')
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()


#print(normalDischarges)
fig, ax = plt.subplots()

ax.plot(selfishLevel,normalDischarges,label="média de descargas de nós normais",marker=marks[0])
ax.plot(selfishLevel,selfishDischarges,label="Média de descargas de nós egoístas",marker=marks[1])

legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.xlabel('nível de egoísmo')
plt.ylabel('Numero de descargas')
plt.show()

#print("Proportion: ",proportion)
#print("Econters: ",totalEncounters)
#print("normal consume: ",normalEnergyConsume)
#print("selfish consume: ",selfishEnergyConsume)
#print("selfishnodes: ",selfishNodes )
#print("Normal nodes: ",NormalNodes)
#print("hostsConsume: ",hostsConsume)
'''