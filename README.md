# DiscriminativeSubgraphDiscovery

This repository contains the source code, toy examples and executable jar used to perform the experiments described in the paper **"Discriminative Pattern Discovery for the Characterization of Different Network Populations"** authored by Fabio Fassetti, Simona Rombo and Cristina Serrao.

The software has been designed to analyse gene expression data of a set of samples belonging to two different populations, tipically the one refferred to healthy individuals an the other to unhealthy ones. Data must be provided through two csv file, each of which has to refer to one of the two populations and contain the expression data organized as a matrix in the form of a matrix with rows representing the number of samples and columns reffered to genes.
This files must be named:
- datasetName_H.csv
- datasetName_U.csv

## How to run the software

Run the code from a console using a command written as follows:

		java -Xmx10g -jar  nomeJar.jar <dataset_path_folder> <dataset_name> 
  
where:

    • **-Xmx10g** set the Java Heap size to 10GB, which is required to deal with bigger datasets
    • <dataset_path_folder> path of the folder containing the input files (output files will be avaialble in this folder, too)
    • <dataset_name> the string which in the files name precedes "_H.extension" and "_U.extension" 

These parametera are mandatoy. If they are not scpecified a message clarifying how to run the code will be displayed

Further parameters can be specified (if ypu don't specify them, the defalut values will be used):

  **-type <H/U>**: indicare per quale sotto-popolazione si desidera individuare i pattern discriminati [default: H]
  
  **-names <geneNames_path>**: path del file in cui sono salvati i nomi dei geni (se non presente nelle stampe verranno usati dei numeri progressivi) [default: null]
  
 **-k <numPattern_output>**: numero di pattern che si desidera produrre in output* [default: 20]

 **-fill_result_set**: in questo momento non siamo in grado di garantire che l'output contenga esattamente k risultati. Per ovviare a questa problematica è stata implementata una funzione che, terminata l'esplorazione dell'albero di ricerca, cerca di riempire i posti rimasti liberi nell'insieme dei risultati. Il fatto di effettuare questo passo è opzionale perché allunga il tempo di mining (inoltre necessita ancora di essere raffinata per essere più efficiente, tuttavia consente al momento di riempire almeno in parte i posti rimasti vuoti)
 
 **-dim <max_cardinality>**: numero massimo di archi dei pattern in output (determina a che profondità dello spazio di ricerca si spingerà l'analisi) [default: 10]
 
**-verbose <0/1/2>**: verbose(0: silent; 1: small-print; 2:extended-print) [default: 1]

**-serialize_heap**:  specifica se si vuole salvare (tramite serializzazione) lo stato dell'heap [al momento poco utile] [defalut false]

 **-depth**: L'attuale implementazione realizza una sorta di vista per livelli, tuttavia, qualora si desideri effettuare una visita in profondità tradizinale è possibile specificare tale parametro [è servito sopratutto per testare la bontà dei cambiamenti apportati al software e la correttezza dei risultati confrontando le due versioni]
 
 **-tau_s <tau_s value>**: strenght threshold (to be used for network creation if they are not availabe yet) [default: 0.7]
 
**-tau_r <tau_r value>**: relevance threshold (to be used for network creation if they are not availabe yet) [default: 0.9]
