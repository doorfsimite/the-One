import re
import numpy as np
import matplotlib.pyplot as plt

path = "/home/simite/Documents/the-One/reports/PIBIC/ufam/multicast/multicast para 0.5%/"
bubbleMessageStats= 'UFAM_BUBBLE_RAP_MultiCast_emauc_trace_MessageStatsReport.txt'
bubbleDeliveryRate = []
bubbleConsume = []

pathTratado = "/home/simite/Documents/the-One/reports/PIBIC/ufam/tit-for-tat/comTratamentoDeBuffer/"
pathSemTratamento = '/home/simite/Documents/the-One/reports/PIBIC/ufam/tit-for-tat/semTratamento/'

MessageStats = 'UFAM_BubbleRap_TFT_MessageStatsReport.txt'
Energy= 'UFAM_BubbleRap_TFT_SelfishBatteryReport.txt'

tMessageStats = 'UFAM_BubbleRap_TFT_comTratamentoDeBuffer_MessageStatsReport.txt'
tEnergy = 'UFAM_BubbleRap_TFT_comTratamentoDeBuffer_SelfishBatteryReport.txt'

tratadoDeliveryRate = []
semTratamentoDeliverRate = []

tratadoConsume = []
semTratamentoConsume = []


def getMessagesStats (fileLines):
    print(fileLines[2])
    created = int(re.split(r' ',fileLines[2])[1][:-1])
    delivered = int(re.split(r' ',fileLines[8])[1][:-1])
    started = int(re.split(r' ',fileLines[3])[1][:-1])
    return (created,delivered,started)
'''

def getConsume(text):
    hosts = int(re.split(r' ',text[1][:-1]))
    consume = []
    totalNetworkConsume = 0
    
    for i in consume:
        totalNetworkConsume += i[1]

    return totalNetworkConsume

'''
for i in range (0,11):
    if(i == 5):
        continue
    bubbleMFile = open(path+str(i)+"/"+bubbleMessageStats)
    bubbleMtext = bubbleMFile.readlines()
    bubbleDeliveryRate.append(getMessagesStats(bubbleMtext))


    tratadoMFile = open(pathTratado+str(i*10)+"/"+tMessageStats)
    tratadoEFile = open(pathTratado+str(i*10)+"/"+tEnergy)

    semTratamentoMFile = open(pathSemTratamento+str(i*10)+"/"+MessageStats)
    semTratamentoEFile = open(pathSemTratamento+str(i*10)+"/"+Energy)

    tMtext = tratadoMFile.readlines()
    tEtext = tratadoEFile.readlines()
    stMtext = semTratamentoMFile.readlines()
    stEtext = semTratamentoEFile.readlines()
    
    tratadoDeliveryRate.append(getMessagesStats(tMtext))
    semTratamentoDeliverRate.append(getMessagesStats(stMtext))

#bubbleConsume.append(getConsume(bubbleEtext))
#epidemicConsume.append(getConsume(epidemicEtext))
    

bstarted =[]
bdelivered = []
bcreated = []

tstarted =[]
tdelivered = []
tcreated = []

ststarted = []
stdelivered = []
stcreated = []
    

for i in range(0,10):
    bstarted.append(bubbleDeliveryRate[i][2])
    bdelivered.append(bubbleDeliveryRate[i][1])
    bcreated.append(bubbleDeliveryRate[i][0])

    tstarted.append(tratadoDeliveryRate[i][2])
    tdelivered.append(tratadoDeliveryRate[i][1])
    tcreated.append(tratadoDeliveryRate[i][0])

    ststarted.append(semTratamentoDeliverRate[i][2])
    stdelivered.append(semTratamentoDeliverRate[i][1])
    stcreated.append(semTratamentoDeliverRate[i][0])
    
#   print(bubbleDeliveryRate[i],epidemicDeliverRate[i])
#    print(i,bubbleConsume[i],epidemicConsume[i])

marks = [".","^","2","s","p","*","h","+","|","_","1"]


fig, ax = plt.subplots()
ax.plot(bcreated,bdelivered,label="Bubble Rap",marker=marks[1])
ax.plot(tcreated,tdelivered,label="tit-for-tat",marker=marks[1])
ax.plot(stcreated,stdelivered,label="sem tratamento",marker=marks[0])

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
'''