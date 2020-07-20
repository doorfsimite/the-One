import re


arq = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/contatos_validos/emaucScanContatosValidos.txt",'r')
scan_text = arq.readlines()

arq_active = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/UFAM_active_Log_ActiveReport.txt",'r')
active_text = arq_active.readlines()

scanlen = len(scan_text) - 1
activelen = len(active_text) - 1

scanLine = 0
activeLine = 0

lines = 0
saida = open("/home/simite/Documents/the-One/reports/PIBIC/ufam/totalContacts/Active_Deactive/contatos_validos/ufam_emaucscan_active_log.txt",'w')


debug = 0
while (lines < scanlen + activelen):
    
    if(activeLine < activelen):
        if(scanLine < scanlen):
            splitScanLine = re.split(r' ',scan_text[scanLine])
            splitActiveLine = re.split(r' ',active_text[activeLine])

            if(float(splitActiveLine[0]) <= float(splitScanLine[0])):
                print(float(splitActiveLine[0]) ,float(splitScanLine[0]))
                saida.write(active_text[activeLine])
                activeLine += 1
            else:
                saida.write(scan_text[scanLine])
                scanLine += 1
        else:
            saida.write(active_text[activeLine])
            activeLine += 1
            
    else:
        saida.write(scan_text[scanLine])
        scanLine+=1
    lines += 1 
