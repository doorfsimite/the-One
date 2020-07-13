import matplotlib as plt
import re
import numpy as np
from pylab import *

arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/emaucTest_ScanReport.txt","r")

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

for host in textScan:
    consume = re.split(r' ',host)
    staticTime.append((int(consume[7][:-2]),int(consume[0])))
    movingTime.append((int(consume[2][:-2]),int(consume[0])))
    onTime.append((int(consume[1][:-2]),int(consume[0])))
    
    
    mScan.append(float(consume[4]))
    mIdle.append(float(consume[5]))
    mSleep.append(float(consume[6]))

    sScan.append(float(consume[9]))
    sIdle.append(float(consume[10]))
    sSleep.append(float(consume[11][:-1]))

    
meanMscan = 0
meanMidle = 0
meanMsleep = 0
meanSscan = 0
meanSidle = 0
meanSsleep = 0
for i in range(0,len(mScan)):
    meanMscan += mScan[i]
    meanMidle += mIdle[i]
    meanMsleep += mSleep[i]

    meanSscan += sScan[i]
    meanSidle += sIdle[i]
    meanSsleep += sSleep[i]


meanMscan =  meanMscan/len(mScan)
meanMidle =  meanMidle/len(mScan)
meanMsleep = meanMsleep/len(mScan)

meanSscan =  meanSscan/len(sScan)
meanSidle =  meanSidle/len(sScan)
meanSsleep = meanSsleep/len(sScan)


#MOVING
x = np.arange(3)
plt.bar(x, height=[meanMscan,meanMidle,meanMsleep])
plt.xticks(x,["scan","idle","sleep"],fontsize=14)
plt.ylabel('tempo (segundos)',fontsize=14)
plt.show()

#STATIC
x = np.arange(3)
plt.bar(x, height=[meanSscan,meanSidle,meanSsleep])
plt.xticks(x,["scan","idle","sleep"],fontsize=14)
plt.ylabel('tempo (segundos)',fontsize=14)

plt.show()

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


for i in range(0,len(sortedMovingTime)):
    sortedMovingTime[i] = sortedMovingTime[i][0]
    sortedstaticTime[i] = sortedstaticTime[i][0]
    sortedOnTime[i] = sortedOnTime[i][0]

print(sortedMovingTime)
print(sortedstaticTime)

#plt.plot(sortedOnTime)
plt.plot(sortedMovingTime)
plt.plot(sortedstaticTime)
plt.show()



exit(0)
plotStatic = []
plotMoving = []
plotHost = []
for i in range (0,len(staticTime)):
    plotStatic.append(staticTime[i][0])
    plotMoving.append(sortedMovingTime[i][0])
    plotHost.append(staticTime[i][1])

print(plotStatic)
ps = np.array(plotStatic)
plt.plot(ps)
plt.plot(sortedMovingTime)
#,plotHost)
#plt.plot(plotHost,plotMoving)
plt.show()


