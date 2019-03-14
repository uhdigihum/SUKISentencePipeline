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

/**
 *
 * @author Heidi Jauhiainen
 */
public class GetEndLanguage {
    private static String winningLanguage, winningUralic;
    private static int highestValue, highestUralic;
    
    public static String getLanguage(String language, String prevLang) {
        winningLanguage = "";
        highestValue = 0;
        winningUralic = "";
        highestUralic = 0;
        String endLang = "";
        if (language.equals("null")) {
            endLang = prevLang;
        }
        else if (language.equals("") || language.equals(":xxx")) {
            endLang = "xxx";
        }
        else {
            language = language.substring(1);
            String[] array = language.split("\\:");
            for (String lang : array) {
                getWinning(lang);
            }
            if (array.length > 9 || winningUralic.equals("")) {
                if (CheckLanguage.other(winningLanguage)) {
                    endLang = winningLanguage;
                }
                else {
                    endLang = "other";
                }
            }
            else {
                endLang = winningUralic;
            }
        }
        return endLang;
    }
    
    public static String getFinalMultili(String language, String prevLang) {
        winningLanguage = "";
        highestValue = 0;
        String endLang;
        if (language.equals("null")) {
            endLang = prevLang;
        }
        else if (language.equals("") || language.equals(":xxx")) {
            endLang = "xxx";
        }
        else {
            language = language.substring(1);
            String[] array = language.split("\\:");
            
            for (String lang : array) {
                getWinning(lang);
            }
            if (array.length > 9) {
                endLang = "xxx";
            }
            else {
                endLang = winningLanguage;
            }
            
        }
        return endLang;
    }
    
    private static void getWinning(String lang) {
        //String lang = array[i];
        String[] langArray = lang.split(" ");
        String currentLang = langArray[0];
        String valuePart = langArray[1];
        String valueString = valuePart.split("\\/")[1];
        int value = Integer.parseInt(valueString.split("\\.")[0]);
        if (value > highestValue) {
            highestValue = value;
            winningLanguage = currentLang;
        }
        if (CheckLanguage.uralic(currentLang)) {
            if (value > 2 && value > highestUralic) {
                highestUralic = value;
                winningUralic = currentLang;
            }
        }
    }
}
