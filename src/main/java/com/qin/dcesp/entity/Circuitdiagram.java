package com.qin.dcesp.entity;

import java.util.List;

/**电路图实体
 * 前端的电路图数据:
 *    [
 *      {
 *          from:"连接的源节点,比如74LS20,1",fromPort:"连接的源节点的端口",
 *          to:"本节点,也就是被连到的节点",toPort: "被连到节点与源节点连接的端口"
 *      },
 *      {}....
 *    ]
 *    对应表:Circuitdiagram表
 * */
public class Circuitdiagram {

    private int cdmId;//电路图id
    private int userId;//本图对应的用户
    private String cdmName;//电路图名称
    private List<Integer> graphDataIds;//电路图节点id数据

    public Circuitdiagram() {
    }

    public int getCdmId() {
        return cdmId;
    }

    public void setCdmId(int cdmId) {
        this.cdmId = cdmId;
    }

    public List<Integer> getGraphDataIds() {
        return graphDataIds;
    }

    public void setGraphDataIds(List<Integer> graphDataIds) {
        this.graphDataIds = graphDataIds;
    }

    public Circuitdiagram(int cdmId, int userId, String cdmName, List<Integer> graphDataIds) {
        this.cdmId = cdmId;
        this.userId = userId;
        this.cdmName = cdmName;
        this.graphDataIds = graphDataIds;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getCdmName() {
        return cdmName;
    }

    public void setCdmName(String cdmName) {
        this.cdmName = cdmName;
    }
}
