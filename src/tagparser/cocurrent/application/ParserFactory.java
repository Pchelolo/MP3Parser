
package tagparser.cocurrent.application;

import tagparser.cocurrent.Parser;
import tagparser.cocurrent.application.ParserMP3;

public class ParserFactory {
    private String parserType;
    
    public ParserFactory(String parserType) {
        this.parserType = parserType;
    }
    
    public Parser getParser() {
        switch(parserType) {
            case "MP3":
            case "mp3": 
                return new ParserMP3();
            default: return null;   
        }
    }
}
