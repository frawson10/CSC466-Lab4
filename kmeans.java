import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.Math;  

class kmeans{
    public static void main(String[] args){
        int k = 4;
        ArrayList<ArrayList<String>> D = getData("data/4clusters.csv");
        ArrayList<String> restrictions = D.get(0);
        D.remove(0);
        ArrayList<ArrayList<String>> initialCentroids = selectInitialCentroids(D, k);
        getFinalClusters(D, initialCentroids, k);
    }

    public static void getFinalClusters(ArrayList<ArrayList<String>> D, ArrayList<ArrayList<String>> centroids, int k){
        HashMap<ArrayList<String>, ArrayList<String>> assignments = new HashMap<>();
        HashMap<ArrayList<String>, ArrayList<String>> pastAssignments = null;
        double sse = 999999999;
        double alpha = 5;

        while(true){
            double newSSE = 0;
            for(int i=0; i<D.size(); i++){
                ArrayList<String> closestCentroid = null;
                double closestDistance = 999999999;
                for(int j=0; j<centroids.size(); j++){
                    double distToCentroid = getDistance(D.get(i), centroids.get(j));
                    if(distToCentroid < closestDistance){
                        closestDistance = distToCentroid;
                        closestCentroid = centroids.get(j);
                    }
                }
                assignments.put(D.get(i), closestCentroid);
            }
            ArrayList<ArrayList<String>> newCentroids = new ArrayList<>();
            for(int i=0; i<centroids.size(); i++){
                 ArrayList<String> newCentroid = new ArrayList<>();
                 double numInCluster = 0;
                 for(int j=0; j<centroids.get(0).size(); j++){
                     newCentroid.add("0");
                 }
                 for(Map.Entry<ArrayList<String>, ArrayList<String>> set : assignments.entrySet()){
                    if(set.getValue().equals(centroids.get(i))){
                        numInCluster+=1;
                        for(int j=0; j<set.getValue().size(); j++){
                            double toAdd = Double.parseDouble(newCentroid.get(j)) + Double.parseDouble(set.getKey().get(j));
                            sse += toAdd;
                            newCentroid.set(j, String.valueOf(toAdd));
                        }
                    }
                 }
                 for(int j=0; j<newCentroid.size(); j++){
                     double mean = Double.parseDouble(newCentroid.get(j)) / numInCluster;
                     newCentroid.set(j, String.valueOf(mean));
                 }
                 newCentroids.add(newCentroid);
            }
            // check if we can stop
            // no points reassigned
            if(pastAssignments != null){
                boolean continueLoop = false;
                for(Map.Entry<ArrayList<String>, ArrayList<String>> set : assignments.entrySet()){
                    if(!pastAssignments.get(set.getKey()).equals(set.getValue())){
                        continueLoop= true;
                        break;
                    }
                }
                if(!continueLoop){
                    break;
                }
            }
            // centroids do not move
            boolean continueLoop = false;
            for(int i=0; i<centroids.size(); i++){
                if(!centroids.get(i).equals(newCentroids.get(i))){
                    continueLoop = true;
                }
            }
            if(!continueLoop){
                break;
            }

            // insignificant decrease SSE
            if(Math.abs((sse * sse) - (newSSE * newSSE)) < alpha){
                break;
            }

            pastAssignments = assignments;
            assignments = new HashMap<>();
            centroids = newCentroids;
            sse = newSSE;
        }
        HashMap<ArrayList<String>, ArrayList<ArrayList<String>>> finalClusters = new HashMap<>();
        for(Map.Entry<ArrayList<String>, ArrayList<String>> set : assignments.entrySet()){
            if(finalClusters.get(set.getValue()) == null){
                finalClusters.put(set.getValue(), new ArrayList<>());
            }
            finalClusters.get(set.getValue()).add(set.getKey());
        }
        System.out.println(finalClusters);
        int counter =0;
        for(Map.Entry<ArrayList<String>, ArrayList<ArrayList<String>>> set : finalClusters.entrySet()){
            System.out.println("Cluster "+counter);
            System.out.println("Center: " + set.getKey());
            System.out.println(set.getValue().size() + " points");
            double maxDist = 0;
            double minDist = 999999999;
            double totalDist = 0; 
            for(ArrayList<String> point : set.getValue()){
                System.out.println(point);
                double distToCenter = getDistance(point, set.getKey());
                if(distToCenter > maxDist){
                    maxDist = distToCenter;
                }
                if(distToCenter < minDist){
                    minDist = distToCenter;
                }
                totalDist += distToCenter;
            }
            System.out.println("Avg Dist To Center: "+ totalDist/set.getValue().size());
            System.out.println("Max Dist To Center: "+ maxDist);
            System.out.println("Min Dist To Center: "+ minDist);
            System.out.println("SSE: "+ sse);
            counter+=1;
        }
    }

    public static ArrayList<ArrayList<String>> selectInitialCentroids(ArrayList<ArrayList<String>> D, int k){
        ArrayList<String> mainCentroid = new ArrayList<>();
        for(int i=0; i<D.get(0).size(); i++){
            double dimensionalSum = 0;
            for(int j=0; j<D.size(); j++){
                dimensionalSum += Integer.parseInt(D.get(j).get(i));
            }
            mainCentroid.add(i, String.valueOf(dimensionalSum / D.size()));
        }
        ArrayList<ArrayList<String>> initialCentrtoids = new ArrayList<>();
        while(initialCentrtoids.size() < k){
            ArrayList<String> farthest = null;
            double farthestDistance = 0;
            if(initialCentrtoids.size() == 0){
                for(int i=0; i<D.size(); i++){
                    double distToPoint = getDistance(mainCentroid, D.get(i));
                    if(distToPoint > farthestDistance){
                        farthestDistance = distToPoint;
                        farthest = D.get(i);
                    }
                }
                initialCentrtoids.add(farthest);
                continue;
            }
            for(int i=0; i<D.size(); i++){
                double distToCentroids = 0;
                for(ArrayList<String> centroid : initialCentrtoids){
                    distToCentroids += getDistance(D.get(i), centroid);
                }
                if(distToCentroids > farthestDistance){
                    farthestDistance = distToCentroids;
                    farthest = D.get(i);
                }
            }
            initialCentrtoids.add(farthest);
        }
        return initialCentrtoids;
    }

    public static Double getDistance(ArrayList<String> str1, ArrayList<String> str2){
        ArrayList<Double> p1 = new ArrayList<>();
        ArrayList<Double> p2 = new ArrayList<>();
        for(int i=0; i<str1.size(); i++){
            p1.add(Double.parseDouble(str1.get(i)));
            p2.add(Double.parseDouble(str2.get(i)));
        }
        double sum = 0;
        for(int i=0; i<p1.size(); i++){
            sum += (p1.get(i) - p2.get(i)) * (p1.get(i) - p2.get(i));
        }
        return Math.sqrt(sum);
    }

    public static ArrayList<ArrayList<String>> getData(String path){
        Scanner sc;
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        try {
            sc = new Scanner(new File(path));
            while (sc.hasNextLine()){
                ArrayList<String> lineVals = new ArrayList<>();
                String[] line = sc.nextLine().split(",");
                for(String s : line){
                    lineVals.add(s);
                }
                data.add(lineVals);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }
}