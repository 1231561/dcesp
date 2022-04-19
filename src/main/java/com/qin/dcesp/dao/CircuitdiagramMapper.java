package com.qin.dcesp.dao;


import com.qin.dcesp.entity.Circuitdiagram;
import com.qin.dcesp.entity.GraphData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CircuitdiagramMapper {

    //根据用户id查询用户历史电路图
    List<Circuitdiagram> selectAllCdmById(int userId);

    //根据电路图id获取其所有的连接id
    String selectGraphIdsById(int cdmId);

    //根据id查询电路图
    Circuitdiagram selectCdmById(int cdmId);

    //保存电路图
    int saveCdm(Circuitdiagram cdm);

    //保存一个电路图的一个连接
    int saveGraphData(int cdmId,GraphData graphData);

    //修改电路图,不实现不使用该方法,改为修改电路节点.
    int updateCdmById(int cdmId,Circuitdiagram cdm);

    //根据修改的电路图修改连接,不实现不使用该方法,改为修改电路节点.
    int updateGraphData(int cdmId,GraphData graphData);

    //删除电路图
    int deleteCdmById(int cdmId);
}
