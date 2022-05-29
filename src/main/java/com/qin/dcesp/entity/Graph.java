package com.qin.dcesp.entity;

import java.util.*;

/**
 * 电路图数据
 * 将电路节点排序
 * 排序规则:
 *    以电源节点为起始,以接地节点为结束,进行拓扑排序
 * */
public class Graph {

    private List<GraphData> allNodes;

    private List<List<GraphData>> sortNodes;

    public List<List<GraphData>> getSortNodes() {
        return sortNodes;
    }

    public void setSortNodes(List<List<GraphData>> sortNodes) {
        this.sortNodes = sortNodes;
    }

    public List<GraphData> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<GraphData> allNodes) {
        this.allNodes = allNodes;
    }

    public Graph(List<GraphData> graphDataList){
        allNodes = graphDataList;
        sortNodes = new ArrayList<>();
    }

    public Map<String,List<List<String>>> getRoad(){
        return getRoadFromPowerToGround(allNodes);
    }

    //寻找所有从电源到接地端的路径
    //首先根据给定信息构建邻接表
    //给定信息GraphData格式:from,to,fromport,toport
    //综合路径信息:from+fromport.to+toport
    private Map<String,List<List<String>>> getRoadFromPowerToGround(List<GraphData> allNodes) {
        //构建邻接表:
        Map<String, List<String>> adjacencyTable = new HashMap<>();
        for (GraphData node : allNodes) {
            if (!adjacencyTable.containsKey(node.getFrom())) {
                List<String> list = new ArrayList<>();
                list.add(node.getTo());
                adjacencyTable.put(node.getFrom(), list);
            } else {
                List<String> list = adjacencyTable.get(node.getFrom());
                list.add(node.getTo());
                adjacencyTable.put(node.getFrom(), list);
            }
            if (!adjacencyTable.containsKey(node.getTo())) {
                List<String> list = new ArrayList<>();
                list.add(node.getFrom());
                adjacencyTable.put(node.getTo(), list);
            } else {
                List<String> list = adjacencyTable.get(node.getTo());
                list.add(node.getFrom());
                adjacencyTable.put(node.getTo(), list);
            }
        }
        //寻找所有从电源到接地端的路径以及所有从电源到检测端的路径(检测端即为电平检测端)
        //先找电源到地的
        //然后还要找电源到检测端的
        List<String> powerNode = new ArrayList<>();
        for(String key : adjacencyTable.keySet()){
            if(key.contains("电源")){
                dfs(adjacencyTable,  key, "接地",new HashSet<>(),new ArrayList<>(),powerToGroundRoads);
                dfs(adjacencyTable, key, "电平检测",new HashSet<>(),new ArrayList<>(),powerToMeasure);
                dfs(adjacencyTable,key,"示波器",new HashSet<>(),new ArrayList<>(),powerToMeasure);
            }
            if(key.contains("高电平")){
                dfs(adjacencyTable,key,"接地",new HashSet<>(),new ArrayList<>(),highPowerTo);
            }
            if(key.contains("低电平")){
                dfs(adjacencyTable,key,"接地",new HashSet<>(),new ArrayList<>(),lowerPowerTo);
            }
        }
        Map<String, List<List<String>>> result = new HashMap<>();
        result.put("powerToGround", powerToGroundRoads);
        result.put("powerToMeasure", powerToMeasure);
        result.put("highPowerTo",highPowerTo);
        result.put("lowerPowerTo",lowerPowerTo);
        return result;
    }

    private List<List<String>> powerToGroundRoads = new ArrayList<>();
    private List<List<String>> powerToMeasure = new ArrayList<>();
    private List<List<String>> highPowerTo = new ArrayList<>();
    private List<List<String>> lowerPowerTo = new ArrayList<>();


    /**
     * 方法功能 : 深度优先遍历,遍历给定nowNode到end节点的所有路径并去重
     * @param adjTable : 邻接表
     * @param nowNode : 当前节点
     * @param end : 结束节点
     * @param visited : 已经访问过的节点
     * @param nowRoad : 当前路径
     * @param res : 结果
     * */
    private void dfs(Map<String,List<String>> adjTable,String nowNode,
                     String end,Set<String> visited,
                     List<String> nowRoad,List<List<String>> res){
        if(visited.contains(nowNode)){
            return;
        }
        if(nowNode.contains(end)){
            nowRoad.add(nowNode);
            res.add(new ArrayList<>(nowRoad));
            nowRoad.remove(nowRoad.size() - 1);
            return;
        }
        for (String node : adjTable.get(nowNode)){
            if(!visited.contains(node)){
                nowRoad.add(nowNode);
                visited.add(nowNode);
                dfs(adjTable,node,end,visited,nowRoad,res);
                visited.remove(nowNode);
                nowRoad.remove(nowRoad.size() - 1);
            }
        }
    }

}
