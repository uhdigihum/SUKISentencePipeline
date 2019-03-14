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
package sukisentencemaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method to divide lines to sentences
 * Uses the method by Mikheev to find abbreviations from any language
 * Andrei Mikheev. 2000. Tagging sentence boundaries.
 * Andrei Mikheev. 2002. Periods, capitalized words, etc.
 */
public class SukiSentenceMaker {

    /**
     * @param args the command sentence arguments
     * args[0] = file with line to be divided into sentences
     * 
     * writes to stdout
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String file = args[0];
        BufferedReader reader = null;
        String line, sentence, word, word2 = "", url;
        String[] lineArray;
        try{
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                url = line.substring(line.lastIndexOf(" ")+1);
                sentence = line.substring(0, line.lastIndexOf(" "));
                //change space that is not really a space
                sentence = sentence.replaceAll(" ", " ");
                //remove tabs
                sentence = sentence.replaceAll("\t", " ");
                //remove extra spaces
                sentence = sentence.replaceAll("\\s{2,}", " ").trim();
                //remove extra space in the beginning of sentence
                //line = sentence.replaceFirst("^ ", "");
                //replace "word.word" with "word. word"
                sentence = sentence.replaceAll("(\\p{Ll}[\\.\\?!:;…]+)(\\p{Lu}\\p{Ll})", "$1 $2");
                //divide sentence to words
                lineArray = sentence.split(" ");
                for (int i=0; i<lineArray.length; i++) {
                    word = lineArray[i].trim();
                    if (!word.equals("")) {
                        // if word end in . ? ! ) ] parenthesis or ellipsis
                        if (word.matches(".*[\\.\\?!\"”…\\)\\]:]")) {
                            //get the next word
                            if (i<lineArray.length-1) {
                                word2 = lineArray[i+1].trim();
                            }
                            String pattern = "[\\(\\)\\[\\]]";
                            //IF word ends with ?!... and possibly )"”»
                            //      or with . and )"”»
                            //      or with . without a letter or number before it
                            //              (e.g. word". or word . as Mikheev does not consider these as abbrs)
                            // or word without parenthesis ends with . and Mikheev method does not consider the word an abbrs
                            //  or word ends in :
                            //AND next word does not start with a lowercase letter (not considering "”“«)
                            //THEN ADD A NEWLINE
                            if (word.matches(".*(([\\?!…][\\)\"”»]?)|(\\.[\\)\"”»])|([^\\p{L}\\p{N} ]\\.))") 
                                    || ((word.replaceAll(pattern, "").matches(".*\\.") 
                                        && !doMikheev(word.replaceAll(pattern, ""), i, lineArray)) 
                                    || word.matches(".*:")) 
                                        && word2.matches("[\"”“«]?[^\\p{Ll}].*")) {
                                System.out.println(word+" "+url);
                            }
                            else {
                                System.out.print(word+" ");
                                if (i==lineArray.length-1) {
                                    System.out.println(url);
                                }
                            }
                        }
                        else {
                            System.out.print(word+" ");
                            if (i==lineArray.length-1) {
                                System.out.println(url);
                            }
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
    
    //method that implements Mikheev's abbreviation list forming
    //but simply returns true if an abbreviation is detected
    private static boolean doMikheev(String word, int i, String[] lineArray) {
        //if token is longer than 1 character, contains at least one letter and ends with . or .,
        if (word.length() > 1  && word.matches(".*\\p{L}.*\\.,?")) {
            //System.out.println(word);
            //if token is a single character with period
            if (word.length() == 2 && word.matches(".*\\.")) {
                //if the first character is letter
                if (Character.isLetter(word.charAt(0))) {
                    return true;
                }
            }
            //if token is x.x. or x.x.x. etc. with any letters
            else if (word.matches("^\\p{L}\\.(\\p{L}\\.)+$")) {
                return true;
            }
            //if token contains no vowels
            else if (!word.toLowerCase().matches(".*[aieouаáеыоиäуõяэӹзöӓâyüӧåӱïюёæУÿіøӑўèӰєāéīәūãαēőӯōũíįιóɛúÆїοǟıυàȯӣăë¡ȭωάĕЎœîùòŒìύŏӘӛęôêӥӚėǎûě].*\\.")) {
                //but is not all capital letters (contains at least one letter)
                if (!word.matches("^\\p{Lu}+\\.$") && word.matches(".*\\p{L}.*\\.")) { 
                    return true;
                }
            }
            //if token has at most 4 letters with . or .,
            else if (word.matches("\\p{L}{1,4}\\.,?")) {
                //if token ends with .,
                if (word.matches(".*.,")) {
                    return true;
                }
                //lets consider the following token
                String word2 = "";
                if (i<lineArray.length-1) {
                    word2 = lineArray[i+1];
                }
                //if next token starts with a lowercase letter
                else if ( i<lineArray.length-1 && word2.matches("\\p{Ll}.*")) {
                    return true;
                }
                //if next token is a number
                else if (i<lineArray.length-1 && word2.matches("[0-9].*")) {
                    return true;
                }
            }
            
        }
        return false;                            
    }
        
    
}
