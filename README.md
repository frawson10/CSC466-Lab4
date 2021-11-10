# CSC466-Lab4

## Authors: 
    Finian Rawson (frawson@calpoly.edu)    
    Matthew Jaojoco (mjaojoco@calpoly.edu)


## kmeans
    This clustering algorithm creates k clusters, with k specified by the user. 
    Initial cluster centroids chosen using the k-means++ approach
 

### usage: 
    javac kmeans.java
    java kmeans <filename> <k>

## Agglomerate Hierarchical
    This clustering algorithm creates a dendrogram from the provided dataset,
    printing the resulting json object.
    If a threshold is provided, it will then cut the dendrogram at that point
    and present info on the resulting clusters.
    This algorithm uses average link and Manhattan distance for distance calculations.

### usage: 
    javac -cp ".:./lib/org.json.jar" hclustering.java Node.java
    java -cp ".:./lib/org.json.jar" hclustering <filename> [threshold]

### DBSCAN
    This algorithm identifies each data point as either core, boundary, or noise.
    It then turns each group of core neighborhoods that are connected into a cluster.
    Manhattan distance is used to calculate the distance matrix. 

### usage: 
    javac dbscan.java
    java dbscan <filename> <epsilon> <numPoints>