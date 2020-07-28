import graphviz
from graphviz import Source

arq = open("UFAM_EPIDEMIC_MultiCast_emauc_trace_AdjacencyGraphvizReport.txt",'r')
#arq = open("gteste.txt",'r')
arq_text = arq.read()


#h = graphviz.Graph(format='png')

print(arq_text)
src = Source(arq_text)
src.format = 'png'
src.render()
print(src)