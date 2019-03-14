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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Heidi Jauhiainen
 */
public class IdentifyLanguage {
    public static String testLanguage(String sentence, String server, int port) {
        Socket clientSocket = null;
        DataOutputStream outToServer = null;
        BufferedReader inFromServer = null;
        String language = "";
        byte[] buf;
        try {
            if (sentence.equals("")) {
                return language;
            }
            else {
                sentence += "\n";
                buf = sentence.getBytes("UTF-8");
                clientSocket = new Socket(server, port);
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                try {
                    outToServer.write(buf, 0, buf.length);
                } 
                catch (Exception e) {
                    System.out.println(e);
                }
                try {
                    language = inFromServer.readLine();
                } 
                catch (Exception e) {
                }
                finally {
                    outToServer.close();
                    inFromServer.close();
                    clientSocket.close();
                }
            }
        }
        catch(UnknownHostException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return language;
    }
}
