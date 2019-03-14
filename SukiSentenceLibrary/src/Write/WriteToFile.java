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
package Write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Heidi Jauhiainen
 */
public class WriteToFile {
    File file;
    BufferedWriter writer = null;

    public WriteToFile(String filename, Boolean replace) throws IOException {
        file = new File(filename);
        if (replace) {
            Files.deleteIfExists(file.toPath());
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        }
        catch (Exception e) {
            System.out.println("Error while creating writer: "+e.getMessage());
        }
    }
    
    public void write(String sentence) throws IOException {
        writer.write(sentence+"\n");
    }
    public void end() throws IOException {
        writer.close();
    }
}
