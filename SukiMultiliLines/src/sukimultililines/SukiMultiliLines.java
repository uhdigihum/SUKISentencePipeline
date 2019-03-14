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
package sukimultililines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import LangID.CheckLanguage;
import LangID.GetEndLanguage;
import LangID.IdentifyLanguage;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method to send lines to MultiLI to be tested
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 */
public class SukiMultiliLines {

    /**
     * Method to send lines to MultiLI to be tested
     * Assumes a line with the following form:
     *  text [langs]url
     * 
     * @param args the command line arguments
     * args[0] = number of the file to be tested
     *      assumes files with the filepath 5_UniquedLines/4_allRows_uniqued.<args[0]>
     * args[1] = server where MultiLI is installed
     * args[2] = port where MultiLI is listening
     * 
     * writes to 6_MultiliLines/lines_multilied.<args[0]>
     */
    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        WriteToFile writer = null;
        String line, origSentence, sentence, urlLine, url, langs;
        String multiLang, endLang, prevLang = "";
        String[] lineArray;
        String server = args[1];
        int port = Integer.parseInt(args[2]);
        
        try {
            writer = new WriteToFile("6_MultiliLines/lines_multilied."+args[0], false);
            reader = new BufferedReader(new FileReader("5_UniquedLines/4_allRows_uniqued."+args[0]));
            while ((line = reader.readLine()) != null) {
                origSentence = line.substring(0, line.lastIndexOf(" "));
                
                if (!origSentence.isEmpty()) {
                    urlLine = line.substring(line.lastIndexOf(" ")+1);
                    url = urlLine.substring(urlLine.indexOf("]")+1);
                    langs = urlLine.substring(0, urlLine.indexOf("]"));
                    langs = langs.replace("[", "");
                    //MultiLI assumes to get lang,lang,lang text text text...
                    sentence = langs+" "+origSentence;
                    //get multiLi language for the line
                    multiLang = IdentifyLanguage.testLanguage(sentence, server, port);
                    //check the winning language
                    endLang = GetEndLanguage.getLanguage(multiLang, prevLang);
                    //if the language is one of the Uralic ones, write 'line [langs]url' to file
                    if (CheckLanguage.uralic(endLang)) {
                        langs = "[";
                        lineArray = multiLang.split(":");
                        for (int i=1; i<lineArray.length; i++) {
                            langs += lineArray[i].split(" ")[0]+",";
                        }
                        langs = langs.replaceAll(",$", "]");
                        //System.out.println(sentence);
                        writer.write(origSentence+" "+langs+url);
                        //writer.write(sentence+"\t"+multiLang);
                    }
                    //this is now the previous language
                    prevLang = endLang;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            writer.end();
            reader.close();
        }
    }
    
}
