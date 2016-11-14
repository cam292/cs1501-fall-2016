
import java.io.*;
import java.util.Scanner;
import dataStructures.*;

/**
* The class pw_check can be run with the argument of "-find" or "-find";
*
* -find generates all valid passwords, storing them in an RWay trie.
* Methods are genPasswords, validatePassword, hasWord, and storePass.
*
* -check prompts the user for a password, validates it, then attempts to crack it
* if the password is invalid, it will return a list of 10 passwords sharing a
* prefix with the password entered.
* Methods are getPass, runCheck, and checkPass
*
* createDictionary() is used by both -find and -check to populate the DLB with values
*
* @author Craig Mazzotta
*/

public class pw_check {
  public static RWay<Double> validPasswords = new RWay<Double>(); //symbol tree containing valid passwords
  public static final Alphabet characters = new Alphabet("abcdefghijklmnopqrstuvwxyz0123456789!@$^_*"); //valid characters for a given password
  public static DLB dictionary = new DLB(); //DLB trie containing words from dictionary.txt

  /**
  * @param args Command-line arguments. must be "-find" or "-check".
  *     "-find" must be run first
  * @throws IllegalARgumentException if the argument wasn't -find or -check
  * @throws ArrayIndexOutOfBoundsException if no argument was passed
  */
  public static void main(String[] args ){
    try {
    //Handle the argument passed to begin
      if(args[0] != null){}
    }
    catch (IllegalArgumentException e) {
      System.out.println("You must pass either '-find' or '-check' as your argument to begin1");
      System.exit(1);
    }
    catch (ArrayIndexOutOfBoundsException ee){
      System.out.println("You must pass either '-find' or '-check' as your argument to begin2");
      System.exit(1);
    }

    switch(args[0]){
      case "-find":
        System.out.println("Generating passwords...");
        createDictionary();
        genPasswords();
        System.out.println("All valid passwords have been generated!");
        break;
      case "-check":
        if(runCheck()){ //-find was already run
          createDictionary();
          getPasswords();
          System.out.println("Generating symbol table for valid passwords...");
          checkPass();
        } else {
          System.out.println("You must run '-find' before '-check'");
        }
        break;
      default:
        System.out.println("You must pass either '-find' or '-check' as your argument to begin1");
      }
  }

  /**
  * This method opens the dictionary.txt file and reads in
  * each line (word) and adds it to the DLB trie implementation.
  *
  * @throws FileNotFoundEXception If file does not exist in the directory
  */
  public static void createDictionary() {
    boolean invalid;
    do {
      try {
        File file = new File("dictionary.txt");
        Scanner infile = new Scanner(file);
        invalid = false;
        while (infile.hasNextLine()) {
          String word = infile.nextLine();
          boolean added = dictionary.add(word);
        }
        infile.close();

      }
      catch (FileNotFoundException e) {
        invalid = true;
      }
    } while (invalid);
  }

  /**
  * Opens the all_passwords.txt file and reads in each line and stores
  * the key,value pair in the RWay trie as (password,time);
  */
  public static void getPasswords() {
    boolean invalid;
    String line;
    String split[];
    int comma;
    String pass;
    double time;
    do {
      try {
        File file = new File("all_passwords.txt");
        Scanner infile = new Scanner(file);
        invalid = false;
        while (infile.hasNextLine()) {
          line = infile.nextLine(); //read in whole line
          split = line.split(","); //splits line in two
          pass = split[0]; //password is index 0
          time = Double.parseDouble(split[1]); //time is index 1
          validPasswords.put(pass,time);
        }
        infile.close();
      }
      catch (FileNotFoundException e) {
        invalid = true;
      }
    } while (invalid);
  }

  /**
  * This function uses exhaustive search to populate an RWay trie with
  * all possible password combinations.
  *
  * @param dictionary The DLB trie dictionary to pass into validatePassword()
  * @return validPass The RWay trie containing all valid passwords
  */
  public static void genPasswords(){
    StringBuilder attempt = new StringBuilder(5);
    long startTime = System.nanoTime(); //begin timing password generation
    for(int i = 0; i < 42; i++){ //first char in password
      // if(characters.toChar(i)=='a' || characters.toChar(i)=='i' || characters.toChar(i)=='1' || characters.toChar(i)=='4'){
      //   continue;
      // }else{
        attempt.replace(0,1,String.valueOf(characters.toChar(i)));
      //}
       for(int j = 0; j < 42; j++){ //second char
        //  if(characters.toChar(j)=='a' || characters.toChar(j)=='i' || characters.toChar(j)=='1' || characters.toChar(j)=='4'){
        //    continue;
        //  }else{
           attempt.replace(1,2,String.valueOf(characters.toChar(j)));
         //}
        for(int k = 0; k < 42; k++){ //third char
          // if(characters.toChar(k)=='a' || characters.toChar(k)=='i' || characters.toChar(k)=='1' || characters.toChar(k)=='4'){
          //   continue;
          // }else{
            attempt.replace(2,3,String.valueOf(characters.toChar(k)));
          //}
          for(int l = 0; l < 42; l++){ //fourth char
            // if(characters.toChar(l)=='a' || characters.toChar(l)=='i' || characters.toChar(l)=='1' || characters.toChar(l)=='4'){
            //   continue;
            // }else{
              attempt.replace(3,4,String.valueOf(characters.toChar(l)));
            // }
            for(int m = 0; m < 42; m++){ //fifth char
              // if(characters.toChar(m)=='a' || characters.toChar(m)=='i' || characters.toChar(m)=='1' || characters.toChar(m)=='4'){
              //   continue;
              // }else{
                attempt.replace(4,5,String.valueOf(characters.toChar(m)));
              //}
              if(validatePassword(attempt)){
                double estimatedTime = ((System.nanoTime() - startTime)/1000000.0); //calculate time to find the password
                String password = attempt.toString();
                validPasswords.put(password, estimatedTime);
                storePass(password, estimatedTime);
              }
            }
          }
        }
       }
    }
  }

  /**
  * This function takes in a password and validates it.
  * The password must be 5 characters long containing:
  *   1-3 letters
  *   1-2 numbers (0-9)
  *   1-2 symbols ("!", "@", "$", "^", "_", or "*")
  * and can't contain numbers for letters ("7" for "t", "4" for "a", "0" for "o", "3" for "e", "1" for "i", "1" for "l", or "$" for "s") to form a word
  *
  * @param password The password to be validated
  * @param characters The object containing valid characters.
  * @param dictionary The dictionary with words a password can't contain
  * @return True if the password is valid, otherwise false
  */
  public static boolean validatePassword(StringBuilder password) {
    int let = 0; //number of letters in the given password
    int num = 0; //number of numbers in the given password
    int sym = 0; //number of symbols in the given password
    Queue<Integer> breakPts = new Queue<Integer>(); //store location of word break points
    Queue<Integer> oneIndex = new Queue<Integer>(); //location of 1's in password to be used as 'i' and 'l' for hasWord()

    if(password.length() > 5 || password.length() < 5){ //password is of incorrect length
      return false;
    }

    boolean afterLetter = true; //true if previous letter was a letter or it's the start of the word
    int checkLnI=0;
    for(int i = 0; i < 5; i++){ //iterate through each character of the passsword to count occurences
      char c = password.charAt(i);
      if(characters.toIndex(c) <= 25) { //indices [0,25] are letters
        let++;
        afterLetter = true;
      } else if (characters.toIndex(c) <= 35){ //indices [26,35] are numbers
        num++;
        if(afterLetter){ //change value of c to corresponding letter
          switch(c){
            case '7':
              password.setCharAt(i, 't');
              break;
            case '4':
              password.setCharAt(i, 'a');
              break;
            case '0':
              password.setCharAt(i, 'o');
              break;
            case '3':
              password.setCharAt(i, 'e');
              break;
            case '1':
              oneIndex.enqueue(i); //store location of 1
              break;
            default: //the num was not changed, store index
              afterLetter = false;
              breakPts.enqueue(i);
              break;
          }
        }
      } else if (characters.toIndex(c) <= 41) { //indices [36,41] are symbols
        sym++;
        switch(c){
          case '$': //'$' can be substituted to form a word
            break;
          default: //the sym was not changed, store index
            breakPts.enqueue(i);
        }
        afterLetter = false;
      } else {
        return false; //password contains an INVALID character
      }
    }

    if (let > 3 || let < 1){ //password contains INVALID number of letters
      return false;
    }
    if (num > 2 || num < 1){ //password contains INVALID number of numbers
      return false;
    }
    if((let + num) >= 5){ //contains valid let & num
      return false;
    }
    if (sym > 2 || sym < 1){ //password contains INVALID number of symbols
      return false;
    } else {  //pasword contains a VALID number of each type of character
      if (hasWord(password, breakPts, oneIndex)){ //password contains a word, therefore it's INVALID
        return false;
      } else {
        return true;
      }
    }

  }

  /**
  * This function does a dictionary lookup on a possible word.
  * Possible words are from 0 to breakPt[0], between each value in breakPt
  * and the last index of breakPt to 4.
  *
  * @param password The password being checked for a word from validatePassword()
  * @param breakPts Queue containing location of symbols/nums within password
  * @param dictionary The DLB trie containing words for the comparison
  * @param oneIndex Queue containing location of a '1' in the word
  * @return hasWord True if the password contains a word
  */
  public static boolean hasWord(StringBuilder password, Queue<Integer> breakPts, Queue<Integer> oneIndex){
    boolean hasWord = false;
    int divides = breakPts.size(); //number of break points in password
    Queue<String> check = new Queue<String>(); //queue to store words to lookup

    int j = 0; //index to start dictionary lookup
    for (int i = 0; i < divides; i++){
      int wordEnd = breakPts.dequeue(); //index of next break point
      if(wordEnd == 0){ //first character is a break point
        j++;
      } else {
        for(int k=j; k<wordEnd+1; k++){ //generate all substrings between break points
          int end = k+1;
          while(end<wordEnd+1){
            check.enqueue(password.substring(k,end));
            if(oneIndex.size() > 0){
              int index = oneIndex.peek();
              password.replace(index,index+1, "i"); //substitue i for 1 and add substring
              check.enqueue(password.substring(k,end));
              password.replace(index, index+1, "l"); //substitute l for 1 and add substring
              check.enqueue(password.substring(k,end));
              password.replace(index, index+1, "1"); //restore originial word
            }
            end++;
          }
          if(oneIndex.size()>0){
            oneIndex.dequeue();
          }
        }
      }
      j = wordEnd + 1;
    }
    //lookup each possible word found in previous for loop
    for(int k = 0; k < check.size()-1; k++) {
      StringBuilder word = new StringBuilder(check.dequeue());
      int result = dictionary.search(word);
      if (result == 0 || result == 1){ //string in question is not a word
        hasWord = false;
      } else if (result == 2 || result == 3) { //string in question is a word
        hasWord = true;
        k = check.size(); //found a word leave loop
      }
    }
    return hasWord;
  }

  /**
  * This function takes a password(key) and time(value) pair and stores
  * it in "all_passwords.txt" in the format password,time.
  *
  * @param password The valid password found
  * @param time The time it took to find the password
  * @throws IOException If file can't be written to
  */
  public static void storePass(String password, double time) {
    boolean invalid;
    int numLines = 0;
    do {
      try {
        File file = new File("all_passwords.txt");
        FileWriter writer = new FileWriter(file, true);
        invalid = false;

        writer.write(password+","+time+"\n");
        writer.close();
      }
      catch (IOException e1) {
        invalid = true;
      }
    } while (invalid);
  }

  /**
  * Checks if -find was run before -check.
  * i.e. "all_passwords.txt" exists
  *
  * @return True if all_passwords.txt exists. false otherwise
  */
  public static boolean runCheck() {
    try{
      File file = new File("all_passwords.txt");
      Scanner infile = new Scanner(file);
      return true;
    }
    catch(FileNotFoundException e){ //file doesn't exist
      return false;
    }
  }

  /**
  * Prompts user for a password and validates it until they wish to stop
  *
  */
  public static void checkPass() {
    Scanner keyboard = new Scanner(System.in);
    StringBuilder userPass = new StringBuilder();
    double time;
    boolean cont;
    do {
      System.out.println("Enter a password: ");
      String input = keyboard.nextLine();
      input = input.toLowerCase();
      userPass.replace(0,5,input);

      if(validatePassword(userPass)){ //user entered a valid password
        time = validPasswords.get(input); //return time it took to guess
        System.out.println("It took " + time + "ms to crack your password!");
      } else { //password is invalid, return 10 passwords with a shared prefix
        System.out.println("Invalid password, here are 10 similar passwords:");
        StringBuilder prefix = new StringBuilder(validPasswords.longestPrefixOf(input));
        Queue<String> sharedPrefixes = new Queue<String>();
        sharedPrefixes = validPasswords.keysWithPrefix(prefix.toString());

        while (sharedPrefixes.size() < 10){ //not 10 passwords present, so search a smaller prefix
          try{
            prefix.deleteCharAt(prefix.length()-1);
            sharedPrefixes = validPasswords.keysWithPrefix(prefix.toString());
          }
          catch(StringIndexOutOfBoundsException e){
            sharedPrefixes = validPasswords.keys();
            break;
          }
        }
        for(int j = 0; j<10; j++){ //print 10 passwords to the user
          System.out.println("  "+sharedPrefixes.peek()+" took "+ validPasswords.get(sharedPrefixes.dequeue()) + " ms to crack");
        }
      }

      System.out.println("Would you like to enter another password? (y or n)"); //prompt to try again
      input = keyboard.nextLine();
      if (input.equals("y")){
        cont = true;
      } else {
        cont = false;
      }
    } while(cont);
  }
}
