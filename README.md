# Discriminative Subgraph Discovery

This repository contains the source code, the dataset and the executable jar used to perform the experiments described in the paper **"Discriminative Pattern Discovery for the Characterization of Different Network Populations"** authored by Fabio Fassetti, Simona Rombo and Cristina Serrao.

The software has been designed to analyze gene expression data of a set of samples belonging to two different populations, typically the one referred to healthy individuals and the other to unhealthy ones. Data must be provided through two csv file, each of which has to refer to one of the two populations and contain the expression data organized as a matrix in the form of a matrix with rows representing the number of samples and columns referring to genes.

This files must be named:

- datasetName_H.csv
- datasetName_U.csv

## How to run the software

Run the code from a console using a command written as follows:

		java -Xmx10g -jar  nomeJar.jar <dataset_path_folder> <dataset_name> 
  
where:

    • -Xmx10g set the Java Heap size to 10GB, which is required to deal with bigger datasets
    • <dataset_path_folder> path of the folder containing the input files (output files will be available in this folder, too)
    • <dataset_name> the string which in the files name precedes "_H.extension" and "_U.extension" 

These parameters are mandatory. If they are not specified a message clarifying how to run the code will be displayed

Further parameters can be specified (if you don't specify them, the default values will be used):

 **-type <H/U>**: specify for which sub-population (healthy or unhealthy) you want to identify the discriminated patterns [default: H]
  
  **-names <geneNames_path>**: path of the file containing gene names (if not available, genes will be identified by numbers) [default: null]
  
 **-k <numPattern_output>**: number of patterns you what to be printed as output [default: 20]

 **-dim <max_cardinality>**: max number of edges of the patterns provided as output (it determinates the depth of the search space) [default: 10]
 
**-verbose <0/1/2>**: manage the prints (0: silent; 1: small-print; 2:extended-print) [default: 1]

**-tau_s <tau_s value>**: strength threshold (to be used for network creation if they are not available yet) [default: 0.7]
 
**-tau_r <tau_r value>**: relevance threshold (to be used for network creation if they are not available yet) [default: 0.9]

**-depth**: specify how the search space is visited (see the paragraph "Visiting the search space" below)


## Building the networks

As a first step, the software uses the expression levels stored in files provided as input to create the co-expression networks describing each sample. The networks are store in binary files named:

 - datasetName_corrH.ds2 
 - datasetName_corrU.ds2.

More in detail, for each sample and for each pair of genes, the parameters of interest are evaluated and the adjacent matrices of the graphs corresponding to each sample are constructed. As such graphs are undirected, adjacent matrices are stored as lower triangular in .ds2 files.
Using this format, data is stored as follows: the first 64 bits of the files contain the dimensions of the matrix (the first 32 bits refer to the number of columns and the other 32 to the number of rows) the remaining ones host data. Therefore, the output of the network construction procedure is given by nSamples x nEdges (with nEdges = nGenes*(nGenes-1)/2), one for each subpopulation.
The software provides methods to read and write .ds2 files.

## Visiting the search space 

The research space is visited in depth up to a level specified by the user. The deepest level corresponds to the maximum number of edges you want the patterns to be formed.
Two visiting strategies have been implemented: the first (performed at the request of the user specifying the -depth parameter) performs a real visit in depth of the research space; the second is an iterative deepening strategy: a depth-limited search is repeatedly performed, increasing the depth limit (of one unit) at each iteration until reaching the maximum depth (specified by the user). In this way, at each iteration, it is possible to restart with the threshold determined by the previous iteration.
The results of each level are stored in the same folder containing the datasets.


