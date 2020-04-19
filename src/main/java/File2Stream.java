import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class File2Stream implements X2Stream{

        private static final int PORT = 30833;
        private List<String> processedFileList;

        public File2Stream() {
            processedFileList = new ArrayList<>();
        }

        public void sendFile(String filePath) {
            ServerSocket server = null;
            Socket socket = null;
            DataOutputStream out = null;
            File file = new File(filePath);
            BufferedReader reader = null;
            try {
                server = new ServerSocket(PORT);
                socket = server.accept();

                out = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    reader = new BufferedReader(new FileReader(file));
                    String tempString = null;
                    while ((tempString = reader.readLine()) != null) {
                        if (!processedFileList.contains(tempString.strip())) {
                            File logFile = new File(tempString);
                            BufferedReader logReader = new BufferedReader(new FileReader(logFile));
                            String log = null;
                            while ((log = logReader.readLine()) != null) {
                                out.writeBytes(log.strip()+'\n');
                                out.flush();
                            }
                            logReader.close();
                        }
                        processedFileList.add(tempString.strip());
                    }
                    reader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }

    public void logSplit() {
    }
}
