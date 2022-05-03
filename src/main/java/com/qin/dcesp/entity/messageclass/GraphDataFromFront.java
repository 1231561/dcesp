package com.qin.dcesp.entity.messageclass;

import lombok.ToString;

@ToString
public class GraphDataFromFront {
    private int __gohashid;
    private String from;
    private String to;
    private String fromPort;
    private String toPort;

    public GraphDataFromFront() {
    }

    public GraphDataFromFront(int __gohashid, String from, String to, String fromPort, String toPort) {
        this.__gohashid = __gohashid;
        this.from = from;
        this.to = to;
        this.fromPort = fromPort;
        this.toPort = toPort;
    }

    public int get__gohashid() {
        return __gohashid;
    }

    public void set__gohashid(int __gohashid) {
        this.__gohashid = __gohashid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }
}
