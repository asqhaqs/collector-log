package cn.situation.udp;

import cn.situation.cons.SystemConstant;
import cn.situation.model.DataPacket;
import cn.situation.model.MetaData;
import cn.situation.producer.KafkaBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class SyslogInput extends AbstractUdp {

    private static final Logger LOG = LoggerFactory.getLogger(SyslogInput.class);

    public static KafkaBroker broker = KafkaBroker.getInstance();

    private String host;
    private int port = 514;

    private void updateParam() throws SocketException {
        host = SystemConstant.HOST;
        port = Integer.valueOf(SystemConstant.PORT);
        port = (port < 1024) ? port + 20000 : port;
        initParam(host, port);
    }

    @Override
    protected void action(String remoteAddress, byte[] data) throws Exception {
        MetaData meta = new MetaData();
        meta.setSource(remoteAddress);
        DataPacket dp = new DataPacket();
        dp.setHead(meta);
        dp.setBody(data);
        broker.deliver(dp);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                updateParam();
                handle();
            } catch (Exception e) {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException ie) {
                    LOG.error(ie.getMessage(), ie);
                }
                LOG.error(e.getMessage(), e);
            }
        }
        stop();
    }
}