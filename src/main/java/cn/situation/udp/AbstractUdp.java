package cn.situation.udp;

import cn.situation.cons.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUdp implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractUdp.class);

    public static final int FILTER_NONE = 0;
    // public static final int FILTER_BLACK_LIST = 1;
    public static final int FILTER_WHITE_LIST = 2;

    // public static final int SUCCESS = 0;
    // public static final int FAIL = 1;


   private byte[] buffer = null;
    private DatagramSocket socket = null;
    private InetSocketAddress socketAddress = null;
    /**
     * 支持过滤模式，不过滤，黑名单，白名单
     */
    private int filterMode = FILTER_NONE;
    private Set<String> ipSet;
    // private volatile boolean listen = false;

    protected void initParam(String host, int port) throws SocketException {
        if (!isRunning()) {
            buffer = new byte[Integer.valueOf(SystemConstant.BUFFER_SIZE)];
            filterMode = Integer.valueOf(SystemConstant.FILTER_MODE);
            if (filterMode != FILTER_NONE) {
                String ips = SystemConstant.IP_LIST;
                ipSet = new HashSet<>();
                if (ips != null) {
                    String[] ip = ips.split(",");
                    for (String p : ip) {
                        ipSet.add(p);
                    }
                }
            }
            InetSocketAddress address = new InetSocketAddress(host, port);
            socketAddress = address;
            beforeStart();
        }
    }

    private boolean isRunning() {
        return null != socket;
    }

    private void beforeStart() throws SocketException {
        try {
            socket = new DatagramSocket(socketAddress);
            LOG.info(String.format("[%s]: host<%s>, port<%s>", "beforeStart", socketAddress.getHostString(),
                    socketAddress.getPort()));
        } catch (SocketException e) {
            LOG.error(String.format("[%s]: message<%s>", "beforeStart", "创建UDP端口失败"));
            throw e;
        }
    }

    protected void handle() throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            String remoteAddress = packet.getAddress().getHostAddress();
            if (filterMode != FILTER_NONE) {
                boolean in = ipSet.contains(remoteAddress);
                if (in ^ (filterMode == FILTER_WHITE_LIST)) {
                    return;
                }
            }
            byte[] data = packet.getData();
            data = Arrays.copyOfRange(data, 0, packet.getLength());
            action(remoteAddress, data);
        } catch (IOException e) {
            LOG.error(String.format("[%s]: message<%s>", "handle", "UDP接收数据错误"));
            throw e;
        }
    }

    protected abstract void action(String remoteAddress, byte[] data) throws Exception;

    protected void stop() {
        if (null != socket) {
            socket.close();
        }
        LOG.info(String.format("[%s]: port<%s>, message<%s>", "stop", socketAddress.getPort(), "UDP端口关闭"));
    }
}
