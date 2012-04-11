package tagparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.cmc.music.common.ID3ReadException;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

public class TagParserConcurrent {
    private interface RunnableWithStop extends Runnable{
        void setFinished();
    }

    private static final int TASK_QUEUE_CAPACITY = 10;
    private static final int RESULT_QUEUE_CAPACITY = 100;
    private BlockingQueue<File> tasksQueue = new LinkedBlockingQueue<File>(TASK_QUEUE_CAPACITY);
    private BlockingQueue<String> resultQueue = new LinkedBlockingQueue<String>(RESULT_QUEUE_CAPACITY);
    
    private RunnableWithStop resultPrinter = new RunnableWithStop() {
        private volatile boolean isFinished = false;
        
        @Override
        public void run() {
            try {
                while (!isFinished || !resultQueue.isEmpty()) {
                    
                    String res = resultQueue.poll(10, TimeUnit.MILLISECONDS);
                    if(res!=null) {
                         System.out.println(res);
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted while printing");
            }
        }

        @Override
        public void setFinished() {
            isFinished = true;
        }
    };
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

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

    public void parse() {
        (new Thread(resultPrinter, "resultPrinter")).start();

        try {
            while (true) {
                final File dir = tasksQueue.poll(5, TimeUnit.SECONDS);
                
                if(dir == null)  {
                    break;
                }
                
                executor.execute(new Runnable() {

                    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 file", "mp3", "MP3");

                    @Override
                    public void run() {
                        for (File file : dir.listFiles()) {
                            if (file.isDirectory()) {
                                try {
                                    tasksQueue.put(file);
                                } catch (InterruptedException ex) {
                                    System.err.println("Interrupted while adding new task");
                                }
                            } else {
                                if (filter.accept(file)) {
                                    try {
                                        resultQueue.put(getTagsForFile(file));
                                    } catch (IOException ex) {
                                        System.err.println("File read error " + file);
                                    } catch (ID3ReadException ex) {
                                        System.err.println("File read error " + file);
                                    } catch (InterruptedException ex) {
                                        System.err.println("Interrupted while adding new task");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } catch (InterruptedException ex) {
            //IGNORE
        } finally {
            resultPrinter.setFinished();
            executor.shutdown();
        }
    }

    private String getTagsForFile(File file) throws IOException, ID3ReadException {
        MusicMetadataSet metaData = new MyID3().read(file);
        if (metaData == null) {
            return file.getAbsolutePath() + " No metadata";
        }
        return file.getAbsolutePath() + " " + metaData.getSimplified().getArtist() + " " + metaData.getSimplified().getSongTitle();
    }
}
