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
package Method;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Heidi Jauhiainen
 */
public class Methods {
    /**
     * Replace part of string using StringBuffer
     * @param modifiable (string where replacement is done)
     * @param pattern (which part is replaced)
     * @param with (with what is replaced)
     * @return modified string
     */
    public String replaceAll(String modifiable, String pattern, String with) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(modifiable);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, with);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
