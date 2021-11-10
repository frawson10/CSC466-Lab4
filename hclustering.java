import java.util.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void printJSON(Node tree){
        JSONObject json = tree.toJSON(true);
        System.out.println(json.toString(2));
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
        data.add(0, binaryVector);
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

            //System.out.println(idx1 + " " + idx2);
            Node cluster1 = clusters.get(idx2);
            Node cluster2 = clusters.get(idx1);
            clusters.remove(cluster1);
            clusters.remove(cluster2);
            //System.out.println(cluster1.value + " " + cluster2.value);
            //combine and place in list
            Node combinedCluster = new Node(shortestLink, cluster1, cluster2);
            clusters.add(combinedCluster);
        }
        return clusters.get(0);
    }

    public static ArrayList<Node> splitAtThreshold(Node root, double threshold){
        ArrayList<Node> clusters = new ArrayList<Node>();
        ArrayList<Node> q = new ArrayList<Node>();
        q.add(root);
        while(!q.isEmpty()){
            Node curr = q.remove(0);
            if(!curr.isLeaf() && curr.value > threshold){
                q.add(curr.lChild);
                q.add(curr.rChild);
            }
            else if(!curr.isLeaf() && curr.value < threshold){
                clusters.add(curr);
            }
        }
        return clusters;
    }

    public static int getCentroid(Node cluster, ArrayList<ArrayList<String>> data){
        ArrayList<String> bVector = data.remove(0);

        System.out.println(bVector);

        double[] avgVals = new double[data.get(0).size()];
        for(double v: avgVals) { v = 0; }
        for(int i: cluster.getLeafNodes()){
            ArrayList<String> datapoint = data.get(i);
            for(int j = 0; j < datapoint.size(); j++){
                if(Double.parseDouble(bVector.get(j)) != 0) {
                    avgVals[j] += Double.parseDouble(datapoint.get(j));
                }
            }
        }
        for (double v: avgVals) { v /= cluster.getLeafNodes().size(); }

        int centroidIdx = cluster.getLeafNodes().get(0);
        double minDist = hclustering.getDistance(data.get(centroidIdx), avgVals, bVector);
        for(int i: cluster.getLeafNodes()){
            if(hclustering.getDistance(data.get(i), avgVals, bVector) < minDist){
                centroidIdx = i;
                minDist = hclustering.getDistance(data.get((int) i), avgVals, bVector);
            }
        }

        data.add(0, bVector);
        return centroidIdx;
    }

    public static double getDistance(ArrayList<String> x, double[] avg, ArrayList<String> bVector){
        /**
         * gets the manhattan distance of 2 points, using only attributes indicated in the binary vector
         * note: yet to experiment with other distance calculations
         */
        double distance = 0;
        for(int i = 0; i < bVector.size(); i++){
            if(Double.parseDouble(bVector.get(i)) != 0){
                distance += Math.abs(Double.parseDouble(x.get(i)) - avg[i]);
            }
        }
        return distance;
    }

    public static void printInfo(ArrayList<Node> clusters, double[][] dm,
                                 ArrayList<ArrayList<String>> data){

        int idx = 0;
        double minDist, avgDist, maxDist, currDist, SSE;
        int centroid_idx;
        for(Node cluster: clusters){
            minDist = 99999999;
            maxDist = avgDist = SSE = 0;
            System.out.printf("Cluster %d:\n", idx++);
            centroid_idx = hclustering.getCentroid(cluster, data);
            System.out.printf("Center: %s\n", data.get(centroid_idx + 1));
            for(int i: cluster.getLeafNodes()){
                if(i != centroid_idx){
                    currDist = dbscan.getDistance(data.get(centroid_idx + 1), data.get(i + 1), data.get(0));
                    avgDist += currDist;
                    SSE += Math.pow(currDist, 2);
                    if(currDist < minDist){
                        minDist = currDist;
                    }
                    if(currDist > maxDist){
                        maxDist = currDist;
                    }
                }
            }
            avgDist /= cluster.getLeafNodes().size() - 1;
            System.out.printf("Max Dist. to Center: %f\n", maxDist);
            System.out.printf("Min Dist. to Center: %f\n", minDist);
            System.out.printf("Avg Dist. to Center: %f\n", avgDist);
            System.out.printf("SSE: %f\n", SSE);
            System.out.printf("%d Points:\n", cluster.getLeafNodes().size());
            for(int leafIdx: cluster.getLeafNodes()){
                System.out.println(data.get(leafIdx + 1));
            }
            System.out.println();
        }
    }


    public static void main(String[] args){
        if(args.length == 0){
            System.out.println("usage: java hclustering <Filename> [threshold]");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> data = dbscan.readCSV(args[0]);
        ArrayList<Node> init_clusters = hclustering.makeLeafNodes(data.size() - 1);
        double[][] dm = hclustering.makeDistanceMatrix(data);
        Node root = hclustering.buildDendrogram(init_clusters, dm);

        if(args.length == 2){
            double thresh = Double.parseDouble(args[1]);
            ArrayList<Node> clusters = hclustering.splitAtThreshold(root, thresh);
            hclustering.printInfo(clusters, dm, data);
        }
        printJSON(root);
    }
}
