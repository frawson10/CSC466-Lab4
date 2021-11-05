import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.lang.Math;

public class dbscan {

    public static ArrayList<ArrayList<Double>> readCSV(String filename) {
        /**
         * output is an ArrayList of ArrayList of doubles
         * can be indexed into in row-major order
         * output.get(i).get(j) = d[i][j]
         * output.get(0) is the binary vector for restrictions
         */
        ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
        try {
            Scanner sc = new Scanner(new File(filename));
            String curr;
            while(sc.hasNextLine()) {
                curr = sc.nextLine();
                String[] vals = curr.split(",");
                ArrayList<Double> row = new ArrayList<Double>();
                for(String v: vals) { row.add(Double.parseDouble(v)); }
                output.add(row);
            }
        }
        catch(FileNotFoundException e) {
            System.out.printf("file: '%s' not found", filename);
            System.exit(1);
        }
        return output;
    }

    public static double[][] makeDistanceMatrix(ArrayList<ArrayList<Double>> data){
        /**
         * returns a fully filled out distance matrix
         * note: matrix[i][j] = matrix[j][i] and matrix[i][i] = 0
         *       for loops could be changed for optimization
         */
        ArrayList<Double> binaryVector = data.remove(0);
        int numPoints = data.size();
        double[][] matrix = new double[numPoints][numPoints];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                matrix[i][j] = dbscan.getDistance(data.get(i), data.get(j), binaryVector);
            }
        }
        return matrix;
    }

    public static double getDistance(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> bVector){
        /**
         * gets the manhattan distance of 2 points, using only attributes indicated in the binary vector
         * note: yet to experiment with other distance calculations
         */
        double distance = 0;
        for(int i = 0; i < bVector.size(); i++){
            if(bVector.get(i) != 0){
                distance += Math.abs(x.get(i) - y.get(i));
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

    public static void print2dArrayList(ArrayList<ArrayList<Double>> data) {
        for(int r = 0; r < data.size(); r++) {
            System.out.println(data.get(r).toString());
        }
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("usage: java dbscan <Filename> <epsilon> <NumPoints>");
            System.exit(1);
        }

        ArrayList<ArrayList<Double>> data = dbscan.readCSV(args[0]);
        double epsilon;
        int minPoints;
        epsilon = Double.parseDouble(args[1]);
        minPoints = Integer.parseInt(args[2]);

        double[][] distanceMatrix = dbscan.makeDistanceMatrix(data);
        ArrayList<ArrayList<Integer>> neighbors = dbscan.getNeighbors(distanceMatrix, epsilon);
        ArrayList<Integer> corePoints = dbscan.getCorePoints(neighbors, minPoints);
        ArrayList<ArrayList<Integer>> clusters = dbscan.getClusters(corePoints, neighbors);
        System.out.println(clusters.size());
        for(ArrayList<Integer> cluster: clusters){
            System.out.println(cluster.toString());
        }

    }

}