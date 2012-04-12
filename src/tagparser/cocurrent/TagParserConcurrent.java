package tagparser.cocurrent;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import tagparser.cocurrent.application.ConsumerFactory;
import tagparser.cocurrent.application.ParserFactory;

public class TagParserConcurrent {
    private static final int TASK_QUEUE_CAPACITY = 10;
    private static final int RESULT_QUEUE_CAPACITY = 100;
    private BlockingQueue<File> tasksQueue = new LinkedBlockingQueue<>(TASK_QUEUE_CAPACITY);
    private BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>(RESULT_QUEUE_CAPACITY);
    private ParserFactory parserFactory = new ParserFactory("mp3");
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Consumer> consumers = new LinkedList<>();

    public TagParserConcurrent(String filename) {
        File rootDir = new File(filename);
        if (!rootDir.isDirectory()) {
            System.err.println("Root is not a directory");
            System.exit(1);
        }
        try {
            tasksQueue.put(rootDir);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted while adding rootDir");
        }

    }

    public void start() {
        List<Future> submittedTasks = new LinkedList<>();
        ExecutorCompletionService exComplection = new ExecutorCompletionService(executor);
        startConsumers();

        try {
            while (true) {
                final File dir = tasksQueue.poll(10, TimeUnit.MILLISECONDS);

                if (dir != null) {
                    submittedTasks.add(exComplection.submit(new DirectoryParser(dir, tasksQueue, resultQueue, parserFactory), new Object()));
                }

                Iterator<Future> it = submittedTasks.iterator();
                while (it.hasNext()) {
                    if(it.next().isDone()) {
                        it.remove();
                    }
                }

                if (submittedTasks.isEmpty()) {
                    executor.shutdown();
                    stopConsumers();
                    break;
                }
            }
        } catch (InterruptedException ex) {
            //IGNORE
        }
    }

    private void startConsumers() {
        ConsumerFactory consumerFactory = new ConsumerFactory("print", resultQueue);
        Consumer resultPrinter = consumerFactory.getConsumer();
        consumers.add(resultPrinter);
        (new Thread(resultPrinter, "resultPrinter")).start();
    }

    private void stopConsumers() {
        for (Consumer consumer : consumers) {
            consumer.setStopping();
        }
    }
}
