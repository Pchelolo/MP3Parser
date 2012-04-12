/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagparser.cocurrent.application;

import java.util.concurrent.BlockingQueue;
import tagparser.cocurrent.Consumer;

/**
 *
 * @author pchel
 */
public class ConsumerFactory {
    private String type;
    private BlockingQueue<String> resultsQueue;
    
    public ConsumerFactory(String type, BlockingQueue<String> resultsQueue) {
        this.type = type;
        this.resultsQueue = resultsQueue;
    }
    
    public Consumer getConsumer() {
        switch(type) {
            case "print" :
                 return new PrintingConsumer(resultsQueue);
            default:
                return null;
        }
    }
}
