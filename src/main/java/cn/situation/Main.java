package cn.situation;

import cn.situation.cons.SystemConstant;
import cn.situation.udp.SyslogWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        byte[] buffer = new byte[Integer.valueOf(SystemConstant.BUFFER_SIZE)];
        String host = SystemConstant.HOST;
        int port = Integer.valueOf(SystemConstant.PORT);
        InetSocketAddress address = new InetSocketAddress(host, port);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(address);
        } catch (SocketException e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
        LOG.info(String.format("[%s]: host<%s>, port<%s>", "main", address.getHostString(),
                address.getPort()));

        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket sender = context.socket(ZMQ.PUSH);
        sender.bind("inproc://workers");
        sender.setHWM(Integer.valueOf(SystemConstant.ZMQ_SNDHWM));

        int threadNum = Integer.valueOf(SystemConstant.WORKER_THREAD_NUM);
        for(int num = 0; num < threadNum; num++) {
            SyslogWorker worker = new SyslogWorker(context);
            new Thread(worker).start();
        }

        LOG.error(String.format("[%s]: message<%s>", "main", "start receive log..."));

        while (!Thread.currentThread ().isInterrupted ()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                data = Arrays.copyOfRange(data, 0, packet.getLength());
                sender.send(data, 0);
            } catch (IOException e) {
                LOG.error(String.format("[%s]: message<%s>", "handle", "UDP接收数据错误"));
                LOG.error(e.getMessage(), e);
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException ie) {
                    LOG.error(ie.getMessage(), ie);
                }
            }
        }
        sender.close();
        context.term();
    }
}
