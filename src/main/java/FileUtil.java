import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xiren on 6/9/16.
 */
public class FileUtil {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("./src/main/resources/data/002204.csv"));
        String tomorrow = "";
        lines = lines.stream().filter(line -> !line.split(",")[5].equals("0")).collect(Collectors.toList());
        List<String> data = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");
            data.add(array[1] + "," + array[4] + "," + array[5] + "," + tomorrow);
            double open = Double.parseDouble(array[1]);
            double close = Double.parseDouble(array[4]);
            if (close - open > 0) {
                tomorrow = "1";
            } else {
                tomorrow = "0";
            }

        }
        data.remove(0);
        Files.write(Paths.get("./src/main/resources/data/data_002204.csv"), data);

        cleanDataWithStep(8);
    }

    public static void cleanDataWithStep(int step) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("./src/main/resources/data/002204.csv"));
        lines = lines.stream().filter(line -> Double.parseDouble(line.split(",")[5]) != Double.parseDouble("0")).collect(Collectors.toList());
        Collections.reverse(lines);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] array = line.split(",");
            int index = i + step < lines.size() ? i + step : lines.size() - 1;
            String targetLine = lines.get(index);
            String[] targetArray = targetLine.split(",");
            double todayClose = Double.parseDouble(array[5]);
            double targetClose = Double.parseDouble(targetArray[5]);
            int target = 0;
            if(targetClose - todayClose > 0){
                target = 1;
            }
            data.add(array[1] + "," + array[4] + "," + array[5] + "," + target);
        }
        Files.write(Paths.get("./src/main/resources/data/data_002204_step"+step+".csv"), data);
    }
}
