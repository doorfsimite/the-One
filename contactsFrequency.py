import re
import numpy as np
from pylab import *

'''
arqEmauc = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Dados de tempo de contato/EmaucContactsDurations.txt","r")
text = arqEmauc.readlines()

emaucConnDurations = []
textDurations = ((re.split(r', ',(text[0][1:-1]))))
for i in range(0,len(textDurations)):
    if(int(textDurations[i]) > 15):#contato válido (t > scanDuration))
        emaucConnDurations.append(int(textDurations[i]) - 15)#duracao - scanDuration
#        if((int(textDurations[i]) - 15 )< 60):
#            menor +=1

menor = 0
arqFullScan = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Dados de tempo de contato/scanConstante.txt","r")
text = arqFullScan.readlines()

menor = 0
fullScanConnDurations = []
textDurations = ((re.split(r', ',(text[0][1:-1]))))
for i in range(0,len(textDurations)):
    if(int(textDurations[i]) > 15):#contato válido (t > scanDuration))
        fullScanConnDurations.append(int(textDurations[i]) - 15)#duracao - scanDuration
        if((int(textDurations[i]) - 15 )< 60):
            print((int(textDurations[i]) - 15 ))
            menor +=1

print(menor/len(fullScanConnDurations))
exit(0)
print("emaucContacts: ",len(emaucConnDurations)," - FullScanContacts: ",len(fullScanConnDurations))
print("Proporção de contatos perdido",(len(fullScanConnDurations)/len(emaucConnDurations)))
econnDurations = np.array(emaucConnDurations)
fsconnDurations = np.array(fullScanConnDurations)

eProportion = np.arange(len(econnDurations)) / len(econnDurations)
fsProportion = np.arange(len(fullScanConnDurations)) / len(fullScanConnDurations)

plt.plot(fsconnDurations,fsProportion,label="Scan Contínuo",linestyle="solid")
plt.plot(econnDurations,eProportion,label="EMAUC",linestyle="dashed",color="red")
plt.xlabel("Tempo ( segundos )",fontsize=14)
plt.ylabel("FDA",fontsize=14)
legend(loc='lower right')
plt.show()

exit(0)
'''
# gerar lista de tempo de contatos

#arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/beta/limite_superior_de_contatos_UfamOficial_ConnectivityONEReport.txt","r")
#arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/emaucTest_ScanReport.txt","r")

scanEnergy = 0.5455

arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/contatos_validos/emaucScanContactsLog.txt",'r')
text = arq.readlines()

arq_active = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/UFAM_active_Log_ActiveReport.txt",'r')
active_text = arq_active.readlines()

activeTimes = dict()

for i in range(0,625):
    activeTimes[i] = []

for acTimeText in active_text:
    active = re.split(r' ',acTimeText[:-1])
    if(active[1] == "ACTIVE"):
        activeTimes[int(active[2])].append([float(active[0]),0])
    else:
        if( activeTimes[int(active[2])] != []):
            activeTimes[int(active[2])][len( activeTimes[int(active[2])])-1][1] = float(active[0])

def isActive(host,inicio,final):
    global activeTimes
    onTimes = activeTimes[host]

    for i in onTimes:
        if( ((inicio <= i[1] and inicio >= i[0] ) and (final <= i[1] and final >= i[0])) ):
            return True
    return False
        
def getLastConnection(time,host,connections):
    for i in range(len(connections)-1,-1,-1):
        if(connections[i].getTimebyHost(host)):
            return connections[i].ordem
    global activeTimes
    for at in activeTimes[host]:
        if(time > at[0]):
            return at[0]

    return -1

connections = []
connDurations = []

def getOnConnectionTime(conections):
    conections = sorted(conections, key=lambda tup: tup[0])
    msmInicio = []
    for i in conections:
        if(msmInicio == []):
            msmInicio.append([i[0],i[1]])
        for j in msmInicio:
            if(i[0] == j[0]):
                if(i[1] > j[1]):
                    j[1] = i[1]
                    break
            else:
                msmInicio.append([i[0],i[1]])
                break
    
    current = 0

    validRanges = []
    for i in range(0,len(msmInicio)):
        if(msmInicio[i][0] >= msmInicio[current][0] and msmInicio[i][0] < msmInicio[current][1]): #dentro do intervalo
            if(msmInicio[i][1] > msmInicio[current][1]):
                msmInicio[current][1] = msmInicio[i][1]
        else:
            validRanges.append([msmInicio[current][0],msmInicio[current][1]])
            current = i
    
    tempoTotal = 0
    for i in validRanges:
        tempoTotal += i[1] - i[0]
    return tempoTotal


class conection:
    def __init__(self, fromHost, toHost,time,start):
        self.fromHost = fromHost
        self.toHost = toHost
        self.start = 0
        self.end = 0
        self.ordem = time
        self.fromHostEnergy = 0
        self.toHostEnergy = 0
        if(start):
            self.start = time
        else:
            self.end = time

    def haveNodes(self,fromHost,toHost):
        if((self.fromHost == fromHost and self.toHost == toHost) or (self.fromHost == toHost and self.toHost == fromHost)):
            return True
        else:
            return False
    def endConnectionTime(self,time):
        self.end = time
        
    def getDuration(self):
        return self.end - self.start
    
    def getTimebyHost(self,host):
        if(self.toHost == host or self.fromHost == host ):
            return self.ordem
    
    def toString(self):
        if(self.start != 0):
            return str(self.start) + " CONN " + str(self.fromHost) + " " +str(self.toHost) + " up " + str(self.fromHostEnergy) + " "+ str(self.toHostEnergy )
        else:
            return str(self.end) + " CONN " + str(self.fromHost) + " " +str(self.toHost) + " down "+ str(self.fromHostEnergy) + " "+ str(self.toHostEnergy) 


'''
usado pra sintaxe do  full scan
i = 0
conectionsSaida = []
for conn in text:
    contact = re.split(r' ',conn)
    if(int(contact[2]) != 458 and int(contact[3]) != 458):
        if(contact[4] == "up\n"):
            newConection = conection(int(contact[2]),int(contact[3]),int(float(contact[0])),True)
            lastConnTimeToHost = getLastConnection(newConection.ordem,newConection.toHost,conectionsSaida)
            if(lastConnTimeToHost > 0):
                newConection.toHostEnergy = (newConection.start - lastConnTimeToHost) * scanEnergy

            lastConnTimeFromHost = getLastConnection(newConection.ordem,newConection.fromHost,conectionsSaida)
            if(lastConnTimeFromHost > 0):
                newConection.fromHostEnergy = (newConection.start - lastConnTimeFromHost) * scanEnergy

            connections.append(newConection)
            conectionsSaida.append(newConection)

        else:
            for c in connections:
                if(c.haveNodes(int(contact[2]),int(contact[3]))):
                    c.endConnectionTime(int(float(contact[0])))
                    if(isActive(c.fromHost,c.start,c.end) and isActive(c.toHost,c.start,c.end)):
                        if(c.getDuration() > 15):
                            
                            
                            endConnection = conection(int(contact[2]),int(contact[3]),int(float(contact[0])),False)
                            endConnection.toHostEnergy = (endConnection.end - getLastConnection(endConnection.ordem,endConnection.toHost,conectionsSaida) ) * scanEnergy
                            endConnection.fromHostEnergy = (endConnection.end - getLastConnection(endConnection.ordem,endConnection.fromHost,conectionsSaida) ) * scanEnergy

                            conectionsSaida.append(endConnection)
                    else:
                        conectionsSaida.remove(c)
                    connections.remove(c)
                   
                    break
'''

remainingEnergy = dict()
contactTime = dict()
for i in range(0,625):
    contactTime[i] = []

i = 0
conectionsSaida = []
for conn in text:
    contact = re.split(r' ',conn[:-1])
    if(int(contact[2]) != 458 and int(contact[3]) != 458):
        if(contact[4] == "up"):
            newConection = conection(int(contact[2]),int(contact[3]),int(float(contact[0])),True)
            if newConection.toHost in remainingEnergy:
                newConection.toHostEnergy = float(contact[-1]) + remainingEnergy[newConection.toHost]
                del remainingEnergy[newConection.toHost]
            else:
                newConection.toHostEnergy = float(contact[-1])
            
            if newConection.fromHost in remainingEnergy:
                newConection.fromHostEnergy = float(contact[-2]) + remainingEnergy[newConection.fromHost]
                del remainingEnergy[newConection.fromHost]
            else:
                newConection.fromHostEnergy = float(contact[-2])

            connections.append(newConection)
            conectionsSaida.append(newConection)

        else:
            for c in connections:
                if(c.haveNodes(int(contact[2]),int(contact[3]))):
                    c.endConnectionTime(int(float(contact[0])))
                    if(isActive(c.fromHost,c.start,c.end) and isActive(c.toHost,c.start,c.end)):
                        if(c.getDuration() > 15):
                            
                            contactTime[c.fromHost].append((c.start,c.end))
                            contactTime[c.toHost].append((c.start,c.end))
                            
                            endConnection = conection(int(contact[2]),int(contact[3]),int(float(contact[0])),False)

                            if endConnection.toHost in remainingEnergy:
                                endConnection.toHostEnergy = float(contact[-1]) + remainingEnergy[endConnection.toHost]
                                del remainingEnergy[endConnection.toHost]
                            else:
                                endConnection.toHostEnergy = float(contact[-1])
                            
                            if endConnection.fromHost in remainingEnergy:
                                endConnection.fromHostEnergy = float(contact[-2]) + remainingEnergy[endConnection.fromHost]
                                del remainingEnergy[endConnection.fromHost]
                            else:
                                endConnection.fromHostEnergy = float(contact[-2])


                            conectionsSaida.append(endConnection)

                        else:
                            remainingEnergy[c.toHost] = c.toHostEnergy
                            remainingEnergy[c.fromHost] = c.toHostEnergy

                    else:
                        remainingEnergy[c.toHost] = c.toHostEnergy
                        remainingEnergy[c.fromHost] = c.toHostEnergy
                        conectionsSaida.remove(c)
                    connections.remove(c)
                   
                    break


conectionsSaida = sorted(conectionsSaida,key = lambda conection : conection.ordem)

maior = 0
times = []
for i in range(0,625):
    times.append(getOnConnectionTime(contactTime[i]))

for time in times:
    if(time > maior):
        maior = time
    print(i,time)
print(maior)

x = str(times)

saida = open("tempoDeContatoTotalDeCadaNo.txt",'w')
saida.write(x)

#for i in conectionsSaida:
#    print(i.toString()+"\n")


#arq_saida = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/contatos_validos/emaucScanContatosValidos.txt","w")
#for i in conectionsSaida:
#    arq_saida.write(i.toString()+"\n")
#