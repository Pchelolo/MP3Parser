
package tagparser.cocurrent.application;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cmc.music.common.ID3ReadException;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import tagparser.cocurrent.Parser;
import tagparser.cocurrent.exceptions.ParsingException;

public class ParserMP3 implements Parser {

    @Override
    public String parse(File file) throws ParsingException {
        try {
            MusicMetadataSet metaData = new MyID3().read(file);
            if (metaData == null) {
                return file.getAbsolutePath() + " No metadata";
            }
            return file.getAbsolutePath() + " " + metaData.getSimplified().getArtist() + " " + metaData.getSimplified().getSongTitle();
        } catch (IOException | ID3ReadException ex) {
            throw new ParsingException("Could not parse an MP3 file "+file.getAbsolutePath());
        }
    }
    
}
