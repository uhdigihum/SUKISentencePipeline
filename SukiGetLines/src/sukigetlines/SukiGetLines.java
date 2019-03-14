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
package sukigetlines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method to get lines from files that have been MultiLI tested
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 */
public class SukiGetLines {

    /**
     * Method to get lines from files that have been MultiLI tested
     * the file is assumed to be of the form:
     * 'MultiLi: '+list of languages provided by MultiLI
     * 'WARC-Target-URI: '+url of the page
     * <text of the page>
     * 'WARC-Date:'
     * 
     * @param args the command line arguments
     * args[0] = file to be read
     * 
     * writes all the lines 
     *      line [languages]url
     * to 3_allRows_withUrl
     */
    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        String line, url, langs = "";
        String[] lineArray;
        WriteToFile writer = new WriteToFile("3_allLines_withUrl", false);
        
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MultiLi: ")) {
                    langs = "[";
                    lineArray = line.split(":");
                    for (int i=2; i<lineArray.length; i++) {
                        langs += lineArray[i].split(" ")[0]+",";
                    }
                    langs = langs.replaceAll(",$", "]");
                }
                if (line.startsWith("WARC-Target-URI:")) {
                    url = langs+""+line.split(" ")[1];
                    while ((line = reader.readLine()) != null && !line.startsWith("WARC-Date")) {
                        line = line.replaceAll(" ", " ");
                        //remove extra spaces
                        line = line.replaceAll("\\s{2,}", " ");
                        //remove extra space in the beginning of line
                        line = line.trim();
                        //replace "word.Anotherword" with "word. Anotherword"
                        line = line.replaceAll("(\\p{Ll}[\\.\\?!:;…]+)(\\p{Lu}\\p{Ll})", "$1 $2");
                        if (!line.isEmpty()) {
                            writer.write(line+" "+url);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            reader.close();
            writer.end();
        }
    }
    
}
