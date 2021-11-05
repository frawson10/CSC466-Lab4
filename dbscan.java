import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.lang.Math;

public class dbscan {

    public static ArrayList<ArrayList<Double>> readCSV(String filename) {

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
        // gets the manhattan distance of 2 points, using only attributes indicated in the binary vector
        double distance = 0;
        for(int i = 0; i < bVector.size(); i++){
            if(bVector.get(i) != 0){
                distance += Math.abs(x.get(i) - y.get(i));
            }
        }
        return distance;
    }

    public static ArrayList<ArrayList<Integer>> getNeighbors(double[][] distanceMatrix, double epsilon){
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

    public static void print2dArrayList(ArrayList<ArrayList<Double>> data) {
        for(int r = 0; r < data.size(); r++) {
            for (int c = 0; c < data.get(0).size(); c++) {
                System.out.print(data.get(r).get(c));
            }
            System.out.println();
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
    }

}