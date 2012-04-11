package tagparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.cmc.music.common.ID3ReadException;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

public class TagParser {

    private File rootDir;
    
    public TagParser(String filename) {
        rootDir = new File(filename);
        if(!rootDir.isDirectory()) {
            System.err.println("Root is not a directory");
            System.exit(1);
        }
    }
    
    public List<String> parse() {
        return parseFolder(rootDir);
    }
    
    private List<String> parseFolder(File dir) {
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 file", "mp3", "MP3");
                
        List<String> resultList = new LinkedList<String>();
        for(File file : dir.listFiles() ) {
            if(file.isDirectory()) {
                resultList.addAll(parseFolder(file));
            } else {
                if(filter.accept(file)) {
                    try {
                        resultList.add(getTagsForFile(file));
                    } catch (IOException ex) {
                        System.err.println("File read error "+file);
                    } catch (ID3ReadException ex) {
                        System.err.println("File read error "+file);
                    }
                }
            }
        }
        return resultList;
    }
    
    private String getTagsForFile(File file) throws IOException, ID3ReadException   {
        MusicMetadataSet metaData = new MyID3().read(file);
        if(metaData == null) {
            return file.getAbsolutePath()+" No metadata";
        }
        return file.getAbsolutePath()+" "+metaData.getSimplified().getArtist()+" "+metaData.getSimplified().getSongTitle();  
    }
}
