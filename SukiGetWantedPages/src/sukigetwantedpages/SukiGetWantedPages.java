/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sukigetwantedpages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method to extract the Uralic languages from MultiLI tested files
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 */
public class SukiGetWantedPages {

    /**
     * Method to extract the Uralic languages from MultiLI tested files
     * leaves out the lines of a page that do not contain
     * 1. at least one uppercase letter
     * 2. at least one punctuation mark
     *
     * reads from folder '1_tested'
     *      files that have name 'tested'+<number>
     * 
     * @param args the command line arguments
     * args[0] = number of the file to be read
     * 
     * writes of '2_wantedPages/+<language of page>+_wantedPages
     */
    public static void main(String[] args) throws IOException {
        // language codes of the Uralic languages
        List<String> WANTED = Arrays.asList("enf", "enh", "fit", "fkv", "izh", "kca", "koi", "kom", "kpv", "krl", "liv", "lud", "mdf", "mhr", "mns", "mrj", "mtm", "myv", "nio", "olo", "sel", "sia", "sjd", "sje", "sjk", "sjt", "sju", "sma", "sme", "smj", "smn", "sms", "udm", "vep", "vot", "vro", "xas", "yrk");
        BufferedReader reader = null;
        WriteToFile writer = null;
        String line = "";
        String lang;
        try {
            reader = new BufferedReader(new FileReader("1_tested/tested"+args[0]));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("EndLanguage: ")) {
                    lang = line.split(" ")[1];
                    if (WANTED.contains(lang)) {
                        try {
                            writer = new WriteToFile("2_wantedPages/"+lang+"_wantedPages", false);
                            writer.write(line);
                            while (!(line = reader.readLine()).startsWith("WARC-Date: ")) {
                                // only write a line to file if it contains at least 
                                // one uppercase letter and one punctuation
                                if (line.matches(".*\\p{Lu}.*") && line.matches(".*[\\.\\?!…:;].*")) {
                                    writer.write(line);
                                }
                                
                            }
                            writer.write(line+"\n");
                        }
                        catch (Exception e) {
                            System.out.println("Problem when trying to write: "+e);
                        }
                        finally {
                            writer.end();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Problem when trying to read: "+e);
        }
        finally {
            reader.close();
        }
        
    }
    
}
