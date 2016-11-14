package dataStructures;
import dataStructures.APT;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
* An indexed maximum priority queue to store APT objects by the priority of their
* square footage. The higher the square footage, the higher the priority.
*
* Written for cs1501 fall 2016
*
* @author Craig Mazzotta
*/

public class indexMaxPQ {
  private int N; //number of items in PQ
  private int[] pq; //binary heap
  private int[] qp; //inverse of pq: qp[pq[i]] = pq[qp[i]] = i
  private APT[] apts; //apts[i] = priority of i

    /**
   * Initializes an empty indexed priority queue with indices between 0
   * and maxN.
   * @param  maxN the max number of items in the PQ
   * @throws IllegalArgumentException if maxN < 0
   */
  public indexMaxPQ(int maxN) {
      if (maxN < 0) throw new IllegalArgumentException();
      N = 0;
      apts = new APT[maxN + 1];
      pq   = new int[maxN + 1];
      qp   = new int[maxN + 1];
      for (int i = 0; i <= maxN; i++)
          qp[i] = -1;
  }

  /*
  * @return True if size is 0, false otherwise.
  */
  public boolean isEmpty(){
    return N==0;
  }
  /*
  * @i The index to lookup
  * @return True if the index is in the PQ
  */
  public boolean contains(int i){
    return qp[i] != -1;
  }
  /*
  * @return The number of objects in PQ
  */
  public int size(){
    return N;
  }
  /*
  * @param i The index to insert the apt to in the PQ
  * @param apt The apartment object being inserted to the PQ
  */
  public void insert(int i, APT apt){
    if (contains(i)) throw new IllegalArgumentException("Index is already in the priority queue");
    N++;
    qp[i] = N;
    pq[N] = i;
    apts[i] = apt;
    swim(N);
  }


  /*
  * Returns the APT with highest square footage
  * @return the apartment object with most square footage
  * @throws NoSuchElementException if the PQ is empty
  */
  public APT maxApt(){
    if(N==0) throw new NoSuchElementException("Priority queue underflow");
    return apts[pq[1]];
  }

  /*
  * Returns an index associated with the highest square footage APT
  * @return an index associated with highest square footage APT
  * @throws NoSuchElementException if this PQ is empty
  */
  public int maxIndex(){
    if (N==0) throw new NoSuchElementException("PQ underflow");
    return pq[1];
  }

  /*
  * Returns the APT object associated with index i
  * @param i the index of the APT object to return
  * @return the APT object associated with index i
  * @throws NoSuchElementException no APT is associated with index i
  */
  public APT aptOf(int i){
    if(!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    else return apts[i];
  }

  /*
  * Change APT at index i to apt
  * @param i Index of the key to change
  * @param key The new value
  * @throws NoSuchElementException unless (0 <= i < maxN)
  */
  public void update(int i, APT apt){
    if(!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    apts[i] = apt;
    swim(qp[i]);
    sink(qp[i]);
  }

  /*
  * Remove APT at index i from PQ
  * @param i the index of the APT object to Remove
  * @throws IndexOutOfBoundsException unless (0 <= i < maxN)
  * @throws NoSuchElementException no APT object is associated with index i
  */
  public void delete(int i){
    if(!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    int index = qp[i];
    exch(index, N--);
    swim(index);
    sink(index);
    apts[i] = null;
    qp[i] = -1;
  }

  /*
  * @param i Index of item in question
  * @param j Index of item being compared to
  * @return True if APT square footage at index i is less than APT square footage at index j. False otherwise
  */
  private boolean less(int i, int j){
    return apts[pq[i]].getSf() < apts[pq[j]].getSf();
  }

  /*
  * Trade the APT objects' indexes to exchange their position in the PQ
  * @param i Index of first APT to be exchanged
  * @param j Index of second APT to be exchanged
  */
  private void exch(int i, int j){
    int swap = pq[i];
    pq[i] = pq[j];
    pq[j] = swap;
    qp[pq[i]] = i;
    qp[pq[j]] = j;
  }

  /*
  * Given index k, move item up PQ by priority
  * @param k Index of item to move
  */
  private void swim(int k){
    while (k > 1 && less(k/2,k)){
      exch(k,k/2);
      k=k/2;
    }
  }
  /*
  * Move APT at index k down to corresponding priority level
  * @param k Index of item to move
  */
  private void sink(int k){
    while (2*k <= N){
      int j = 2*k;
      if(j<N && less(j,j+1)) j++;
      if(!less(k,j)) break;
      exch(k,j);
      k=j;
    }
  }
}
