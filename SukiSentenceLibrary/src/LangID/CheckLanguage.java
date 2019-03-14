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
package LangID;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Heidi Jauhiainen
 */
public class CheckLanguage {
    private static List<String> WANTED = Arrays.asList("enf", "enh", "fit", "fkv", "izh", "kca", "koi", "kom", "kpv", "krl", "liv", "lud", "mdf", "mhr", "mns", "mrj", "mtm", "myv", "nio", "olo", "sel", "sia", "sjd", "sje", "sjk", "sjt", "sju", "sma", "sme", "smj", "smn", "sms", "udm", "vep", "vot", "vro", "xas", "yrk");
    
    private static List<String> OTHERS = Arrays.asList("fin", "ekk", "eng", "hun", "nob", "nno", "swe", "lav", "rus");
    
    public static boolean uralic(String language) {
        if (WANTED.contains(language.substring(0, 3))) {
            return true;
        }
        return false;
    }
    
    public static boolean other(String language) {
        if (OTHERS.contains(language.substring(0, 3))) {
            return true;
        }
        return false;
    }
    
    public static List<String> getWanted() {
        return WANTED;
    }
}
