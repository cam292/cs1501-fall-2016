import java.io.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import dataStructures.*;

/*
* Main driver for project 4. Creates an adjacency list representation of a graph
* for an airline. Supports following queries:
*   display entire graph
*   display MST based on distances
*   shortest path searches for: mileage, price, num. of connections
*   all trips affordable given certain price
*   add a new route between cities
*   remove a route between cities
*   update the file for graph
*/
public class Airline {
  public static String fileName; //global for file name
  public static String cities[];
  public static EdgeWeightedGraph routes;

  public static void main(String args[]){
    Scanner keyboard = new Scanner(System.in);
    boolean run = true;
    int input = -1;

    System.out.println("\nCraig's Flight Control Unit\n");
    initFile(); //prompt for airline file and populate graph

    while(run){ //program driver loop
      printMenu();
      try{
        input = keyboard.nextInt();
      }
      catch(InputMismatchException e1){}

      switch(input){
        case 0:
          run = false;
          System.out.println("Exiting...");
          exitProto();
          break;
        case 1:
          System.out.println("Selected: See entire list of routes\n");
          listRoutes();
          break;
        case 2:
          System.out.println("Selected: List service routes based on distances\n");
          printMST();
          break;
        case 3:
          System.out.println("Selected: Shortest path search\n");
          shortestPaths();
          break;
        case 4:
          System.out.println("Selected: Flights lower than a price\n");
          listPriUnder();
          break;
        case 5:
          System.out.println("Selected: Add a new route\n");
          addRoute();
          break;
        case 6:
          System.out.println("Selected: Remove an existing route\n");
          removeRoute();
          break;
        default:
          System.out.println("Please enter a number 0-6");
          keyboard.nextLine();
          break;
      }

    }
    return;
  }

  /*
  * Validates that the user inputted file name exists
  * then calls readFile()
  */
  public static void initFile(){
    Scanner keyboard = new Scanner(System.in);
    do{ //prompt for file name
      System.out.println("Please enter the file for routes:");
      fileName = keyboard.nextLine();
      try{
        File file = new File(fileName);
        Scanner infile = new Scanner(file);
        readFile(infile);
        infile.close();
        break;
      }
      catch (FileNotFoundException e){
        System.out.println("File doesn't exist!");
      }
    } while(true);
  }

  /*
  * Scans the airline file and populates the graph
  *
  * @param infile The file scanner for the airline's file
  */
  public static void readFile(Scanner infile){
    int numCities = infile.nextInt(); //get the number of cities
    infile.nextLine();

    routes = new EdgeWeightedGraph(numCities);

    cities = new String[numCities+1];
    for(int i=0; i<numCities;i++){ //get city names
      String city = infile.nextLine();
      cities[i] = city;
    }

    while(infile.hasNextLine()){ //get routes between cities
      String line = infile.nextLine();
      System.out.println(line);
      String split[] = line.split(" ");
      addGraph(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),Double.parseDouble(split[3]));
    }
  }
  /*
  * Given the source and destination cities, add edge to the graph.
  * Serves the purpose of adding new edges and updating current edge values
  *
  * @param src The source city name
  * @param dest The destination city name
  * @param dist The distance weight between src and dest
  * @param pri The price weight between src and dest
  */
  public static void addGraph(int src, int dest, int dist, double pri){
    Edge route = new Edge(src-1, dest-1, dist, pri);
    routes.addEdge(route);
    return;
  }

  /*
  * List all possible routes from current graph
  */
  public static void listRoutes(){
    System.out.println("LIST OF ALL DIRECT ROUTES:\n");

    for(Edge e : routes.edges()){
      String src = cities[e.either()];
      String dest = cities[e.other(e.either())];
      System.out.println("Cost: "+e.pri()+" Path: "+src+" "+e.dist()+" "+dest);
    }
    System.out.println("");
  }

  /*
  * Prints the MST based on distances
  */
  public static void printMST(){
    KruskalMST distMST = new KruskalMST(routes);
    System.out.println("**MST BY ASCENDING DISTANCE***\n");
    for(Edge e : distMST.edges()){
      String src = cities[e.either()];
      String dest = cities[e.other(e.either())];
      System.out.println(src+", "+dest+" : "+e.dist());
    }
    System.out.println("");
  }

  /*
  * Prompts user for which type of shortest path they want,
  * then proceeds to print the path.
  */
  public static void shortestPaths(){
    Scanner keyboard = new Scanner(System.in);
    System.out.println("Find a shortest path based on:");
    System.out.println("(1) Distance");
    System.out.println("(2) Price");
    System.out.println("(3) Hops");

    int mode = keyboard.nextInt();
    keyboard.nextLine();
    System.out.println("Source city:");
    String tempSrc = keyboard.nextLine();
    System.out.println("Destination city:");
    String tempDest = keyboard.nextLine();

    int src=0,dest=0;
    for(int i=0;i<cities.length;i++){
      if(tempSrc.equals(cities[i])){
        src = i;
      } else if(tempDest.equals(cities[i])){
        dest = i;
      }
      if(src != 0 && dest !=0)  break;
    }

    DijkstraSP sp = new DijkstraSP(routes, src, mode);
    if(sp.hasPathTo(dest)){
      switch(mode){
        case 1:
          System.out.println("\nSHORTEST DISTANCE PATH from "+tempSrc+" to "+tempDest+"\n");
          System.out.println("Total distance: "+sp.distTo(dest));
          break;
        case 2:
          System.out.println("\nLowest COST PATH from "+tempSrc+" to "+tempDest+"\n");
          System.out.println("Total price: "+sp.distTo(dest));
          break;
        case 3:
          System.out.println("\nFEWEST HOPS PATH from "+tempSrc+" to "+tempDest+"\n");
          System.out.println("Total hops: "+sp.distTo(dest));
          break;
      }
      System.out.print("Path: ");
      int prev = src;
      for(Edge e : sp.pathTo(dest)){
        if(e.either() == src)   System.out.print(cities[prev]+" ");
        else if(e.other(e.either()) == src){
          prev = e.other(e.either());
          System.out.print(cities[prev]+" ");
        }
        switch(mode){
          case 1:
            System.out.print(e.dist()+" "+cities[e.other(prev)]+" ");
            break;
          case 2:
            System.out.print(e.pri()+" "+cities[e.other(prev)]+" ");
            break;
          case 3:
            System.out.print(" "+cities[e.other(prev)]+" ");
            break;
        }
        prev = e.other(prev);
      }
      System.out.println("");
    } else {
      System.out.println("No path found from "+tempSrc+" to "+tempDest+"!");
    }

    return;

  }

  /*
  * Prompts user for spending limit then prints all possible Flights
  * under that price limit.
  */
  public static void listPriUnder(){
    Scanner keyboard = new Scanner(System.in);

    System.out.println("Enter max travel price:");
    Double limit = keyboard.nextDouble();
    keyboard.nextLine();

    StringBuilder sb = new StringBuilder();
    sb.append("\n***All routes "+limit+" or less***\n");
    int V = routes.V();
		Edge[] edgeTo;
		boolean[] marked;
		for (int i = 0; i < V; i++){
			marked = new boolean[V];
			edgeTo = new Edge[V];
			marked[i] = true;
			listPriUnder(i, edgeTo, 0, limit, sb, marked);
      marked[i] = false;
		}
    System.out.println(sb.toString());
    return;
  }

  /*
  * Recursive function to find all possible paths from a given vertex
  *
  * @param vertex The vertex in question
  * @param edgeTo Array containing edges. Indicies are edge vertices
  * @param pri Total price of current path
  * @param priLim User entered price limit
  * @param sb The StringBuilder object for all routes < priLim
  */
  public static void listPriUnder(int vertex,Edge[] edgeTo, double pri,double priLim,StringBuilder sb, boolean[] marked){
    for(Edge e : routes.adj(vertex)){
      int w = e.other(vertex);
      if(!marked[w] && pri+e.pri() <= priLim){
        edgeTo[w] = e;
        marked[w] = true;
        appendRoute(sb,w,edgeTo,pri+e.pri());
        listPriUnder(w,edgeTo, pri+e.pri(), priLim, sb, marked);
        edgeTo[w] = null;
        marked[w] = false;
      }
    }
    return;
  }

  /*
  * Helper function to add routes to list for listPriUnder()
  *
  * @param sb The StringBuilder object for all routes < price limit
  * @param end Last vertex to add
  * @param edgeTo Array containing edges. Indicies are edge verticies
  * @param pri Total cost for current route
  */
  public static void appendRoute(StringBuilder sb, int end, Edge[] edgeTo, double pri){
    sb.append("Cost: "+pri+" Route: ");
    int vert = end;
    while(edgeTo[vert] != null){
      String city = cities[vert];
      double cost = edgeTo[vert].pri();
      sb.append(city+" "+cost+" ");
      vert = edgeTo[vert].other(vert);
    }
    String s = cities[vert];
    sb.append(s + "\n");
    return;
  }
  /*
  * Goes through exit protocol of printing the graph back to the
  * text file
  */
  public static void exitProto(){
    int numCities = cities.length - 1;
    boolean invalid = true;
    do {
      try {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file, false);
        invalid = false;

        writer.write(numCities+"\n");
        for(int i=0;i<cities.length-1;i++){
          writer.write(cities[i]+"\n");
        }
        for(Edge e : routes.edges()){
          writer.write((e.either()+1)+" "+(e.other(e.either())+1)+" "+e.dist()+" "+e.pri()+"\n");
        }

        writer.close();
      }
      catch (IOException e1) {}
    } while (invalid);
  }

  /*
  * Prompts user for the cities, distance, and price for the route
  *   Function assumes the cities already exist
  */
  public static void addRoute(){
    Scanner keyboard = new Scanner(System.in);
    int src=0,dest=0;

    System.out.println("Source city:");
    String tempSrc = keyboard.nextLine();

    System.out.println("Destination city:");
    String tempDest = keyboard.nextLine();

    System.out.println("Distance between cities:");
    int dist = keyboard.nextInt();
    keyboard.nextLine();

    System.out.println("Price for travel:");
    double pri = keyboard.nextDouble();

    for(int i=0;i<cities.length;i++){
      if(tempSrc.equals(cities[i])){
        src = i+1;
      } else if(tempDest.equals(cities[i])){
        dest = i+1;
      }
    }

    addGraph(src, dest, dist, pri);
  }

  /*
  * Prompts for source and destination cities, then removes that edge
  */
  public static void removeRoute(){
    Scanner keyboard = new Scanner(System.in);
    int src=0,dest=0;

    System.out.println("Source city:");
    String tempSrc = keyboard.nextLine();

    System.out.println("Destination city:");
    String tempDest = keyboard.nextLine();

    for(int i=1;i<cities.length;i++){
      if(tempSrc.equals(cities[i])){
        src = i;
      } else if(tempDest.equals(cities[i])){
        dest = i;
      }
    }

    for(Edge e : routes.adj(src)){
      if(e.other(src)==dest){
        routes.removeEdge(e);
        break;
      }
    }

  }
  /*
  * Helper function to print menu to user
  */
  public static void printMenu(){
    System.out.println("\nWould you like to:");
    System.out.println("(1) See entire list of routes");
    System.out.println("(2) List service routes based on distances");
    System.out.println("(3) Shortest path search (distance, price, or hops)");
    System.out.println("(4) Flights lower than a price");
    System.out.println("(5) Add a new route");
    System.out.println("(6) Remove an existing route");
    System.out.println("(0) Exit");
    return;
  }
}
