import matplotlib as plt
import re
import numpy as np
from pylab import *

arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/delta = 60/emaucTest_ScanReport.txt","r")
fullScanArq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/fullScan/UFAMFullScanEnergy_ScanReport.txt","r")

#[0] HOST
#[1] TimeON
#[2] Moving:Time 
#[3] Moving:Energy
#[4] Moving:ScanTime  
#[5] Moving:idleTime
#[6] Moving:sleepTime
#[7] Static:Time
#[8] Static:Energy
#[9] Static:ScanTime
#[10] Static:idleTime
#[11] Static:sleepTime

textScan = arq.readlines()[1:]
textFullScan = fullScanArq.readlines()[1:]

fullScanEnergy = []
emaucScanEnergy = []

staticTime = []
movingTime = []
onTime = []

mScan = []
mIdle = []
mSleep = []

sScan = []
sIdle = []
sSleep = []


sortedMovingTime = []
sortedstaticTime = []
sortedOnTime = []
sortedFullScanEnergy = []
sortedEmaucScanEnergy = []

for host in textScan:
    consume = re.split(r' ',host)
    if(int(consume[0]) != 458):
        staticTime.append((int(consume[7][:-2]),int(consume[0])))
        movingTime.append((int(consume[2][:-2]),int(consume[0])))
        onTime.append((int(consume[1][:-2]),int(consume[0])))
        
        
        mScan.append(float(consume[4]))
        mIdle.append(float(consume[5]))
        mSleep.append(float(consume[6]))

        sScan.append(float(consume[9]))
        sIdle.append(float(consume[10]))
        sSleep.append(float(consume[11][:-1]))

        emaucScanEnergy.append((float(consume[3])+float(consume[8]),int(consume[0])))

for host in textFullScan:
    consume = re.split(r' ',host)
    fullScanEnergy.append((float(consume[2]),int(consume[0])))

    
meanMscan = 0
meanMidle = 0
meanMsleep = 0
meanSscan = 0
meanSidle = 0
meanSsleep = 0

meanMtime = 0
meanStime = 0

for i in range(0,len(mScan)):
    meanMscan += mScan[i]
    meanMidle += mIdle[i]
    meanMsleep += mSleep[i]

    meanSscan += sScan[i]
    meanSidle += sIdle[i]
    meanSsleep += sSleep[i]

    meanMtime += movingTime[i][0]
    meanStime += staticTime[i][0]
    
meanMtime = meanMtime/len(movingTime)
meanStime = meanStime/len(staticTime)

meanMscan =  meanMscan/len(mScan)
meanMidle =  meanMidle/len(mScan)
meanMsleep = meanMsleep/len(mScan)

meanSscan =  meanSscan/len(sScan)
meanSidle =  meanSidle/len(sScan)
meanSsleep = meanSsleep/len(sScan)




#media do consumo de cada estado
meanMTime = meanMscan + meanMidle + meanMsleep
meanSTime = meanSscan + meanSidle + meanSsleep

mi = -1
if(meanMidle == 0):
    mi = 0
else:
    mi = meanMidle/meanMTime
ms =  meanMscan/meanMTime
msl =  meanMsleep/meanMTime

si = meanSidle/meanSTime
ss = meanSscan/meanSTime
ssl = meanSsleep/meanSTime

print(mi)
print(ms)
print(msl)



#MOVING
def autolabel(rects,data):
    """Attach a text label above each bar in *rects*, displaying its height."""
    barNumber = 0
    for rect in rects:
        height = rect.get_height()
        ax.annotate("{:.2f}".format(data[barNumber]) + " %",
                    xy=(rect.get_x() + rect.get_width() / 2, height),
                    xytext=(0, 1),  # 3 points vertical offset
                    textcoords="offset points",
                    ha='center', va='bottom',fontsize=14)
        barNumber += 1
'''
x = np.arange(3)
m = [ms,mi,msl]
labels = ["Scan","Idle","Sleep"]
fig, ax = plt.subplots()
rects1 = ax.bar(x,  height=[meanMscan,meanMidle,meanMsleep], width=0.5,label='Women')
ax.set_xticks(x)
ax.set_xticklabels(labels,fontsize=14)
ax.set_ylabel('Tempo (segundos)',fontsize=14)
plt.plot([0],[1100],linestyle='')
autolabel(rects1,m)
ax.set_xlabel("Estado",fontsize=14)
plt.show()

'''


#STATIC
x = np.arange(3)
s = [ss,si,ssl]
labels = ["Scan","Idle","Sleep"]
fig, ax = plt.subplots()

rects1 = ax.bar(x,  height=[meanSscan,meanSidle,meanSsleep], width=0.5)
ax.set_xticks(x)
ax.set_xticklabels(labels,fontsize=14)
ax.set_ylabel('Tempo (segundos)',fontsize=14)
plt.plot(0,[26000],linestyle='-')
autolabel(rects1,s)
ax.set_xlabel("Estado",fontsize=14)

plt.show()



exit(0)

sortedOnTime = sorted(onTime, key=lambda x: x[0])

for i in sortedOnTime:
    for m in movingTime:
        if(i[1] == m[1]):
            sortedMovingTime.append(m)
            movingTime.remove(m)
            break
    for s in staticTime:
        if(i[1] == s[1]):
            sortedstaticTime.append(s)
            staticTime.remove(s)
            break
    for f in fullScanEnergy:
        if(i[1] == f[1]):
            sortedFullScanEnergy.append(f)
            fullScanEnergy.remove(f)
            break
    for ese in emaucScanEnergy:
        if(i[1] == ese[1]):
            sortedEmaucScanEnergy.append(ese)
            emaucScanEnergy.remove(ese)
            break

for i in range(0,len(sortedMovingTime)):
    sortedMovingTime[i] = sortedMovingTime[i][0]
    sortedstaticTime[i] = sortedstaticTime[i][0]
    sortedOnTime[i] = sortedOnTime[i][0]
    sortedFullScanEnergy[i] = sortedFullScanEnergy[i][0]
    sortedEmaucScanEnergy[i] = sortedEmaucScanEnergy[i][0]

'''
plt.plot(sortedstaticTime,label='Parado')
plt.plot(sortedOnTime,label='Ativo')
plt.plot(sortedMovingTime,label='Em movimento')
plt.xlabel("Identificador do usuário",fontsize=14)
plt.ylabel("Tempo (segundos)",fontsize=14)
plt.legend(loc='upper left')
plt.show()


# DIFEREMCA DE ENERGIA CONSUMIDA
print(sortedFullScanEnergy)
plt.plot(sortedFullScanEnergy)
plt.plot(sortedEmaucScanEnergy)
plt.ylabel('Energia Consumida ( J )',fontsize=14)
plt.xlabel("Identificador do usuário",fontsize=14)
plt.show()
'''
