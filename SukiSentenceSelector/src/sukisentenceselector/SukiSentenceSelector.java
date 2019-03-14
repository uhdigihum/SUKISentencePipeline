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

package sukisentenceselector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Heidi Jauhiainen
 * Method that selects proper sentences 
 * (i.e. the ones that start with uppercase letter and end in punctuation)
 */
public class SukiSentenceSelector {

    /**
     * @param args the command sentence arguments
     * 
     * Assumes file with lines that have:
     * <possible sentence> [langs]url
     * 
     * args[0] = file with lines to consider
     */
    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        String sentence, line, url;
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            while ((line = reader.readLine()) != null) {
                url = line.substring(line.lastIndexOf(" ")+1);
                sentence = line.substring(0, line.lastIndexOf(" "));

                //if lines does not start with ^©,|{€ and contains at least 2 letters
                if (sentence.matches("[^©,|{€].*\\p{L}.*\\p{L}.*")) {
                    // remove .↑/)→←^;_]& and space from the beginning of the sentence
                    sentence = sentence.replaceAll("^[.↑/\\)→←\\^;\\_\\]&\\s]+", "");
                    //remove ^→_|´< and space fro the end of the sentence
                    sentence = sentence.replaceAll("[\\^→_|´< ]+$", "");
                    //if sentence end in .?!…:; with possibly )"”»]>“«
                    if (sentence.matches(".*[\\.\\?!…:;][\\)\"”»\\]>“« ]*")) {
                        //if sentence starts with uppercase letter, number or ¿
                        //with possibly «"”“[–(-§:*'+<»#>·‘?=!`™’%~ in front
                        if (sentence.matches("[«\"”“\\[–\\(\\-§:\\*'\\+<»#>·‘\\?=!`™’%~ ]*[\\p{Lu}\\p{N}¿].*")) {
                            //if sentence starts with («"”“[ remove lonely parenthesis etc.
                            if (sentence.matches("[\\(«\"”“\\[].*")) {
                                if (sentence.matches("«[^»«]*")) {
                                    sentence = sentence.replaceFirst("«", "");
                                }
                                if (sentence.matches("[\"”“][^\"”“]*")) {
                                    sentence = sentence.replaceFirst("[\"”“]", "");
                                }
                                if (sentence.matches("\\[[^\\]]*")) {
                                    sentence = sentence.replaceFirst("\\[", "");
                                }
                                if (sentence.matches("\\([^\\)]*")) {
                                    sentence = sentence.replaceFirst("\\(", "");
                                }
                            }
                            //if sentence ends with "”»]>“«] remove lonely parenthesis etc.
                            if (sentence.matches(".*[\"”»\\]>“«]")) {
                                if (sentence.matches("[^\"”“„]*[\"”“]")) {
                                    sentence = sentence.replaceFirst("[\"”“]", "");
                                }
                                if (sentence.matches("[^»«]*[»«]")) {
                                    sentence = sentence.replaceFirst("[»«]", "");
                                }
                                if (sentence.matches("[^><]*>")) {
                                    sentence = sentence.replaceFirst(">", "");
                                }
                                if (sentence.matches("[^\\[*]\\]")) {
                                    sentence = sentence.replaceFirst("\\]", "");
                                }
                            }
                            //only valid sentences are printed
                            System.out.println(sentence+" "+url);
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
        }
    }
    
}
