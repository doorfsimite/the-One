import re
import numpy as np
from pylab import *

#arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/beta/limite_superior_de_contatos_UfamOficial_ConnectivityONEReport.txt","r")
arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/emaucTest_ScanReport.txt","r")


menor = 0
text = arq.readlines()
connDurations = []
textDurations = ((re.split(r', ',(text[0][1:-1]))))
for i in range(0,len(textDurations)):
    if(int(textDurations[i]) > 15):#contato válido (t > scanDuration))
        connDurations.append(int(textDurations[i]) - 15)#duracao - scanDuration
        if((int(textDurations[i]) - 15 )< 60):
            menor +=1

connDurations = np.array(connDurations)
print(100*menor/len(connDurations))
proportion = np.arange(len(connDurations)) / len(connDurations)

plt.plot(connDurations,proportion)
plt.show()

exit(0)

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
    print(contact[0])
    if(contact[-1] == "up\n"):
        connections.append(conection(int(contact[2]),int(contact[3]),int(float(contact[0]))))
    else:
        for c in connections:
            if(c.haveNodes(int(contact[2]),int(contact[3]))):
                c.endConnectionTime(int(float(contact[0])))
                connDurations.append(c.getDuration())
                connections.remove(c)
                break

connDurations.sort()
arq_saida = open("/home/simite/Documents/the-one-1.6.0/reports/PIBIC/ufam/totalContacts/beta/plotagem.txt","w")
arq_saida.write(str(connDurations))

cs = np.cumsum(connDurations)
proportion = np.arange(len(cs)) / len(cs)
plt.plot(cs,proportion)
plt.show()
