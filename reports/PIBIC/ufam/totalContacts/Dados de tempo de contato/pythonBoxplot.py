import pandas
import re
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Dados de tempo de contato/tempoDeContatoTotalDeCadaNo.txt",'r')

text = arq.readlines()

textlist = re.split(r', ',text[0][1:-1])

contacts = []
for i in textlist:
    contacts.append(int(i))

x = sorted(contacts)
print(x)
npContacts = np.array(contacts)

df = pd.DataFrame(npContacts,columns=['contatos'])
boxplot = df.boxplot(column=['contatos'])
plt.show()
print(type(boxplot))