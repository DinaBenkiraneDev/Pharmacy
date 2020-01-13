/**
 * Auteur: Dina Benkirane et Tae Suzanne-Ly
 * But du programme: permet de gerer des fichier d'entrer et de performer les commandes pour gerer l'inventaire et
 * les commandes d'une pharmacie.
 */

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.Calendar;

public class Tp2 {

    private String inputFilename; //First argument, args[0]
    private String outputFilename; //Second argument, args[1]
    static ArrayList<String> stockPrinter;
    static ArrayList<String> orderPrinter;
    static String date;

    /**
     *
     * @param inputFilename
     * @param outputFilename
     * @throws IOException
     * @throws ParseException
     */
    public Tp2(String inputFilename, String outputFilename) throws IOException, ParseException {

        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        stockPrinter = new ArrayList<>();
        orderPrinter = new ArrayList<>();
        date = null;
        parse();
    }

    /**
     * Drug class for evry type of drug in the program
     */
    public class Drug implements Comparable<Drug>{
        private String name;
        private int quantity;
        private String expiryDate;
        private Date maxDate;

        /**
         *
         * @param name
         * @param quantity
         * @param expiryDate
         */
        private Drug(String name, int quantity, String expiryDate) {
            try {
                this.name = name;
                this.quantity = quantity;
                this.expiryDate = expiryDate;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                this.maxDate = format.parse(expiryDate);
            } catch (ParseException ex) {
                System.out.println(name+quantity+expiryDate);
            }
        }

        /**
         * @param drug
         * @return
         */
        public int compareTo(Drug drug) {
            if (name.equals(drug.name)) {
                if (maxDate.compareTo(drug.maxDate) == 0) {
                    drug.quantity += this.quantity;
                    return 0;
                } else if (drug.quantity == quantity) {
                    if (drug.maxDate.compareTo(maxDate) > 0) return -1;
                    else return 1;
                } else if (drug.quantity > quantity) return -1;
                else return 1;
            } else if (name.compareTo(drug.name) > 0) return 1;
            else return -1;
        }

        /**
         * @param drug
         * @return If the drug has the same name and date, but the quantity is different
         */
        private int compareTo2(Drug drug) {
            if (name.equals(drug.name)) {
                if (drug.quantity <= quantity) {
                    if (drug.maxDate.compareTo(maxDate) <= 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } else if (name.compareTo(drug.name) > 0) return 1;
            else return -1;
        }

        @Override
        public String toString() {
            return this.name + " " +this.quantity + " " + this.expiryDate;
        }

        private String getExpiryDate() {
            return expiryDate;
        }

    }

    /**
     A node of a tree stores a data item and references
     of the child nodes to the left and to the right.
     */
    private class Node {
        /**
         Inserts a new node as a descendant of this node.
         @param newNode the node to insert
         */
        private void addNode(Node newNode) {
            int comp = newNode.data.compareTo(data);
            if (comp < 0) {
                if (left == null) left = newNode;
                else left.addNode(newNode);
            }
            else if (comp > 0) {
                if (right == null) right = newNode;
                else right.addNode(newNode);
            }
        }

        /**
         Prints this node and all of its descendants
         in sorted order.
         */
        private void printNodes() {
            if (left != null)
                left.printNodes();
            stockPrinter.add(data.toString());
            orderPrinter.add(data.name + " " + data.quantity);
            if (right != null)
                right.printNodes();
        }


        private Drug data;
        private Node left;
        private Node right;
    }

    /**
     This class implements a binary search tree whose
     nodes hold objects that implement the Comparable
     interface.
     */
    protected class BinarySearchTree {
        /**
         Constructs an empty tree.
         */
        private BinarySearchTree() {
            root = null;
        }

        /**
         Inserts a new node into the tree.
         @param obj the object to insert
         */
        private void add(Drug obj) {
            Node newNode = new Node();
            newNode.data = obj;
            newNode.left = null;
            newNode.right = null;
            if (root == null) root = newNode;
            else root.addNode(newNode);
        }

        /**
         Tries to find an object in the tree.
         @param obj the object to find
         @return true if the object is contained in the tree
         */
        private boolean findNode(Drug obj) {
            Node current = root;
            while (current != null) {
                int d = current.data.compareTo2(obj);
                if (d == 0) return true;
                else if (d > 0) current = current.left;
                else current = current.right;
            }
            return false;
        }

        /**
         Tries to remove an object from the tree. Does nothing
         if the object is not contained in the tree.
         @param obj the object to remove
         */
        private void remove(Drug obj) {
            // Find node to be removed
            Node toBeRemoved = root;
            Node parent = null;
            boolean found = false;
            while (!found && toBeRemoved != null) {
                int d = toBeRemoved.data.compareTo(obj);
                if (d == 0) found = true;
                else {
                    parent = toBeRemoved;
                    if (d > 0) toBeRemoved = toBeRemoved.left;
                    else toBeRemoved = toBeRemoved.right;
                }
            }

            if (!found) return;

            // toBeRemoved contains obj
            // If one of the children is empty, use the other

            if (toBeRemoved.left == null || toBeRemoved.right == null) {
                Node newChild;
                if (toBeRemoved.left == null)
                    newChild = toBeRemoved.right;
                else
                    newChild = toBeRemoved.left;

                if (parent == null) // Found in root
                    root = newChild;
                else if (parent.left == toBeRemoved)
                    parent.left = newChild;
                else
                    parent.right = newChild;
                return;
            }

            // Neither subtree is empty
            // Find smallest element of the right subtree

            Node smallestParent = toBeRemoved;
            Node smallest = toBeRemoved.right;
            while (smallest.left != null) {
                smallestParent = smallest;
                smallest = smallest.left;
            }

            // smallest contains smallest child in right subtree
            // Move contents, unlink child

            toBeRemoved.data = smallest.data;
            if (smallestParent == toBeRemoved)
                smallestParent.right = smallest.right;
            else
                smallestParent.left = smallest.right;
        }

        /**
         * If we find the same drug in the tree, we update de quantity of the Node
         * @param obj
         */
        private void findNodeAndSub(Drug obj) {
            Node current = root;
            while (current != null) {
                int d = current.data.compareTo2(obj);
                //If current is the same than obj we update it's quantity, by removing the current and replacing it with
                //the updated quantity
                if (d == 0) {
                    int newQuantity = current.data.quantity-obj.quantity;
                    Drug newDrg = new Drug(current.data.name, current.data.quantity-obj.quantity, current.data.expiryDate);
                    remove(current.data);
                    if (newQuantity > 0) {
                        add(newDrg);
                    }
                    current = null;
                }
                else if (d > 0) current = current.left;
                else current = current.right;
            }
        }

        /**
         * Find size of the tree
         */
        private int size() {
            return(size(root));
        }
        /**
         * Gets the size
         * @param node count from
         */
        private int size(Node node) {
            if (node == null) return(0);
            else {
                return(size(node.left) + 1 + size(node.right));
            }
        }

        /**
         * Return list of all node
         */
        private List<Drug> getAllElements() {

            ArrayList<Drug> result = new ArrayList<>();
            Stack<Node> stack = new Stack<>();

            Node p = root;
            //we push all left nodes into the stack
            while(p!=null){
                stack.push(p);
                p=p.left;
            }
            //*Until stack is empty we add all the drug into the result
            while(!stack.isEmpty()){
                Node t = stack.pop();
                result.add(t.data);

                //Check all the right nodes
                t = t.right;
                while(t!=null){
                    stack.push(t);
                    t = t.left;
                }
            }

            return result;
        }

        /**
         Prints the contents of the tree in sorted order.
         */
        private void print() {
            if (root != null)
                root.printNodes();
        }

        private Node root;
    }

    /**
     *
     * @param date
     * @return if the date entered is valid
     */
    private static boolean isValidDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    /*
    public static int daysBetween(String date, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (isValidDate(date) && isValidDate(date2)) {
            try {
                Date currentDate = format.parse(date);
                Date expiryDate = format.parse(date2);
                return (int) ChronoUnit.DAYS.between(currentDate.toInstant(), expiryDate.toInstant());
            } catch (ParseException ex) {
                System.out.println(date + date2);
                return -1;
            }
        } else {
            return -1;
        }
    }*/

    /*
        public static Date makeDate(String date) throws ParseException {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(date);
        }
    */

    /**
     *
     * @param date date to change
     * @param days Adding number of days
     * @return the end date for the treatement
     */
    private static String addDays(String date, int days){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(date));
            c.add(Calendar.DATE, days);
            return date;
        } catch (ParseException ex) {
            System.out.println("Can't add days to " + date);
            return "Can't add days to " + date;
        }
    }

    /**
     * Check if the stock contain any expired drugs
     * @param stock
     */
    private static void removeExpiredDrugs(BinarySearchTree stock){
        //Getting all the element of the tree in a list
        List<Drug> toIterate=stock.getAllElements();
        //Transform date
        LocalDate currDate=LocalDate.parse(date,DateTimeFormatter.ISO_LOCAL_DATE);
        //Check for each element if the expiration date is before the current date of the program
        for (Drug drug:toIterate) {
            LocalDate expiryDate= LocalDate.parse(drug.getExpiryDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (expiryDate.isEqual(currDate)||expiryDate.isBefore(currDate)){
                stock.remove(drug);
            }
        }
    }

    /**
     * Parsing method to read the file and do all the necessary operation.
     * Allows also to write in the result file
     * @throws IOException
     */
    private void parse() throws IOException {

        String action;
        String date2;
        BinarySearchTree stock = new BinarySearchTree();
        BinarySearchTree order = new BinarySearchTree();

        String name;
        int dose;
        int renewal;
        int days;
        int prscrpNb = 0; // Know at what prescription we are at

        //To check if we need to stock or to Order
        boolean toStock = false;
        boolean toOrder = false;

        String outputFilePrint = "";

        try {
            FileReader inputFile = new FileReader(inputFilename);
            Scanner in = new Scanner(inputFile);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                Scanner scanLine = new Scanner(line);

                if (line.isEmpty()) {                                     //ignore les Ns vides
                    continue;
                }
                action = scanLine.next();

                //If we need to stock (FROM APPROV)
                if (toStock) {
                    name = action;
                    //If there was nothing to approv
                    if (name.equals(";")) {
                        toStock = false;
                    } else {
                        dose = scanLine.nextInt();
                        date2 = scanLine.next();
                        //We add to the stock all the drugs received
                        Drug addToStock = new Drug(name, dose, date2);
                        stock.add(addToStock);
                    }
                }

                // If we need to Order (From PRESCRIPTION)
                if (toOrder) {
                    name = action;
                    // If prescription was empty
                    if (name.equals(";")) {
                        outputFilePrint += System.lineSeparator();
                        toOrder = false;
                    } else {
                        dose = scanLine.nextInt();
                        renewal = scanLine.nextInt();
                        days = dose * renewal;
                        date2 = addDays(date, days);
                        //Create a drug for the prescription
                        Drug prescription = new Drug(name, days, date2);
                        //If the drug exist in the stock, we update stock and write OK.
                        if (stock.findNode(prescription)) {
                            stock.findNodeAndSub(prescription);
                            outputFilePrint += name + " " + dose + " " + renewal + "  OK" + System.lineSeparator();
                        } else {
                            //We add the drug into the Order
                            outputFilePrint += name + " " + dose + " " + renewal + "  COMMANDE" + System.lineSeparator();
                            order.add(new Drug(name, days, "0000-00-00"));
                        }
                    }
                }

                //Change of date
                if (action.equals("DATE")) {
                    date = scanLine.next();
                    //If date is invalid
                    if (!isValidDate(date)) {
                        System.out.println("Invalid current date");
                    } else {
                        //If the order is empty
                        if (order.size() == 0) {
                            outputFilePrint += date + " OK" + System.lineSeparator() + System.lineSeparator();
                        } else {
                            outputFilePrint += date + " COMMANDES :" + System.lineSeparator();
                            //Clearing the list
                            orderPrinter.clear();
                            //Print the order
                            order.print();
                            for (Object o : orderPrinter) {
                                outputFilePrint += o + System.lineSeparator();
                            }
                            outputFilePrint += System.lineSeparator();

                        }
                        //Check if something in the stock is expired
                        removeExpiredDrugs(stock);
                    }
                }

                //To print the current stock
                if (action.equals("STOCK")) {
                    //If it's called before a declaration of the date
                    if (date != null) {
                        outputFilePrint += "STOCK " + date + System.lineSeparator();

                        stockPrinter.clear();
                        stock.print();
                        //Print stock
                        for (Object o : stockPrinter) {
                            outputFilePrint += o + System.lineSeparator();
                        }
                        outputFilePrint += System.lineSeparator();

                    } else {
                        System.out.println("STOCK NO DATE");
                    }
                }

                //Print if we need to order or if in stock
                if (action.equals("PRESCRIPTION")) {
                    //Mark at what prescription the file is at
                    prscrpNb++;
                    outputFilePrint += "PRESCRIPTION " + prscrpNb + System.lineSeparator();
                    //Check if we need to order
                    toOrder = true;
                }

                //Update stock
                if (action.equals("APPROV")) {
                    outputFilePrint += "APPROV OK" + System.lineSeparator();
                    toStock = true;
                }

                scanLine.close();
            }
            in.close();
            inputFile.close();
        } catch (IOException e) {
            System.out.println("Input error");
            System.exit(0);
        }


        Writer writer = null;
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFilename);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            writer = new BufferedWriter(outputWriter);
            writer.write(outputFilePrint);

        } catch (IOException e) {
            System.out.println("Output error");
            System.exit(0);
        } finally {
            writer.close();
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length==2) {
            Tp2 tp2 = new Tp2(args[0], args[1]);
        }
        /* TEST
        Tp2 tp1 = new Tp2("exemple1.txt", "lol1.txt");
        Tp2 tp2 = new Tp2("exemple2.txt", "lol2.txt");
        Tp2 tp3 = new Tp2("exemple3.txt", "lol3.txt");
        Tp2 tp4 = new Tp2("exemple4.txt", "lol4.txt");
        Tp2 tp5 = new Tp2("exemple5.txt", "lol5.txt");
        Tp2 tp6 = new Tp2("exemple6.txt", "lol6.txt");
        Tp2 tp7 = new Tp2("exemple7.txt", "lol7.txt");
        Tp2 tp8 = new Tp2("exemple8.txt", "lol8.txt");
        */
    }
}
