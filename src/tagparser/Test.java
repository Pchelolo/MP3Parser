/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagparser;

import tagparser.cocurrent.TagParserConcurrent;
import java.util.List;

/**
 *
 * @author pchel
 */
public class Test {
    private static void testNonConcurrent() {
        TagParser parser = new TagParser("/home/pchel/Music");
        List<String> results = parser.parse();
        for(String line : results) {
            System.out.println(line);
        }
    }
    
    private static void testConcurrent() {
        new TagParserConcurrent("/home/pchel/Music").start();
    }
    
    
    public static void main(String[] args) {
       //testNonConcurrent();
       testConcurrent();
    }
}
