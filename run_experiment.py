import os
one = "./one.sh -b 1 "
exit(0)
'''
path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/intel/selfishLevelExperement/setting_trace_intel_"
for reRun in range (0,10):
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/selfishLevel /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/selfishLevel"+str(reRun))

'''

path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/infoCom2006/selfishLevelExperement/setting_trace_infocom2006_"
for reRun in range (0,10):
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/selfishLevel /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/selfishLevel"+str(reRun))




path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/cambridge/selfishLevelExperement/setting_trace_cambridge_"
for reRun in range (0,10):
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/selfishLevel /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/selfishLevel"+str(reRun))
