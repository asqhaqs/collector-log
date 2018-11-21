package cn.situation.udp;

import cn.situation.cons.SystemConstant;
import cn.situation.model.DataPacket;
import cn.situation.model.MetaData;
import cn.situation.producer.KafkaBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class SyslogWorker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SyslogWorker.class);

    private static KafkaBroker broker = KafkaBroker.getInstance();

    private ZMQ.Context context;

    public SyslogWorker (ZMQ.Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        socket.connect ("inproc://workers");
        socket.setHWM(Integer.valueOf(SystemConstant.ZMQ_RCVHWM));
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] result = socket.recv(0);
                LOG.info(String.format("[%s]: result<%s>, thread-id<%s>", "SyslogWorker.run", new String(result),
                        getId()));
                action(result);
            } catch (Exception e) {
                LOG.error("[%s]: message<%s>, thread-id<%s>", "SyslogWorker.run", e.getMessage(), getId());
                LOG.error(e.getMessage(), e);
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException ie) {
                    LOG.error(ie.getMessage(), ie);
                }
            }
        }
        socket.close();
    }

    private void action(byte[] data) throws Exception {
        DataPacket dp = new DataPacket();
        dp.setHead(null);
        dp.setBody(data);
        broker.deliver(dp);
    }

    private long getId() {
        return Thread.currentThread().getId();
    }
}
