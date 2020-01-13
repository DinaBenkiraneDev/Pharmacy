import java.util.*;
import java.io.*;

/**
 * Auteur: Dina Benkirane
 *         Tae-Suzanne Ly
 * But du programme: permet de donner la position du camion initiale et le trajet en ordre des entrepot a visiter
 *                  avec la distance la plus minime.
 */
public class Tp1 {

    //double startOfProg = System.nanoTime();
    //empirical analysis

    private static final double rayon = 6371000; // In meters

    private String inputFilename; //First argument, args[0]
    private String outputFilename; //Second argument, args[1]
    private List<servicePoint> warehouse;
    private servicePoint truckPosition;
    private int boxesNeeded; //Boxes that must be transported
    private int maxBoxes; //Maximum capacity of boxes the truck can handle
    private Queue<servicePoint> toVisit= new LinkedList<>(); //Service point to visit

    private Tp1(String inputFilename, String outputFilename) throws IOException {

        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        warehouse = parse();
        truckPosition = truckPosition();

        setDistance();
        //Partitions to the right

        quickSort(warehouse,0,warehouse.size()-1);

        //Part of code that select the servicePoint to visit.
        int temp = maxBoxes; // To substract the number of boxes of the service point
        for (int i=0; boxesNeeded>0&&maxBoxes>0&&i<warehouse.size();i++) {
            boxesNeeded -= warehouse.get(i).getBox();
            maxBoxes-= warehouse.get(i).getBox();

            //set the remaining boxes left
            warehouse.get(i).setBox(Math.max(0,warehouse.get(i).getBox()-temp));

            //allows to not take more boxes than needed
            if (boxesNeeded<0)
                warehouse.get(i).setBox(Math.abs(boxesNeeded));

            temp=maxBoxes;

            toVisit.add(warehouse.get(i));
        }

        //Creates the result file
        printFile();

        //double endOfProg = System.nanoTime();
        //System.out.println(String.format("%.3f", (endOfProg - startOfProg)/1000000000));
        //empirical analysis
    }

    /**
     * Represents each coordinate given by the file.
     * java class with box info, latitude, longitude and distance.
     */
    public class servicePoint {
        int box;
        double latitude;
        double longitude;
        double distance;

        servicePoint(int box, double latitude, double longitude) {
            this.box = box;
            this.latitude = latitude;
            this.longitude = longitude;
            // to set after when we decide where is the truck
            this.distance = -1.0;
        }
        //Bunch of getter and a setter
        private double getDistance() {
            return this.distance;
        }

        private double getLatitude() {
            return this.latitude;
        }

        private double getLongitude() {
            return this.longitude;
        }

        private int getBox() {
            return this.box;
        }

        private void setBox(int value) {
            this.box = value;
        }

        private void setDistance(double distance) {
            this.distance = distance;
        }
    }

    /**
     *  Reads input file and extract all of the relevant information needed
     * @return list of all servicePoint given by input file
     */
    private List<servicePoint> parse(){
        ArrayList<servicePoint> warehouse = new ArrayList<servicePoint>();
        //servicePoint (element that contains) available boxes, service points (latitude and longitude) and distance
        try {
            FileReader inputFile = new FileReader(inputFilename);
            Scanner in = new Scanner(inputFile);

            String line = in.nextLine();
            Scanner scanLine = new Scanner(line);
            //Scanning the first line and fetching the first 2 information
            boxesNeeded = scanLine.nextInt();
            maxBoxes = scanLine.nextInt();

            if (boxesNeeded > maxBoxes) {
                System.out.println("Truck capacity exceeded, Boxes needed:"+boxesNeeded+" > Truck box capacity:"+maxBoxes);
                System.exit(0);
            }

            //Read the coordinate and the number of boxes available per servicePoint
            while (in.hasNextLine()) {
                line = in.nextLine();
                scanLine = new Scanner(line);

                while (scanLine.hasNextInt()) {
                    int box = scanLine.nextInt();
                    String coordinates = scanLine.next();
                    String[] position = (coordinates.substring(1, coordinates.length() - 1).split(","));

                    double latitude = Double.valueOf(position[0]);
                    double longitude = Double.valueOf(position[1]);

                    servicePoint cargo = new servicePoint(box, latitude, longitude);
                    warehouse.add(cargo);
                }
                scanLine.close();
            }
            in.close();
            inputFile.close();
        } catch (IOException e){
            System.out.println("Input error");
            System.exit(0);
        }
        return warehouse;
    }

    /**
     * @return Truck position
     */
    private servicePoint truckPosition(){
        //Initialise maximum
        servicePoint truckPosition = warehouse.get(0);
        //compare element with current max
        for (int i=1; i<warehouse.size();i++){
            if (warehouse.get(i).getBox()>truckPosition.getBox()) {
                truckPosition = warehouse.get(i);
            }
        }
        return truckPosition;
    }

    /**
     * Sets distance from initial point
     */
    private void setDistance (){
        for (servicePoint point: warehouse) {
            point.setDistance(Harversine(truckPosition,point));
        }
    }

    /**
     *Implements the sorting algorithm (Quicksort)
     */
    private void quickSort(List<servicePoint> array,int start, int end){
        int partition= partition(array, start,end);

        if(partition-1>start){
            quickSort(array,start,partition-1);
        }
        if ((partition+1<end)){
            quickSort(array,partition+1, end);
        }
    }
    private int partition(List<servicePoint> array,int start, int end){

        servicePoint pivot = array.get(end);

        for (int i=start;i<end;i++){
            //Swap the current element if it's smaller than the pivot
            if(warehouse.get(i).getDistance()<pivot.getDistance()){
                servicePoint temp = warehouse.get(start);
                warehouse.set(start,warehouse.get(i));
                warehouse.set(i, temp);
                start++;
            }
        }
        servicePoint temp= warehouse.get(start);
        warehouse.set(start,pivot);
        warehouse.set(end,temp);
        return start;
    }

    /**
     * @return distance between 2 coordinates
     */
    private static double Harversine(servicePoint coord1, servicePoint coord2) {
        double dLat = Math.toRadians(coord2.latitude - coord1.latitude);
        double dLon = Math.toRadians(coord2.longitude - coord1.longitude);
        double lat1 = Math.toRadians(coord1.latitude);
        double lat2 = Math.toRadians(coord2.latitude);

        //Distance calculations
        double toSqrt = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double toAsin = 2 * Math.asin(Math.sqrt(toSqrt));
        return rayon * toAsin;
    }

    private void printFile() throws IOException{
        Writer writer = null;

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFilename);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            writer = new BufferedWriter(outputWriter);

            //Write the truck position
            writer.write("Truck position: ("+truckPosition.getLatitude()+","+truckPosition.getLongitude()+")");

            //Writes all of the servicePoint into the result file

            for (servicePoint point: toVisit) {
                String distanceDecimal = String.format("%.1f", point.getDistance());
                writer.write("\nDistance:"+distanceDecimal+" Number of boxes:"+point.getBox()+
                        " Position:("+point.getLatitude()+","+point.getLongitude()+")");
            }
        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length==2) {
            Tp1 tp1 = new Tp1(args[0], args[1]);
        }

        /* Test */
        /*
        Tp1 tp0 = new Tp1("camion_entrepot0.txt","rest.txt");
        Tp1 tp1 = new Tp1("camion_entrepot1.txt","rest1.txt");
        Tp1 tp2 = new Tp1("camion_entrepot2.txt","rest2.txt");
        Tp1 tp3 = new Tp1("camion_entrepot3.txt","rest3.txt");
        Tp1 tp4 = new Tp1("camion_entrepot4.txt","rest4.txt");
        Tp1 tp5 = new Tp1("camion_entrepot5.txt","rest5.txt");

        /* Empirical Analysis */
        /*
        Tp1 tp0 = new Tp1("100.txt","rest.txt");
        Tp1 tp1 = new Tp1("150.txt","rest1.txt");
        Tp1 tp2 = new Tp1("200.txt","rest2.txt");
        Tp1 tp3 = new Tp1("250.txt","rest3.txt");
        Tp1 tp4 = new Tp1("300.txt","rest4.txt");
        Tp1 tp5 = new Tp1("350.txt","rest5.txt");
        Tp1 tp6 = new Tp1("400.txt","rest6.txt");
        Tp1 tp7 = new Tp1("450.txt","rest7.txt");
        Tp1 tp8 = new Tp1("500.txt","rest8.txt");
        Tp1 tp9 = new Tp1("550.txt","rest9.txt");
        Tp1 tp10 = new Tp1("600.txt","rest10.txt");
        Tp1 tp11 = new Tp1("650.txt","rest11.txt");
        Tp1 tp12 = new Tp1("700.txt","rest12.txt");
        Tp1 tp13 = new Tp1("750.txt","rest13.txt");
        Tp1 tp14 = new Tp1("800.txt","rest14.txt");
        Tp1 tp15 = new Tp1("850.txt","rest15.txt");
        Tp1 tp16 = new Tp1("900.txt","rest16.txt");
        Tp1 tp17 = new Tp1("950.txt","rest17.txt");
        Tp1 tp18 = new Tp1("1000.txt","rest18.txt");
        Tp1 tp19 = new Tp1("1050.txt","rest19.txt");
        Tp1 tp20 = new Tp1("1100.txt","rest20.txt");
        Tp1 tp21 = new Tp1("1150.txt","rest21.txt");
        Tp1 tp22 = new Tp1("1200.txt","rest22.txt");
        Tp1 tp23 = new Tp1("1250.txt","rest23.txt");
        Tp1 tp24 = new Tp1("1300.txt","rest24.txt");
        Tp1 tp25 = new Tp1("1350.txt","rest25.txt");
        Tp1 tp26 = new Tp1("1400.txt","rest26.txt");
        Tp1 tp27 = new Tp1("1450.txt","rest27.txt");
        Tp1 tp28 = new Tp1("1500.txt","rest28.txt");
        Tp1 tp29 = new Tp1("1550.txt","rest29.txt");
        Tp1 tp30 = new Tp1("1600.txt","rest30.txt");
        Tp1 tp31 = new Tp1("1650.txt","rest31.txt");
        Tp1 tp32 = new Tp1("1700.txt","rest32.txt");
        Tp1 tp33 = new Tp1("1750.txt","rest33.txt");
        Tp1 tp34 = new Tp1("1800.txt","rest34.txt");
        Tp1 tp35 = new Tp1("1850.txt","rest35.txt");
        Tp1 tp36 = new Tp1("1900.txt","rest36.txt");
        Tp1 tp37 = new Tp1("1950.txt","rest37.txt");
        Tp1 tp38 = new Tp1("2000.txt","rest38.txt");
        Tp1 tp39 = new Tp1("2050.txt","rest39.txt");
        Tp1 tp40 = new Tp1("2100.txt","rest40.txt");
        Tp1 tp41 = new Tp1("2150.txt","rest41.txt");
        Tp1 tp42 = new Tp1("2200.txt","rest42.txt");
        Tp1 tp43 = new Tp1("2250.txt","rest43.txt");
        Tp1 tp44 = new Tp1("2300.txt","rest44.txt");
        Tp1 tp45 = new Tp1("2350.txt","rest45.txt");
        Tp1 tp46 = new Tp1("2400.txt","rest46.txt");
        Tp1 tp47 = new Tp1("2450.txt","rest47.txt");
        Tp1 tp48 = new Tp1("2500.txt","rest48.txt");
        */
    }
}