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
package sukimakeunique;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Heidi Jauhiainen
 * Method to remove duplicate lines/sentences
 * 
 * This method is part of SukiSentencePipeline
 * https://github.com/uhdigihum/SUKISentencePipeline
 */
public class SukiMakeUnique {

    /**
     * Method to remove duplicate lines/sentences
     * Assumes a file with the following kinds of lines:
     * text [languages]url
     * 
     * if args[1] is not '0', reads a file with urls that are no more available 
     *      in the internet
     * 
     * @param args the command line arguments
     * args[0] = file to read
     * args[1] = name of file with urls not available or 0 (then ignored)
     */
    
    private static ArrayList<String> urls;
    
    public static void main(String[] args) throws IOException {
        urls = new ArrayList<>();
        if (!args[1].equals("0")) {
            read400(args[1]);
        }
        BufferedReader reader = null;
        String line="", sentence, oldUrl, info, oldinfoline, oldinfo, langs, oldlangs;
        String origSentence;
        String[] lineArray;
        TreeMap<String, String> abras = new TreeMap<>();
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    origSentence = line.substring(0, line.lastIndexOf(" "));
                    //remove everything but letters
                    sentence = origSentence.replaceAll("([^\\p{L}′'’´ʹ])", "");
                    //sentence = origSentence.replaceAll("(?<!\\p{L})", "");
                    //System.out.println(sentence);
                    //only keep the first instance of duplicates
                    if (!sentence.isEmpty()) {
                        if (abras.containsKey(sentence)) {
                            info = line.substring(line.lastIndexOf(" ")+1);
                            oldinfoline= abras.get(sentence);
                            oldinfo = oldinfoline.substring(oldinfoline.lastIndexOf(" ")+1);
                            if (oldinfo.contains("[")) {
                                oldUrl = oldinfo.substring(oldinfo.lastIndexOf("]")+1);
                                oldlangs = oldinfo.substring(0, oldinfo.lastIndexOf("]")).replaceAll("\\[", "");
                                //make union of the langs in new and old line
                                langs = info.substring(0, info.lastIndexOf("]")).replaceAll("\\[", "");
                                List<String> mylist = new ArrayList<String>(Arrays.asList(oldlangs.split(",")));
                                for (String lang : langs.split(",")) {
                                    if (!mylist.contains(lang)) {
                                        mylist.add(lang);
                                    }
                                }
                                String list = mylist.toString().replaceAll(" ", "");
                                // add lang union to new line if that url is in unavailable ones
                                if (!urls.isEmpty() && urls.contains(oldUrl)) {
                                    line = origSentence+" "+list+info.substring(info.lastIndexOf("]")+1);

                                }
                                //else ad lang union to new line
                                else {
                                    line = oldinfoline.substring(0, oldinfoline.lastIndexOf(" "))+" "+list+oldUrl;
                                }
                            }
                            // in case there is no langs specified
                            // use old line with all info if that url not in unavailable ones
                            else {
                                if (!urls.isEmpty() && !urls.contains(oldinfo)) {
                                    line = oldinfoline;
                                }
                            }
                        }
                        abras.put(sentence, line);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e+"\t"+line);
        }
        finally {
            reader.close();
        }
        //print the unique lines
        for (Map.Entry<String, String> entry : abras.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
    
    
    //read urls that were no more available on the inernet
    private static void read400(String filename) throws IOException {
        BufferedReader reader = null;
        String line;
        
        try{
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                urls.add(line);
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
