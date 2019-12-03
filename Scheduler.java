import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.*;

public class Scheduler extends TimerTask
{
  Semaphore sem1;
  Semaphore sem2;
  Semaphore sem3;
  Semaphore sem4;
  int count = 0;
  Worker t1;
  Worker t2;
  Worker t3;
  Worker t4;

  public Scheduler()
  {
    sem1 = new Semaphore(1);
    sem2 = new Semaphore(1);
    sem3 = new Semaphore(1);
    sem4 = new Semaphore(1);
    // Initialize threads based on their rates
    t1 = new Worker(sem1, 100, 1);
    t2 = new Worker(sem2, 200, 2);
    t3 = new Worker(sem3, 400, 3);
    t4 = new Worker(sem4, 1600, 4);
    // Set priorities of threads
    t1.setPriority(4);
    t2.setPriority(3);
    t3.setPriority(2);
    t4.setPriority(1);
    t1.start();
    t2.start();
    t3.start();
    t4.start();
  }

  public void run()
  {
    if (count == 160) // Run through 160 / 16 = 10 major periods
    {
      synchronized(Scheduler.class)
      {
        Scheduler.class.notify(); // Sync with main thread
      }
      try
      {
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        // Print stats
        t1.stats();
        t2.stats();
        t3.stats();
        t4.stats();
      }
      catch(InterruptedException e)
      {
        e.printStackTrace(); // Standard error checking
      }
    }
    else // Schedule threads
    {
      try
      {
        if(!t1.working) // If the thread isnt doing work schedule it to run
        {
          sem1.release();
          t1.run(); // Unlock and lock semaphores for the thread to run
          sem1.acquire();
        }
        else
        {
          t1.missed++; // Thread busy; skip scheduling and keep track of misses
        }
        if (count % 2 == 0) // Only schedule thread based on its frequency
        {
          if(!t2.working)
          {
            sem2.release();
            t2.run();
            sem2.acquire();
          }
          else
          {
            t2.missed++;
          }
        }
        if (count % 4 == 0)
        {
          if(!t3.working)
          {
            sem3.release();
            t3.run();
            sem3.acquire();
        }
          else
          {
            t3.missed++;
          }
        }
        if (count % 16 == 0)
        {
          if(!t4.working)
          {
            sem4.release();
            t4.run();
            sem4.acquire();
          }
          else
          {
            t4.missed++;
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace(); // Standard error checking
      }
    }
    count++; // Increment count to indicate a successful period of time
  }
}
