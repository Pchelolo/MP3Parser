package tagparser.cocurrent;

import java.io.File;
import tagparser.cocurrent.exceptions.ParsingException;

public interface Parser {    
    String parse(File file) throws ParsingException;
}
