package tagparser.cocurrent;

import tagparser.cocurrent.application.ParserFactory;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import javax.swing.filechooser.FileNameExtensionFilter;
import tagparser.cocurrent.exceptions.ParsingException;

public class DirectoryParser implements Runnable {

    private final FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 file", "mp3", "MP3");
    private File dir;
    private BlockingQueue<File> taskQueue;
    private BlockingQueue<String> resultQueue;
    private ParserFactory parserFactory;

    public DirectoryParser(File dir, BlockingQueue<File> taskQueue, BlockingQueue<String> resultQueue, ParserFactory parserFactory) {
        this.dir = dir;
        this.taskQueue = taskQueue;
        this.resultQueue = resultQueue;
        this.parserFactory = parserFactory;
    }

    @Override
    public void run() {
        try {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    try {
                        taskQueue.put(file);
                    } catch (InterruptedException ex) {
                        System.err.println("Interrupted while adding new task");
                    }
                } else {
                    if (filter.accept(file)) {
                        try {
                            resultQueue.put(parserFactory.getParser().parse(file));
                        } catch (ParsingException ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            //IGNORE
        }
    }
}
