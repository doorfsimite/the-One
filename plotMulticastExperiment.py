import re
import numpy as np
import matplotlib.pyplot as plt

path = "/home/simite/Documents/the-One/reports/PIBIC/ufam/multicast/multicast para 0.5%/"

bubbleEnergy = 'UFAM_BUBBLE_RAP_MultiCast_emauc_trace_SelfishReport.txt'
bubbleMessageStats= 'UFAM_BUBBLE_RAP_MultiCast_emauc_trace_MessageStatsReport.txt'

epidemicEnergy = 'UFAM_EPIDEMIC_MultiCast_emauc_trace_SelfishReport.txt'
epidemicMessageStats = 'UFAM_EPIDEMIC_MultiCast_emauc_trace_MessageStatsReport.txt'

bubbleDeliveryRate = []
epidemicDeliverRate = []

bubbleConsume = []
epidemicConsume = []


def getMessagesStats (fileLines):
    created = int(re.split(r' ',fileLines[2])[1][:-1])
    delivered = int(re.split(r' ',fileLines[8])[1][:-1])
    started = int(re.split(r' ',fileLines[3])[1][:-1])
    return (created,delivered,started)


def getConsume(text):
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
    totalNetworkConsume = 0
    for i in consume:
        totalNetworkConsume += i[1]

    return totalNetworkConsume


for i in range (1,21):
    bubbleEFile = open(path+str(i)+"/"+bubbleEnergy)
    bubbleMFile = open(path+str(i)+"/"+bubbleMessageStats)

    epidemicEFile = open(path+str(i)+"/"+epidemicEnergy)
    epidemicMFile = open(path+str(i)+"/"+epidemicMessageStats)

    bubbleEtext = bubbleEFile.readlines()
    bubbleMtext = bubbleMFile.readlines()

    epidemicEtext = epidemicEFile.readlines()
    epidemicMtext = epidemicMFile.readlines()

    bubbleDeliveryRate.append(getMessagesStats(bubbleMtext))
    epidemicDeliverRate.append(getMessagesStats(epidemicMtext))

    bubbleConsume.append(getConsume(bubbleEtext))
    epidemicConsume.append(getConsume(epidemicEtext))
    

bstarted =[]
bdelivered = []
bcreated = []

estarted = []
edelivered = []
ecreated = []
    

for i in range(0,20):
    bstarted.append(bubbleDeliveryRate[i][2])
    bdelivered.append(bubbleDeliveryRate[i][1])
    bcreated.append(bubbleDeliveryRate[i][0])

    estarted.append(epidemicDeliverRate[i][2])
    edelivered.append(epidemicDeliverRate[i][1])
    ecreated.append(epidemicDeliverRate[i][0])
    
#   print(bubbleDeliveryRate[i],epidemicDeliverRate[i])
    print(i,bubbleConsume[i],epidemicConsume[i])

marks = [".","^","2","s","p","*","h","+","|","_","1"]

'''
fig, ax = plt.subplots()
ax.plot(bcreated,bdelivered,label="Bubble Rap",marker=marks[1])
ax.plot(ecreated,edelivered,label="Epidemic",marker=marks[0])

plt.xlabel('Mensagens criadas',fontsize=14)
plt.ylabel('Mensagens entregues',fontsize=14)
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()

'''

fig, ax = plt.subplots()
ax.plot(bcreated,bubbleConsume,label="Bubble Rap",marker=marks[1])
ax.plot(ecreated,epidemicConsume,label="Epidemic",marker=marks[0])

plt.xlabel('Mensagens criadas',fontsize=14)
plt.ylabel('Energia ( J )',fontsize=14)
legend = ax.legend(loc='upper left', shadow=False, fontsize='small')
plt.show()
