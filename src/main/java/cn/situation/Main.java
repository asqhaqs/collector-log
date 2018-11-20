package cn.situation;

import cn.situation.udp.SyslogInput;

public class Main {

    public static void main(String[] args) {
        SyslogInput input = new SyslogInput();
        new Thread(input).start();
    }
}
