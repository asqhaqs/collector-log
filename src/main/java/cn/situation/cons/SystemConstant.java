package cn.situation.cons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class SystemConstant {

    private static final Logger LOG = LoggerFactory.getLogger(SystemConstant.class);

    private static Properties props = new Properties();

    private static void init(String fileName) {
        InputStream in = null;
        try {
            // File file = new File(fileName);
            // in = new FileInputStream(file);
            in = SystemConstant.class.getResourceAsStream("/" + fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            props.load(inputStreamReader);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }
        }
    }

    private static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    static {
        LOG.info("init properties");
        init("app.properties");
    }

    public static final String HOST = getProperty("host", "127.0.0.1");
    public static final String PORT = getProperty("port", "514");
    public static final String BUFFER_SIZE = getProperty("bufferSize", "1024");
    public static final String IP_LIST = getProperty("ipList", "");
    public static final String FILTER_MODE = getProperty("filterMode", "0");
    public static final String BROKER_URL = getProperty("broker_url", "");
    public static final String IS_KERBEROS = getProperty("is_kerberos", "false");

    public static final String TOPIC = getProperty("topic", "");

    public static final String KAFKA_KERBEROS_PATH = getProperty("kafka_kerberos_path", "/home/krb");



}
