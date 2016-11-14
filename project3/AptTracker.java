import java.io.*;
import java.util.*;
import dataStructures.*;

/**
* This is the main driver program for project 3. It prompts a user to:
*   add an apartment, update an apartment, remove an apartment,get the lowest
*   price apartment, get the highest square footage apartment, get the lowest
*   price by city, get the highest square footage by city, or exit.
*
* cs1501 fall 2016
* @author Craig Mazzotta
*/

public class AptTracker{
  public static indexMaxPQ sfPQ = new indexMaxPQ(100);
  public static indexMinPQ pricePQ = new indexMinPQ(100);

  public static void main(String args[]){
    Scanner keyboard = new Scanner(System.in);
    boolean run = true;
    int input = -1;

    while(run){
      printMenu();
      try{
        input = keyboard.nextInt();
      }
      catch(InputMismatchException e1){}

      switch(input){
        case 0:
          run = false;
          System.out.println("Exiting...");
          break;
        case 1:
          System.out.println("Selected: Add an apartment\n");
          addApt();
          break;
        case 2:
          System.out.println("Selected: Update an apartment\n");
          updateApt();
          break;
        case 3:
          System.out.println("Selected: Remove an apartment\n");
          removeApt();
          break;
        case 4:
          System.out.println("Selected: Get the lowest price apartment\n");
          lowPri();
          break;
        case 5:
          System.out.println("Selected: Get the highest square footage apartment\n");
          highSf();
          break;
        case 6:
          System.out.println("Selected: Get the lowest price by city\n");
          lowPriCity();
          break;
        case 7:
          System.out.println("Selected: Get the highest sqare footage by city\n");
          highSfCity();
          break;
        default:
          System.out.println("Please enter a number 0-7");
          keyboard.nextLine();
          break;
      }
    }

  }

  /*
  * This function prompts the user for an address, apartment number,
  * city, ZIP code, price to rent, and square footage for an apartment and
  * initializes an apartment object with the given data. It then
  * adds the apartment object to the minPQ based on price,
  * adds it to maxPQ based on square footage, and places the item in
  * the indexList.
  */
  public static void addApt(){
    String address=null, city=null;
    int num=0, zip=0, sf=0, price=0;
    Scanner keyboard = new Scanner(System.in);

    boolean addNew = true;
    char add = 'y';
    while(addNew){
      System.out.print("Address: ");
      address = keyboard.nextLine().toLowerCase();

      System.out.print("Apartment number: ");
      while(true){
        try{
          num = keyboard.nextInt();
          keyboard.nextLine();
          break;
        }
        catch(InputMismatchException e1){
          System.out.print("Apartment number must be type <int>: ");
          keyboard.next();
        }
      }

      System.out.print("City: ");
      city = keyboard.nextLine().toLowerCase();

      System.out.print("ZIP code: ");
      while(true){
        try{
          zip = keyboard.nextInt();
          keyboard.nextLine();
          break;
        }
        catch(InputMismatchException e1){
          System.out.print("ZIP code must be type <int>: ");
          keyboard.next();
        }
      }

      System.out.print("Price to rent per month: ");
      while(true){
        try{
          price = keyboard.nextInt();
          keyboard.nextLine();
          break;
        }
        catch(InputMismatchException e1){
          System.out.print("Price must be type <int>: ");
        }
      }

      System.out.print("Square footage: ");
      while(true){
        try{
          sf = keyboard.nextInt();
          keyboard.nextLine();
          break;
        }
        catch(InputMismatchException e1){
          System.out.print("Sqaure footage must be type <int>: ");
        }
      }

      APT apt = new APT(address, num, city, zip, price, sf); //create new APT object for given parameters
      //insert APT into sfPQ and pricePQ
      sfPQ.insert(sfPQ.size(), apt);
      pricePQ.insert(pricePQ.size(),apt);

      System.out.print("\nAdd another apartment? ('y' or 'n'): ");
      while(true){
        add = keyboard.next().charAt(0);
        if(add=='y'){
          keyboard.nextLine();
          break;
        } else if (add =='n'){
          addNew = false;
          keyboard.nextLine();
          break;
        } else {
          System.out.print("Please enter 'y' or 'n': ");
        }
      }
    }

    return;
  }

  /*
  * This function prompts the user for a street address, apartment number,
  * and zip code of an apartment, then asks if the user would like to
  * update its price.
  */
  public static void updateApt(){
    Scanner keyboard = new Scanner(System.in);
    String address = null;
    int num=0,zip=0;

    System.out.print("Address: ");
    address = keyboard.nextLine().toLowerCase();

    System.out.print("Apartment number: ");
    while(true){
      try{
        num = keyboard.nextInt();
        keyboard.nextLine();
        break;
      }
      catch(InputMismatchException e1){
        System.out.print("Apartment number must be type <int>: ");
        keyboard.next();
      }
    }
    System.out.print("ZIP code: ");
    while(true){
      try{
        zip = keyboard.nextInt();
        keyboard.nextLine();
        break;
      }
      catch(InputMismatchException e1){
        System.out.print("ZIP code must be type <int>: ");
        keyboard.next();
      }
    }
    int i=0;
    boolean found = false;
    APT tempApt = new APT();
    while(i<pricePQ.size() && !found){
      tempApt = pricePQ.aptOf(i);
      if(address.equals(tempApt.getAddress()) && num == tempApt.getNum() && zip == tempApt.getZip()){
        found = true;
      } else{
        i++;
      }
    }
    if(found){ //prompt for new price
      System.out.println("\nCurrent price/month for desired apartment: $"+tempApt.getPrice());
      System.out.print("Update the price? ('y' or 'n'): ");
      char upd;
      while(true){
        upd = keyboard.next().charAt(0);
        if(upd=='y'){
          keyboard.nextLine();
          int newPrice=0;
          System.out.print("New Price: ");
          while(true){
            try{
              newPrice = keyboard.nextInt();
              keyboard.nextLine();
              break;
            }
            catch(InputMismatchException e1){
              System.out.print("Price must be type <double>: ");
            }
          }
          tempApt.setPrice(newPrice);
          sfPQ.update(i,tempApt);
          pricePQ.update(i,tempApt);
          System.out.println("\nPrice successfully updated");
          break;
        } else if (upd =='n'){
          keyboard.nextLine();
          break;
        } else {
          System.out.print("Please enter 'y' or 'n': ");
        }
      }
    } else {
      System.out.println("\nThat apartment is not in the system");
    }

    System.out.println("*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function will prompt the user for a street address, apartment
  * number, and zip code of an apartment to remove from the PQs.
  */
  public static void removeApt(){
    Scanner keyboard = new Scanner(System.in);
    String address = null;
    int num=0,zip=0;

    System.out.print("Address: ");
    address = keyboard.nextLine().toLowerCase();

    System.out.print("Apartment number: ");
    while(true){
      try{
        num = keyboard.nextInt();
        keyboard.nextLine();
        break;
      }
      catch(InputMismatchException e1){
        System.out.print("Apartment number must be type <int>: ");
        keyboard.next();
      }
    }
    System.out.print("ZIP code: ");
    while(true){
      try{
        zip = keyboard.nextInt();
        keyboard.nextLine();
        break;
      }
      catch(InputMismatchException e1){
        System.out.print("ZIP code must be type <int>: ");
        keyboard.next();
      }
    }

    boolean found = false;
    APT tempApt = new APT();
    for(int i=0; i<sfPQ.size();i++){
      tempApt = sfPQ.aptOf(i);
      if(address.equals(tempApt.getAddress()) && num == tempApt.getNum() && zip == tempApt.getZip()){
        sfPQ.delete(i);
        pricePQ.delete(i);
        System.out.println("\nSuccessfully deleted the apartment");
        found=true;
      }
    }
    if(!found){
      System.out.println("\nThat apartment is not in the system.");
    }
    System.out.println("*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function provides the user with information on the apartment
  * with the overall lowest price.
  */
  public static void lowPri(){
    Scanner keyboard = new Scanner(System.in);
    try{
      APT apt = pricePQ.minApt();
      System.out.println("Apartment with the lowest price:");
      printAPT(apt);
    }
    catch(NoSuchElementException e1){
      System.out.println("Please add apartments to the system.");
    }

    System.out.println("\n*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function provides the user with information on the apartment with
  * the overall highest square footage.
  */
  public static void highSf(){
    Scanner keyboard = new Scanner(System.in);
    try{
      APT apt = sfPQ.maxApt();
      System.out.println("Apartment with the highest square footage:");
      printAPT(apt);
    }
    catch(NoSuchElementException e1){
      System.out.println("Please add apartments to the system.");
    }
    System.out.println("\n*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function provides the user with information on the apartment
  * with the lowest price in the user entered city.
  */
  public static void lowPriCity(){
    Scanner keyboard = new Scanner(System.in);
    APT apt = new APT();

    System.out.print("City: ");
    String city = keyboard.nextLine().toLowerCase();

    int i= 0;
    boolean found = false;
    APT tempApt = new APT();
    while(i<pricePQ.size() && !found){
      for(int j=i; j<(2*i + 1);j++){ //iterate through level to find city match with lowest price
        try{
          tempApt = pricePQ.aptOf(j);
        }
        catch(NoSuchElementException e1){
          break;
        }
        if(city.equals(tempApt.getCity())){
          found = true;
          if(apt.getPrice()==0){ //first occurence of the city
            apt = tempApt;
          }else if(apt.getPrice() > tempApt.getPrice()){ //another occurence of the city
            apt = tempApt;
          }
        }
      }
      i = 2 * i + 1; //increment to next level
    }

    if(!found){
      System.out.println("\nNo apartment found in that city!");
    }else{
      city = Character.toUpperCase(city.charAt(0)) + city.substring(1);
      System.out.println("\nApartment with the lowest price in "+ city+":");
      printAPT(apt);
    }
    System.out.println("\n*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function provides the user with information on the apartment with
  * the highest square footage in the user entered city.
  */
  public static void highSfCity(){
    Scanner keyboard = new Scanner(System.in);
    APT apt = new APT();

    System.out.print("City: ");
    String city = keyboard.nextLine().toLowerCase();

    int i=0;
    boolean found = false;
    APT tempApt = new APT();
    while(i<sfPQ.size() && !found){
      for(int j=i;j<(2*i + 1);j++){
        try{
          tempApt = sfPQ.aptOf(j);
        }
        catch(NoSuchElementException e1){
          break;
        }
        if(city.equals(tempApt.getCity())){
          found=true;
          if(apt.getSf()==0){
            apt = tempApt;
          } else if(apt.getSf() < tempApt.getSf()){
            apt = tempApt;
          }
        }
      }
      i = 2 * i + 1;
    }

    if(!found){
      System.out.println("\nNo apartment found in that city!");
    }else{
      city = Character.toUpperCase(city.charAt(0))+city.substring(1);
      System.out.println("\nApartment with the highest square footage in "+city+":");
      printAPT(apt);
    }
    System.out.println("\n*Press enter to continue*");
    keyboard.nextLine();
    return;
  }

  /*
  * This function prints menu options to user.
  */
  public static void printMenu(){
    System.out.println("\nWould you like to:");
    System.out.println("(1) Add an apartment");
    System.out.println("(2) Update an apartment");
    System.out.println("(3) Remove an apartment");
    System.out.println("(4) Get the lowest price apartment");
    System.out.println("(5) Get the highest sqare footage apartment");
    System.out.println("(6) Get the lowest price by city");
    System.out.println("(7) Get the highest sqaure footage by city");
    System.out.println("(0) Exit");
    return;
  }
  /*
  * This helper function prints an APT object's fields
  * @param apt The APT object to be printed
  */
  public static void printAPT(APT apt){
    System.out.println("Address:\t"+apt.getAddress());
    System.out.println("Apartment:\t"+apt.getNum());
    System.out.println("City:\t\t"+apt.getCity());
    System.out.println("Zip:\t\t"+apt.getZip());
    System.out.println("Price:\t\t$"+apt.getPrice());
    System.out.println("Square Footage:\t"+apt.getSf());
    return;
  }
}
