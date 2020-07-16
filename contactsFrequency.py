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
'''
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

arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/beta/limite_superior_de_contatos_UfamOficial_ConnectivityONEReport.txt","r")
#arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/emaucTest_ScanReport.txt","r")
text = arq.readlines()


connections = []
connDurations = []

class conection:
    def __init__(self, fromHost, toHost,start):
        self.fromHost = fromHost
        self.toHost = toHost
        self.start = start
        self.end = 0
    
    def haveNodes(self,fromHost,toHost):
        if((self.fromHost == fromHost and self.toHost == toHost) or (self.fromHost == toHost and self.toHost == fromHost)):
            return True
        else:
            return False
    def endConnectionTime(self,time):
        self.end = time
        
    def getDuration(self):
        return self.end - self.start
    
i = 0
for conn in text:
    contact = re.split(r' ',conn)
    if(int(contact[2]) != 458 and int(contact[3]) != 458 ):
        if(contact[4] == "up\n"):
            connections.append(conection(int(contact[2]),int(contact[3]),int(float(contact[0]))))
        else:
            for c in connections:
                if(c.haveNodes(int(contact[2]),int(contact[3]))):
                    c.endConnectionTime(int(float(contact[0])))
                    connDurations.append(c.getDuration())
                    connections.remove(c)
                    break
connDurations.sort()

arq_saida = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/scanConstante.txt","w")
arq_saida.write(str(connDurations))

cs = np.cumsum(connDurations)
proportion = np.arange(len(cs)) / len(cs)
plt.plot(cs,proportion)
plt.show()
'''