import matplotlib.pyplot as plt
import numpy as np
import re 

def messageStatsReport_deliveryRate(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[9])[1][:-1])

def messageStatsReport_ropAvr(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[14])[1][:-1])

def messageStatsReport_transferedMessages(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[3])[1][:-1])

def messageStatsReport_createdMessages(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[2])[1][:-1])


def messageStatsReport_latencyAverage(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[12])[1][:-1])

def messageStatsReport_latencyMedian(path):
    arq = open(path)
    text = arq.readlines()
    return float (re.split(r' ',text[13])[1][:-1])


def totalEncountersReport(path):
    arq = open(path)
    lines = arq.readlines()[:-1]
    enconters = []
    for i in lines:
        enconter = re.split(r' ',i)
        enconters.append(enconter[1][:-1])
    
    return enconters


def bufferOccupancy(path):
    arq = open(path)
    text = arq.readlines()
    buffers = []
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


path = "/home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/selfishLevel/"
trace = "Intel"

path = "/home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/selfishLevel0/"
trace = "InfoCom2006"

path = "/home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/selfishLevel0/"
trace = "Cambridge"

selfishLevel = 0

createdMessages = messageStatsReport_createdMessages(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt")

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

totalEncounters = (totalEncountersReport(path+str(selfishLevel)+"/"+trace+"_trace_TotalEncountersReport.txt"))
buffersTimes = (bufferOccupancyTimes(path+str(selfishLevel)+"/"+trace+"_trace_BufferOccupancyReport.txt"))
hosts = hostsSize(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt")

for i in range(0,11):#numero de niveis de egoismo
    
    #EM RELAÇÃO A EFICIENCIA DE TRANSMISSÃO DE MENSAGENS
    
    #MessageStatsReports
    deliveryRatePerSelfishLevel.append(messageStatsReport_deliveryRate(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt"))
    ropCountAvrPerSelfishLevel.append(messageStatsReport_ropAvr(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt"))
    transferedMessages.append(messageStatsReport_transferedMessages(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt"))
    latencyAverage.append(messageStatsReport_latencyAverage(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt"))
    latencyMedian.append(messageStatsReport_latencyMedian(path+str(selfishLevel)+"/"+trace+"_trace_MessageStatsReport.txt"))

    
    #EM RELAÇÃO A RECURSOS E EGOISMO
    buffers.append(bufferOccupancy(path+str(selfishLevel)+"/"+trace+"_trace_BufferOccupancyReport.txt"))
    hostTransferedMessages.append(transferMessagesReport(path+str(selfishLevel)+"/"+trace+"_trace_TransferMessagesReport.txt"))

    #INFORMACAO GERAL DA REDE
    proportion.append(SelfishNodesProportion(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    normalEnergyConsume.append(normalConsumedEnergy(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    selfishEnergyConsume.append(selfishConsumedEnergy(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))


    #ESPECIFICO DE CADA NO
    selfishNodes.append(getSelfishNodes(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    normalNodes.append(getNormalNodes(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    hostsConsume.append(getHostConsume(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))
    hostsDischarges.append(getHostsDischarges(path+str(selfishLevel)+"/"+trace+"_trace_SelfishReport.txt"))

    selfishLevel += 10

selfishLevel = [0,10,20,30,40,50,60,70,80,90,100]

'''
plt.plot(selfishLevel,deliveryRatePerSelfishLevel)
plt.ylabel('Probabilidade de entrega')
plt.xlabel('Nível de egoísmo')
plt.show()
#print("Created messages: ",createdMessages)
#print("Delivery Rate: ",deliveryRatePerSelfishLevel)


plt.plot(selfishLevel,ropCountAvrPerSelfishLevel)
plt.ylabel('Média da quantidade de saltos')
plt.xlabel('Nível de egoísmo')
plt.show()
#print("rop count average: ",ropCountAvrPerSelfishLevel)


plt.plot(selfishLevel,transferedMessages)
plt.ylabel('Messagens transferidas')
plt.xlabel('Nível de egoísmo')
plt.show()
#print("Transfered messages: ",transferedMessages)


print("Latency Average: ",latencyAverage)
print("Latency Median: ",latencyMedian)

fig, ax = plt.subplots()
la = ax.plot(selfishLevel,latencyAverage,label="Media",marker="s")
lm = ax.plot(selfishLevel,latencyMedian,label="Mediana",marker="^")
#plt.legend([(la,lm)], ['Media','mediana'], numpoints=2)
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Tempo (segundos)')
plt.xlabel('Nível de egoísmo')
plt.show()

'''



#BUFFERS:
marks = [".","^","2","s","p","*","h","+","|","_","1"]

'''
for b in range(0,len(buffersTimes)):
    buffersTimes[b] = int(buffersTimes[b][:-2])
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
            if(selfishNodes[nivel].__contains__(str(h))):
                totalSelfishBuffer[tempo] += buffers[nivel][h][tempo]
            else:
                totalNormalBuffer[tempo] += buffers[nivel][h][tempo]
    if(len(normalNodes) > 0):
        for i in range (0,len(totalNormalBuffer)):
            totalNormalBuffer[i] = totalNormalBuffer[i]/len(normalNodes)
    if(nivel > 0 ):
        ax.plot(buffersTimes,totalNormalBuffer,label=str(nivel)+"0%",marker=marks[nivel])
    else:
        ax.plot(buffersTimes,totalNormalBuffer,label=str(nivel)+"%",marker=marks[nivel])
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Buffer usado (bytes)')
plt.xlabel('Tempo de simulação')
plt.show()


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
            if(selfishNodes[nivel].__contains__(str(h))):
                totalSelfishBuffer[tempo] += buffers[nivel][h][tempo]
            else:
                totalNormalBuffer[tempo] += buffers[nivel][h][tempo]
    if(len(selfishNodes) > 0):
        for i in range (0,len(totalNormalBuffer)):
            totalSelfishBuffer[i] = totalSelfishBuffer[i]/len(selfishNodes)
    if(nivel > 0 ):
        ax.plot(buffersTimes,totalSelfishBuffer,label=str(nivel)+"0%",marker=marks[nivel])
    else:
        ax.plot(buffersTimes,totalSelfishBuffer,label=str(nivel)+"%",marker=marks[nivel])
legend = ax.legend(loc='upper left', shadow=False, fontsize='medium')
plt.ylabel('Buffer usado (bytes)')
plt.xlabel('Tempo de simulação')
plt.show()

'''
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
'''
print(totalNormalEnergy)
print(totalSelfishEnergy)

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

'''

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