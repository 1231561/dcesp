package com.qin.dcesp.entity;


import lombok.ToString;

/**
 * 电路图节点数据类
 *  对应表:GraphData表
 * */
@ToString
public class GraphData {
    /*
    * from : 连接的源节点,也就是连接是从谁那开始的
    * fromPort : 连接的源节点的端口,也就是连接是从谁的哪个端点开始的
    * to : 连接的目的节点,也就是连接是连到谁那的
    * toPort : 连接的目的节点端口,也就是连接是连到谁的哪个节点
    * */
    private int cdmId;//所属的电路图id
    private int graphId;//本连接的id,主键
    private String from;
    private String fromPort;
    private String to;
    private String toPort;

    public GraphData() {
    }

    public GraphData(int cdmId,int graphId,String from, String fromPort, String to, String toPort) {
        this.cdmId = cdmId;
        this.graphId = graphId;
        this.from = from;
        this.fromPort = fromPort;
        this.to = to;
        this.toPort = toPort;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromPort() {
        return fromPort;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToPort() {
        return toPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    public int getCdmIdId() {
        return cdmId;
    }

    public void setCdmId(int cdmId) {
        this.cdmId = cdmId;
    }

    public int getCdmId() {
        return cdmId;
    }

    public int getGraphId() {
        return graphId;
    }

    public void setGraphId(int graphId) {
        this.graphId = graphId;
    }
}
