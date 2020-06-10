import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Start {
    private static final Logger log = Logger.getLogger(Start.class);

    public static void main(String[] args) {

        String filePath = new File("/opt/resources/config.properties").getAbsolutePath();
        log.info("Start X2Stream!");
        try (InputStream in = new BufferedInputStream(new FileInputStream(filePath))) {
            Properties p = new Properties();
            p.load(in);
            String sourceName = p.getProperty("sourceName");
            String socketType = p.getProperty("socketType");
            String printSocketLog = p.getProperty("printSocketLog");
            int socketPort = Integer.parseInt(p.getProperty("socketPort"));
            int logStashPort = Integer.parseInt(p.getProperty("logStashPort"));
            if ("socket".equalsIgnoreCase(sourceName)) {
                if ("logStash".equalsIgnoreCase(socketType)) {
                    LogStash2Stream logStash2Stream = new LogStash2Stream();
                    logStash2Stream.sendMessageFromLogStash(printSocketLog, socketPort, logStashPort);
                } else if ("file".equalsIgnoreCase(socketType)) {
                    File2Stream file2Stream = new File2Stream();
                    file2Stream.sendFile("/data/logFilePaths", printSocketLog, socketPort);
                } else {
                    log.info("Unsupported socket type:" + socketType);
                }
            }
        } catch (IOException e) {
            log.error("Read file error:" + e);
        } finally {
            log.info("End X2Stream!");
        }
    }
}
