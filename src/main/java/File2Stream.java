import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class File2Stream implements X2Stream {
    private static final Logger logger = Logger.getLogger(File2Stream.class);
    private final List<String> processedFileList;

    public File2Stream() {
        processedFileList = new ArrayList<>();
    }

    public void sendFile(String filePath, String printSocketLog, int socketOutputPort) {
        ServerSocket server = null;
        Socket socket = null;
        DataOutputStream out = null;
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            logger.info("Start File2Stream!");
            server = new ServerSocket(socketOutputPort);
            socket = server.accept();
            logger.info("Taskmanager connection successful, port:" + socketOutputPort);
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                FileReader fr = new FileReader(file);
                reader = new BufferedReader(fr);
                String tempString;
                while ((tempString = reader.readLine()) != null) {
                    if (!processedFileList.contains(tempString.strip())) {
                        File logFile = new File(tempString);
                        if (!logFile.exists()) {
                            continue;
                        }
                        FileReader fr1 = new FileReader(logFile);
                        BufferedReader logReader = new BufferedReader(fr1);
                        String log;
                        while ((log = logReader.readLine()) != null) {
                            if ("true".equalsIgnoreCase(printSocketLog)) {
                                logger.info(log.strip());
                            }
                            out.writeBytes(log.strip() + '\n');
                            out.flush();
                        }
                        logReader.close();
                        fr1.close();
                        processedFileList.add(tempString.strip());
                    }

                }
                reader.close();
                fr.close();
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
