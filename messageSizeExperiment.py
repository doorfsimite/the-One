import os
one = "./one.sh -b 1 "
exit(0)
#intel 5 - 50 mb
#tem que executar mais vezes

for reRun in range (0,3):
    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/intel/messageSizeExperiment/100MB/setting_trace_intel_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/100MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/100MB"+str(reRun))
        
    
    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/intel/messageSizeExperiment/250MB/setting_trace_intel_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/250MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/250MB"+str(reRun))
    
    
    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/intel/messageSizeExperiment/500MB/setting_trace_intel_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/500MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/intel/messageSize/500MB"+str(reRun))
    
    #infocom2006 

    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/infoCom2006/messageSizeExperiment/100MB/setting_trace_infocom2006_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/100MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/100MB"+str(reRun))
    
    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/infoCom2006/messageSizeExperiment/250MB/setting_trace_infocom2006_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/250MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/250MB"+str(reRun))

    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/infoCom2006/messageSizeExperiment/500MB/setting_trace_infocom2006_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/500MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/infoCom2006/messageSize/500MB"+str(reRun))

    #cambridge

    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/cambridge/messageSizeExperiment/100MB/setting_trace_cambridge_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/100MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/100MB"+str(reRun))

    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/cambridge/messageSizeExperiment/250MB/setting_trace_cambridge_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/250MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/250MB"+str(reRun))

    path = "/home/simite/Documents/the-one-1.6.0/selfishSettings/cambridge/messageSizeExperiment/500MB/setting_trace_cambridge_"
    nivel = 0
    for i in range(0,11):
        os.system(one+path+str(nivel)+".txt")
        nivel += 10
    os.system("cp -r /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/500MB /home/simite/Documents/the-one-1.6.0/reports/PIBIC/cambridge/messageSize/500MB"+str(reRun))