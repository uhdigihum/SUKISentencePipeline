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
package sukimultilisentences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import LangID.CheckLanguage;
import LangID.GetEndLanguage;
import LangID.IdentifyLanguage;
import Write.WriteToFile;

/**
 *
 * @author Heidi Jauhiainen
 * Method that sends sentences to MultiLI for language testing
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 */
public class SukiMultiLiSentences {

    /**
     * @param args the command line arguments
     * Assumes file with lines that have:
     * <possible sentence> [langs]url
     * 
     * args[0] = file with sentences to identify
     * args[1] = server where the identifier is
     * args[2] = port where the identifier is listening
     * args[3] = folder where to write the rows identified as Uralic
     * if args[4]='no' the following arguments are not needed
     * args[4] = notRightLanguage
     * args[5] = notUralic
     * args[6] = verifiedLinks
     */
    private static TreeMap<String, ArrayList<String>> notAllowedLangs;
    private static TreeMap<String, ArrayList<String>> mustBeLangs;
    
    public static void main(String[] args) throws IOException {
        notAllowedLangs = new TreeMap<>();
        mustBeLangs = new TreeMap<>();
        BufferedReader reader = null;
        WriteToFile writer = null;
        String line = "", origSentence, sentence, urlLine, url, langs;
        String multiLang, endLang, prevLang = "";
        String server = args[1];
        int port = Integer.parseInt(args[2]);
        String folder = args[3];
        if (!args[4].equals("no")) {
            readNotAllowedLangs(args[4]);
            readNotAllowedUrls(args[5]);
            readMustBeLangs(args[6]);
        }
        
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            while ((line = reader.readLine()) != null) {
                origSentence = line.substring(0, line.lastIndexOf(" "));
                
                if (!origSentence.isEmpty()) {
                    urlLine = line.substring(line.lastIndexOf(" ")+1);
                    url = urlLine.substring(urlLine.indexOf("]")+1);
                    langs = urlLine.substring(0, urlLine.indexOf("]"));
                    langs = langs.replace("[", "");
                    sentence = langs+" "+origSentence;
                    //get multiLi language for the row
                    multiLang = IdentifyLanguage.testLanguage(sentence, server, port);
                    //check the actual language
                    endLang = GetEndLanguage.getFinalMultili(multiLang, prevLang);
                    //if the language is one of the Uralic ones, write row to the file of that language
                    if (CheckLanguage.uralic(endLang)) {
                        //if this url is in MustBeLangs (verified in Wanca) but this in not the language there
                        //do not write
                        if (!CheckMustBeUrls(url, endLang)) {
                            continue;
                        }
                        //if this url is in notAllowedLangs (voted 'no' or changed from) and this is the language there
                        //do not write
                        if (CheckNotAllowedUrls(url, endLang)) {
                            continue;
                        }
                        //if the language is not the same as for the previous line
                        //close the previous file
                        if (!endLang.equals(prevLang)) {
                            if (writer != null) {
                                writer.end();
                            }

                            //and open an other file
                            writer = new WriteToFile(folder+"/"+endLang+"_multiLi", false);

                        }
                        writer.write(origSentence+" "+url);
                    }
                    //this is now the previous language
                    prevLang = endLang;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e+"\t"+line);
        }
        finally {
            writer.end();
            reader.close();
        }
    }
    
    //read urls with languages that in wanca got votes for 'not this language'
    private static void readNotAllowedLangs(String filename) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        String line, url, lang;
        ArrayList<String> langs;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                url = line.split(" ")[0];
                if (notAllowedLangs.containsKey(url)) {
                    langs = notAllowedLangs.get(url);
                }
                else {
                    langs = new ArrayList<>();
                }
                lang = line.split(" ")[1];
                if (!langs.contains(lang)) {
                    langs.add(lang);
                }
                notAllowedLangs.put(url, langs);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            reader.close();
        }
    }
    
    //read urls that in wanca have been changed to non-Uralic
    //no Uralic languages are allowed
    private static void readNotAllowedUrls(String filename) throws IOException {
        BufferedReader reader = null;
        String line, url, lang;
        ArrayList<String> langs;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                langs = new ArrayList<>();
                langs.add("*");
                notAllowedLangs.put(line, langs);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            reader.close();
        }
    }
    
    
    //if the language received from multili is in the list of not allowed for this url
    //return true
    private static boolean CheckNotAllowedUrls(String url, String lang) {
        ArrayList<String> langs;
        if (notAllowedLangs.containsKey(url)) {
            langs = notAllowedLangs.get(url);
            if (langs.contains("*")) {
                //System.out.println(url+" *");
                return true;
            }
            if (langs.contains(lang)) {
                //System.out.println(url+" "+lang);
                return true;
            }
        }
        return false;
    }
    
    //read urls that in wanca have a verified language
    private static void readMustBeLangs(String filename) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        String line, url, lang;
        ArrayList<String> langs;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                url = line.split(" ")[0];
                if (mustBeLangs.containsKey(url)) {
                    langs = mustBeLangs.get(url);
                }
                else {
                    langs = new ArrayList<>();
                }
                lang = line.split(" ")[1];
                if (!langs.contains(lang)) {
                    langs.add(lang);
                }
                mustBeLangs.put(url, langs);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            reader.close();
        }
    }
    
    //if the language if the verified language, return true
    private static boolean CheckMustBeUrls(String url, String lang) {
        ArrayList<String> langs;
        if (mustBeLangs.containsKey(url)) {
            langs = mustBeLangs.get(url);

            if (langs.contains(lang)) {
                //System.out.println(url+" "+lang);
                return true;
            }
            return false;
        }
        return true;
    }
    
}
