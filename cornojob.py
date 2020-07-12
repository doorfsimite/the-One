import re
arq_agentes = open("/home/simite/Documents/pedsim/ecosystem/build-pedsim-Desktop_Qt_5_9_9_GCC_64bit-Debug/defaut_trace_input.txt","r")
total = 0
agents = []
for line in arq_agentes:
    agents.append(re.split(r' ',line))

for i in range (1,61):
    text = ''
    grupo = "Group"+str(i)+"."

    text += grupo+"movementModel = ExternalMovementUFAM\n"
    text += grupo+"router = EpidemicRouter\n"
#    text += grupo+"bufferSize = 5M\n"
    text += grupo+ "groupID = "+agents[i-1][1]+"-\n"
    text += grupo+"nrofInterfaces = 1\n"
    text += grupo+"interface1 = emauc\n"
#    text += grupo+"msgTtl = 300\n"
    text += grupo+"rechargeInterval = 2\n"
    text += grupo+"initialEnergy = 24413.0\n"
    text += grupo+"rechargeEnergy = 24413\n"
    text += grupo+"scanResponseEnergy = 0\n"
    text += grupo+"transmitEnergy =  1.233\n"
    text += grupo+"receveEnergy = 0.5466\n"
    text += grupo+"scanEnergy =  0.5455\n"
    text += grupo+"idleEnergy = 0.3515\n"
    text += grupo+"sleepEnergy = 0.01825\n"
    x =  int(int(agents[i-1][5])*0.05)+1
    text += grupo+"nrofHosts = "+str( x)+"\n"
    print(text)
