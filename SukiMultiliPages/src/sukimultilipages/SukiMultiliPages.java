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
package sukimultilipages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import LangID.GetEndLanguage;
import LangID.IdentifyLanguage;
import Method.Methods;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method for using MultiLI on pages downloaded from the Internet
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 * 
 */
public class SukiMultiliPages {

    /**
     * Method for using MultiLI on pages downloaded from the internet.
     * Assumes a file the name of which ends with '_<number>'
     * where each page has
     * 1) 'WARC-Target-URL: '+<url> (of the page)
     * 2) text of the page without html or other code
     * 3) 'WARC-Date:' with or without the date the page was downloaded.
     * 
     * Assumes folder '1_tested'
     * Writes to file 1_tested/tested<number>
     * 
     * @param args the command line arguments
     * args[0] = server where MultiLI is running
     * args[1] = port where MultiLI is listening
     * args[2] = file to be read (ending in '_')
     */
    
    //language codes of Uralic languages
    private static final List<String> WANTED = Arrays.asList("enf", "enh", "fit", "fkv", "izh", "kca", "koi", "kom", "kpv", "krl", "liv", "lud", "mdf", "mhr", "mns", "mrj", "mtm", "myv", "nio", "olo", "sel", "sia", "sjd", "sje", "sjk", "sjt", "sju", "sma", "sme", "smj", "smn", "sms", "udm", "vep", "vot", "vro", "xas", "yrk");
    private static WriteToFile buffwriter;
    private static Methods methods = new Methods();
    private static String number;
    
    public static void main(String[] args) throws IOException {
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String file = args[2];
        number = file.split("_")[1];
        buffwriter = new WriteToFile("1_tested/tested"+number, false);
        BufferedReader reader = null;
        String line;
        String oldLang, multiLang, endLang, prevLang = ""; 
        String toFile = "", prevLine = "", sentence = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("WARC-Target-URI: ")) {
                    toFile += line+"\n";
                    line = reader.readLine();
                    //toFile is line by line - for writing later to file; includes WARC-Target-URI and WARC-Date lines
                    //sentence is text as one long line - for sending to MultiLI
                    while (!line.startsWith("WARC-Date:")) {
                        if (line.length() > 0) {
                            toFile += line+"\n";
                            if (!line.equals(prevLine)) {
                                sentence += line+" ";
                            }
                            prevLine = line;
                        }
                        line = reader.readLine();

                    }
                    toFile += line+"\n\n";
                    sentence = modify(sentence);
                    //get languages from MultiLI
                    multiLang = IdentifyLanguage.testLanguage("all "+sentence, server, port);
                    //get winning language, Uralic prefered
                    endLang = GetEndLanguage.getLanguage(multiLang, prevLang);
                    prevLang = endLang;
                    //write to file
                    write(endLang, multiLang, toFile);
                    toFile = "";
                    sentence = "";
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            reader.close();
        }
    }
    
    private static String modify(String modifiable) throws UnknownHostException, IOException {
        // remove extra spaces
        modifiable = methods.replaceAll(modifiable, " +", " ");
        return modifiable;
    }
    
    private static void write(String lang, String multiLang, String toFile) throws IOException {
        buffwriter.write("EndLanguage: "+lang);
        buffwriter.write("MultiLi: "+multiLang);
        buffwriter.write(toFile);
        buffwriter.end();
    }
    
}
