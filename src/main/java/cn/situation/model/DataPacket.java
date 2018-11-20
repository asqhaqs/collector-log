package cn.situation.model;

import java.io.Serializable;

public class DataPacket implements Serializable {

    private MetaData head;

    private byte[] body;

    public MetaData getHead() {
        return head;
    }

    public void setHead(MetaData head) {
        this.head = head;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
