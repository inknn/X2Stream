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
    private final Logger logger = Logger.getLogger(File2Stream.class);
    private final List<String> processedFileList;

    public File2Stream() {
        processedFileList = new ArrayList<>();
    }

    public void sendFile(String logFilePaths, String printSocketLog, int socketOutputPort) {
        ServerSocket server = null;
        Socket socket = null;
        DataOutputStream out = null;
        File logFilePathsFile = new File(logFilePaths);
        BufferedReader reader = null;
        try {
            logger.info("Start File2Stream!");
            server = new ServerSocket(socketOutputPort);
            socket = server.accept();
            logger.info("Taskmanager connection successful, port:" + socketOutputPort);
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                FileReader logFilePathsFileReader = new FileReader(logFilePathsFile);
                reader = new BufferedReader(logFilePathsFileReader);
                String tempString;
                while ((tempString = reader.readLine()) != null) {
                    File logFile = new File(tempString);
                    if (logFile.isDirectory()) {
                        File[] fileList = logFile.listFiles();
                        for (File file : fileList) {
                            sendLogFile(file, out, printSocketLog);
                        }
                    } else {
                        sendLogFile(logFile, out, printSocketLog);
                    }
                }

                reader.close();
                logFilePathsFileReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void sendLogFile(File file, DataOutputStream out, String printSocketLog) {
        if (file.exists()) {
            if (!processedFileList.contains(file.toString())) {
                try {
                    logger.info("Begin process file:" + file.toString());
                    FileReader fr1 = new FileReader(file);
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
                    processedFileList.add(file.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void logSplit() {
    }
}
