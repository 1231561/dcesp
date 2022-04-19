package com.qin.dcesp.dao;


import com.qin.dcesp.entity.Circuitdiagram;
import com.qin.dcesp.entity.GraphData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GraphDataMapper {

    //保存单个节点数据
    int saveOneGraphData(GraphData graphData);

    //根据电路图保存节点数据
    int saveGraphDataByCdm(List<GraphData> graphDataList);

    //根据电路图查询节点数据
    List<GraphData> queryGraphDataByCdm(Circuitdiagram cdm);

    //根据id查询单个节点数据
    GraphData queryGraphDataById(int id);

    //根据id修改单个节点数据
    int updateGraphDataById(int id,GraphData graphData);

    //根据电路图修改对应节点数据
    int updateGraphDataByCdm(Circuitdiagram cdm,List<GraphData> graphDataList);

    //根据id删除单个节点数据
    int deleteGraphDataById(int id);

    //根据电路图删除所有节点数据
    int deleteGraphDataByCdm(Circuitdiagram cdm);
}
