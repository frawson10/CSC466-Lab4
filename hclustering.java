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
        for(int i = 0; i < matrix.length - 1; i++){
            for(int j = i + 1; j < matrix.length; j++){
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

    public static Node buildDendrogram(ArrayList<Node> clusters, double[][] distanceMatrix){
        while(clusters.size() > 1){
            //identify two closest clusters
            double shortestLink = hclustering.getAverageLink(clusters, 0, 1, distanceMatrix);
            int idx1 = 0;
            int idx2 = 1;
            for(int i = 0; i < clusters.size() - 1; i++){
                for(int j = i + 1; j < clusters.size(); j++){
                    if(hclustering.getAverageLink(clusters, i, j, distanceMatrix) < shortestLink
                        && i != j){
                        idx1 = i;
                        idx2 = j;
                    }
                }
            }

            Node cluster1 = clusters.remove(idx2);
            Node cluster2 = clusters.remove(idx1);
            //combine and place in list
            Node combinedCluster = new Node(shortestLink, cluster1, cluster2);
            clusters.add(combinedCluster);
        }
        return clusters.get(0);
    }


    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("usage: java hclustering <Filename> [threshold]");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> data = dbscan.readCSV(args[0]);
        ArrayList<Node> clusters = hclustering.makeLeafNodes(data.size() - 1);
        double[][] dm = hclustering.makeDistanceMatrix(data);
        Node root = hclustering.buildDendrogram(clusters, dm);
    }
}
