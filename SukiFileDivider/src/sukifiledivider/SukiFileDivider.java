/*
    SukiFileDivider.java splits a file to 50 parts with equal number of rows
    Copyright (C) 2018 Tommi Jauhiainen

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

package sukifiledivider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SukiFileDivider {

    /**
     * This method is part of SukiSentencePipeline
     * https://github.com/uhdigihum/SUKISentencePipeline
     * 
     * @param args the command line arguments
     * args[0] = file to read
     * args[1] = folder to write to
     */
    private static BufferedWriter writer = null;
    
    public static void main(String[] args) throws IOException {
        int howManyParts = 50;
		int totalRowCount = 0;
                
        File file = new File(args[0]);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int tempRowCounter = 0;
			String line = "";
			while ((line = reader.readLine()) != null) {
				tempRowCounter++;
			}
			totalRowCount = tempRowCounter;
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int partCounter = 1;
			
			int tempRowCounter = 0;
			String outfilename = file.getName().replaceAll("$","."+partCounter);
			outfilename = outfilename.replaceAll("^",args[1]+"/");
			File outfile = new File(outfilename);
			try {
				outfile.createNewFile();
				writer = new BufferedWriter(new FileWriter(outfile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String line = "";
			double rowsPerFile = totalRowCount/howManyParts;

			while ((line = reader.readLine()) != null) {
				if (tempRowCounter > rowsPerFile) {
					writer.close();
					partCounter++;
					tempRowCounter = 0;
					outfilename = file.getName().replaceAll("$","."+partCounter);
					outfilename = outfilename.replaceAll("^",args[1]+"/");
					outfile = new File(outfilename);
					try {
						outfile.createNewFile();
						writer = new BufferedWriter(new FileWriter(outfile));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				writer.write(line + "\n");
				tempRowCounter++;
			}
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
