import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class LogStash2Stream implements X2Stream {
    private static final Logger logger = Logger.getLogger(LogStash2Stream.class);
    private Socket logStashSocket = null;
    private ServerSocket logStashServerSocket = null;

    public LogStash2Stream() {
    }

    public void sendMessageFromLogStash(String printSocketLog, int socketPort, int logStashPort) {
        ServerSocket logFlashServerSocket = null;
        Socket logFlashSocket = null;
        DataOutputStream out = null;
        try {
            logger.info("Start LogStash2Stream!");
            logFlashServerSocket = new ServerSocket(socketPort);
            logFlashSocket = logFlashServerSocket.accept();
            logger.info("Taskmanager connection successful, port:" + socketPort);

            logStashServerSocket = new ServerSocket(logStashPort);
            logStashSocket = logStashServerSocket.accept();
            logger.info("LogStash connection successful, port:" + logStashPort);
            out = new DataOutputStream(logFlashSocket.getOutputStream());

            while (true) {
                BufferedReader input = new BufferedReader(new InputStreamReader(logStashSocket.getInputStream()));
                String logStashInputString = input.readLine();
                if (logStashInputString == null) {
                    logger.info("LogStash disconnect!");
                    logStashSocket.close();
                    logStashSocket = logStashServerSocket.accept();
                    logger.info("LogStash connection successful, port:" + logStashPort);
                    input = new BufferedReader(new InputStreamReader(logStashSocket.getInputStream()));
                    logStashInputString = input.readLine();
                }
                if ("true".equalsIgnoreCase(printSocketLog)) {
                    logger.info(logStashInputString.strip());
                }
                out.writeBytes(logStashInputString.strip() + '\n');
                out.flush();
            }
        } catch (IOException e) {
            logger.error("Socket error:" + e);
        } finally {
            if (logFlashSocket != null) {
                try {
                    logFlashSocket.close();
                } catch (Exception e) {
                    logFlashSocket = null;
                    logger.error("LogFlash socket error:" + e.getMessage());
                }
            }
            if (logFlashServerSocket != null) {
                try {
                    logFlashServerSocket.close();
                } catch (Exception e) {
                    logFlashServerSocket = null;
                    logger.error("LogFLash serverSocket error:" + e.getMessage());
                }
            }
            if (logStashSocket != null) {
                try {
                    logStashSocket.close();
                } catch (Exception e) {
                    logStashSocket = null;
                    logger.error("LogStash socket error:" + e.getMessage());
                }
            }
            if (logStashServerSocket != null) {
                try {
                    logStashServerSocket.close();
                } catch (Exception e) {
                    logStashServerSocket = null;
                    logger.error("LogStash serverSocket error:" + e.getMessage());
                }
            }
        }
    }

    public void logSplit() {
    }
}
