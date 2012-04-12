/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagparser.cocurrent.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import tagparser.cocurrent.Consumer;

/**
 *
 * @author pchel
 */
class PrintingConsumer implements Consumer {
    private BlockingQueue<String> resultsQueue;
    private volatile boolean isStopping = false;

    public PrintingConsumer(BlockingQueue<String> resultsQueue) {
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void setStopping() {
        isStopping = true;
    }

    @Override
    public void run() {
          try {
                while (!isStopping || !resultsQueue.isEmpty() ) {
                    if(Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    
                    String res = resultsQueue.poll(10, TimeUnit.MILLISECONDS);
                    if(res!=null) {
                         System.out.println(res);
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted while printing");
            }
    }
    
}
