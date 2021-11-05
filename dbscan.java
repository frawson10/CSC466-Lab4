import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.lang.Math;

public class dbscan {

    public static ArrayList<ArrayList<String>> readCSV(String filename) {
        /**
         * output is an ArrayList of ArrayList of doubles
         * can be indexed into in row-major order
         * output.get(i).get(j) = d[i][j]
         * output.get(0) is the binary vector for restrictions
         */
        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
        try {
            Scanner sc = new Scanner(new File(filename));
            String curr;
            while(sc.hasNextLine()) {
                curr = sc.nextLine();
                if(curr.equals("")) { break; }  // case for extra line @ end of iris
                String[] vals = curr.split(",");
                ArrayList<String> row = new ArrayList<String>();
                for(String v: vals) { row.add(v); }
                output.add(row);
            }
        }
        catch(FileNotFoundException e) {
            System.out.printf("file: '%s' not found", filename);
            System.exit(1);
        }
        return output;
    }

    public static double[][] makeDistanceMatrix(ArrayList<ArrayList<String>> data){
        /**
         * returns a fully filled out distance matrix
         * note: matrix[i][j] = matrix[j][i] and matrix[i][i] = 0
         *       for loops could be changed for optimization
         */
        ArrayList<String> binaryVector = data.remove(0);
        int numPoints = data.size();
        double[][] matrix = new double[numPoints][numPoints];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                matrix[i][j] = dbscan.getDistance(data.get(i), data.get(j), binaryVector);
            }
        }
        return matrix;
    }

    public static double getDistance(ArrayList<String> x, ArrayList<String> y, ArrayList<String> bVector){
        /**
         * gets the manhattan distance of 2 points, using only attributes indicated in the binary vector
         * note: yet to experiment with other distance calculations
         */
        double distance = 0;
        for(int i = 0; i < bVector.size(); i++){
            if(Double.parseDouble(bVector.get(i)) != 0){
                distance += Math.abs(Double.parseDouble(x.get(i)) - Double.parseDouble(y.get(i)));
            }
        }
        return distance;
    }

    public static ArrayList<ArrayList<Integer>> getNeighbors(double[][] distanceMatrix, double epsilon){
        /**
         * Output - ArrayList of ArrayLists of ints: neighbors
         * neighbors.get(i) is the list of points in the epsilon neighborhood of data point with index i
         */
        ArrayList<ArrayList<Integer>> neighbors = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < distanceMatrix.length; i++){
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for(int j = 0; j < distanceMatrix.length; j++){
                if(distanceMatrix[i][j] < epsilon && i != j){
                    temp.add(j);
                }
            }
            neighbors.add(temp);
        }
        return neighbors;
    }

    public static ArrayList<Integer> getCorePoints(ArrayList<ArrayList<Integer>> neighbors, int minPoints){
        /**
         * output - corePoints, which contains the indices of all core points
         */
        ArrayList<Integer> corePoints = new ArrayList<Integer>();
        for(int i = 0; i < neighbors.size(); i++){
            if(neighbors.get(i).size() >= minPoints){
                corePoints.add(i);
            }
        }
        return corePoints;
    }

    public static ArrayList<ArrayList<Integer>> getClusters(ArrayList<Integer> corePoints,
                                                     ArrayList<ArrayList<Integer>> neighbors){
        /**
         * output - clusters
         */
        ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> visitedPoints = new ArrayList<Integer>();
        ArrayList<Integer> unvisitedCorePoints = new ArrayList<Integer>();
        for(int corePt: corePoints){
            unvisitedCorePoints.add(corePt);
        }
        int currCore;
        int temp;
        boolean visited;
        while(unvisitedCorePoints.size() != 0){
            ArrayList<Integer> currCluster = new ArrayList<Integer>();
            ArrayList<Integer> q = new ArrayList<Integer>();
            currCore = unvisitedCorePoints.get(0);
            q.add(currCore);
            visitedPoints.add(currCore);
            while(!q.isEmpty()){
                temp = q.remove(0);
                if(unvisitedCorePoints.contains(temp)){
                    unvisitedCorePoints.remove((Integer)temp);
                }
                currCluster.add(temp);
                for(int neighbor: neighbors.get(temp)){
                    if(!visitedPoints.contains(neighbor)){
                        q.add(neighbor);
                        visitedPoints.add(neighbor);
                    }
                }
            }
            clusters.add(currCluster);
        }
        return clusters;
    }

    public static void print2dArrayList(ArrayList<ArrayList<String>> data) {
        for(int r = 0; r < data.size(); r++) {
            System.out.println(data.get(r).toString());
        }
    }

    public static void printClusterInfo(ArrayList<ArrayList<Integer>> clusters, double[][] distanceMatrix,
                                        ArrayList<ArrayList<String>> data){
        int core;
        double maxDist, minDist, avgDist, currDist, SSE;
        int clusterIdx = 0;
        for(ArrayList<Integer> cluster: clusters){
            core = cluster.get(0);
            avgDist = maxDist = minDist = distanceMatrix[core][cluster.get(1)];
            SSE = Math.pow(avgDist, 2);
            for(int i = 2; i < cluster.size(); i++){
                currDist = distanceMatrix[core][cluster.get(i)];
                avgDist += currDist;
                if(currDist < minDist) { minDist = currDist; }
                if(currDist > maxDist) { maxDist = currDist; }
                SSE += Math.pow(currDist, 2);
            }
            avgDist /= cluster.size() - 1;

            System.out.printf("Cluster: %d\n", clusterIdx++);
            System.out.printf("Center: %s\n", data.get(core).toString());
            System.out.printf("Max Dist. to Center: %f\n", maxDist);
            System.out.printf("Min Dist. to Center: %f\n", minDist);
            System.out.printf("Avg Dist. to Center: %f\n", avgDist);
            System.out.printf("SSE: %f\n", SSE);
            System.out.printf("%d Points:\n", cluster.size() - 1);
            for(int i = 1; i < cluster.size(); i++){
                System.out.printf("%s\n", data.get(cluster.get(i)).toString());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("usage: java dbscan <Filename> <epsilon> <NumPoints>");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> data = dbscan.readCSV(args[0]);
        double epsilon;
        int minPoints;
        epsilon = Double.parseDouble(args[1]);
        minPoints = Integer.parseInt(args[2]);


        double[][] distanceMatrix = dbscan.makeDistanceMatrix(data);
        ArrayList<ArrayList<Integer>> neighbors = dbscan.getNeighbors(distanceMatrix, epsilon);
        ArrayList<Integer> corePoints = dbscan.getCorePoints(neighbors, minPoints);
        ArrayList<ArrayList<Integer>> clusters = dbscan.getClusters(corePoints, neighbors);

        dbscan.printClusterInfo(clusters, distanceMatrix, data);
    }
}