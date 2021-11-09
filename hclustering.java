import java.util.*;
import java.io.*;

public class hclustering{

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

    public static ArrayList<Node> makeLeafNodes(int n){
        ArrayList<Node> leaves = new ArrayList<Node>(n);
        for(int i = 0; i < n; i++){
            leaves.add(new Node((double)i));
        }
        return leaves;
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
            for(int j = 0; j < i; j++){
                matrix[i][j] = matrix[j][i] = dbscan.getDistance(data.get(i), data.get(j), binaryVector);
            }
            matrix[i][i] = 0;
        }
        return matrix;
    }

    public static double getAverageLink(ArrayList<Node> clusters, int i, int j, double[][] distanceMatrix){
        double runningSum = 0;
        for(int x: clusters.get(i).getLeafNodes()){
            for(int y: clusters.get(j).getLeafNodes()){
                runningSum += distanceMatrix[x][y];
            }
        }
        int sz = clusters.get(i).getLeafNodes().size() * clusters.get(j).getLeafNodes().size();
        return runningSum/sz;
    }

    public Node buildDendrogram(ArrayList<Node> clusters, double[][] distanceMatrix){
        while(clusters.size() > 1){
            continue;
            //identify two closest clusters
        }
        return new Node(0);
    }


    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("usage: java hclustering <Filename> [threshold]");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> data = dbscan.readCSV(args[0]);
        ArrayList<Node> clusters = hclustering.makeLeafNodes(data.size());
        double[][] dm = hclustering.makeDistanceMatrix(data);
        System.out.println(hclustering.getAverageLink(clusters, 0, 1, dm));
    }
}
