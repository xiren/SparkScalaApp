import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kwang3 on 2016/6/22.
 */
public class XueqiuConnector {

    private static final String LOGIN_URL = "http://xueqiu.com/";
    private static final String REQUEST_URL = "http://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1day&type=normal&begin=%S&end=%s";

    private static final long START_TIME = Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();

    public void send(String symbol) throws Exception {
        URL reqUrl = new URL(String.format(REQUEST_URL, symbol, START_TIME, System.currentTimeMillis()));
        URLConnection conn = reqUrl.openConnection();
        conn.setRequestProperty("Cookie", getCookie());
        conn.connect();
        InputStream in = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        for (String line = ""; line != null; line = br.readLine()) {
            System.out.println(line);
        }
    }

    private String getCookie() throws Exception {
        URL loginUrl = new URL(LOGIN_URL);
        URLConnection conn = loginUrl.openConnection();
        conn.connect();
        String headerName = null;
        Map<String, String> COOKIES = new Hashtable<String, String>();
        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; ++i) {
            if ("Set-Cookie".equals(headerName)) {
                String cookie = conn.getHeaderField(i).split(";")[0];
                String[] pairParts = cookie.split("=", 2);
                COOKIES.put(pairParts[0], pairParts[1]);
            }
        }

        return COOKIES.entrySet().stream().map(r -> r.getKey() + "=" + r.getValue()).collect(Collectors.joining("; "));
    }

    public static void main(String[] args) throws Exception {
        XueqiuConnector connector = new XueqiuConnector();
        connector.send("SZ002204");
    }
}
