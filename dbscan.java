import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

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

        dbscan.print2dArrayList(data);
    }

}