import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserUtil {
    private static final Logger log = LoggerFactory.getLogger(ParserUtil.class);

    private static final String source = ConfigProperties.getProperty("tdx.daily.source");
    private static final String destination = ConfigProperties.getProperty("spark.daily.destination");

    private static int readArrayIndex = 0;

    public static void parserTDX() throws IOException {
        log.info("Kick off ... ");
        long start = System.currentTimeMillis();
        List<Path> list = Files.list(Paths.get(source)).collect(Collectors.toList());
        long getFileListTime = System.currentTimeMillis();
        log.info("Load all files cost: " + (getFileListTime - start));

        for (Path path : list) {
            byte[] data = Files.readAllBytes(path);
            int days = (int) data.length / 32;
            String tdxCode = path.getFileName().toString();

            List<TDXRecord> tdxs = new ArrayList<TDXRecord>();
            for (int i = 0; i < days; i++) {
                TDXRecord tdx = new TDXRecord();
                tdx.setDate(readInt32(data));
                tdx.setOpen(readInt32(data));
                tdx.setHigh(readInt32(data));
                tdx.setLow(readInt32(data));
                tdx.setClose(readInt32(data));
                tdx.setAmount(readInt32(data));
                tdx.setVol(readInt32(data));
                tdx.setPreClose(readInt32(data));
                tdxs.add(tdx);
            }

            log.info("Code " + tdxCode + " done.");
            readArrayIndex = 0;

            saveToNewFile(tdxs, tdxCode.substring(0, tdxCode.indexOf(".")));
        }

        log.info("Parser all files completed: " + (System.currentTimeMillis() - getFileListTime));
    }

    private static void saveToNewFile(List<TDXRecord> tdxs, String code) {
        String destFile = destination + code + ".txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(destFile))) {
            for (TDXRecord tdx : tdxs) {
                writer.write(tdx.toString(), 0, tdx.toString().length());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private static long readInt32(byte[] bufInt) {
        long i = 0;
        for (int j = 0; j < 4; readArrayIndex++, j++) {
            i += toInt(bufInt[readArrayIndex]) << (j << 3);
        }
        return i;
    }

    private static int toInt(int b) {
        return b >= 0 ? (int) b : (int) (b + 256);
    }

    public static void main(String[] args) throws Exception {
        ParserUtil.parserTDX();
    }

}
