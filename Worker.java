import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.*;

public class Worker extends Thread
{
  double [][] matrix;
  public boolean working = false;
  int run = -1;
  public int missed;
  int doWork;
  int threadnum;
  Semaphore sem;

  public Worker(Semaphore semaphore, int doWork, int threadnum)
  {
    sem = semaphore;
    this.doWork = doWork;
    this.threadnum = threadnum;
    // Populate matrix
    matrix = new double [10][10];
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        matrix[i][j] = 1;
      }
    }
  }

  public void doWork()
  {
    int product = 1;
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        int row = (5 * (i % 2)) + ((i/2) % 5); // Iterate down the columns in the 0 5 1 6 2 7 3 8 4 9 order
        product *= matrix[j][row]; // Multiply the indices
      }
    }
  }

  public void stats()
  { // Track how many times it ran and failed
    System.out.println("Thread " + threadnum + " ran: " + run + " times");
    System.out.println("It overran " + missed + " times");
  }

  public void run()
  {
    try
    { // Lock
      sem.acquire();
      working = true; // Let scheduler know thread cannot be run
      for (int i = 0; i < doWork; i++)
      {
        doWork(); // Execute task
      }
      run++; // Track successful run
      working = false; // Let scheduler know thread can be run again
      sem.release(); // Unlock
    }
    catch (Exception e)
    {
      e.printStackTrace(); // Standard error checking
    }
  }
}
