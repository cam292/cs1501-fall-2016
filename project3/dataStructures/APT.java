package dataStructures;

public class APT {
  private String address;
  private int num;
  private String city;
  private int zip;
  private int price;
  private int sf;

  /*
  * Constructor: initialize an empty APT object with no parameters
  */
  public APT(){
    address = null;
    num = 0;
    city = null;
    zip = 0;
    price = 0;
    sf = 0;
  }

  /*
  * Constructor: initialize an APT object with values
  *
  * @param address The address of the given apartment
  * @param num The apartment number of given apartment
  * @param city The city a given apartment is located in
  * @param zip The ZIP code for the given apartment
  * @param price The price of rent for the given apartment
  * @param sf The square footage of the given apartment
  */
  public APT(String address, int num, String city, int zip, int price, int sf){
    this.address = address;
    this.num = num;
    this.city = city;
    this.zip = zip;
    this.price = price;
    this.sf = sf;
  }

  public String getAddress(){
    return this.address;
  }
  public int getNum(){
    return this.num;
  }
  public String getCity(){
    return this.city;
  }
  public int getZip(){
    return this.zip;
  }
  public int getPrice(){
    return this.price;
  }
  public int getSf(){
    return this.sf;
  }

  public void setPrice(int price){
    this.price = price;
  }
}
