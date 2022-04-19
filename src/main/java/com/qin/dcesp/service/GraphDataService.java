package com.qin.dcesp.service;

import com.qin.dcesp.dao.GraphDataMapper;
import com.qin.dcesp.entity.Circuitdiagram;
import com.qin.dcesp.entity.GraphData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 电路图节点Sevice
 * */
@Service
@Transactional
public class GraphDataService {

    @Autowired
    GraphDataMapper graphDataMapper;

    public int saveOneGraphData(GraphData graphData){
        return graphDataMapper.saveOneGraphData(graphData);
    }

    public int saveGraphDataByCdm(List<GraphData> graphDataList){
        for(GraphData graphData : graphDataList){
            saveOneGraphData(graphData);
        }
        return graphDataList.size();
    }

    public List<GraphData> queryGraphDataByCdm(Circuitdiagram cdm){
        return graphDataMapper.queryGraphDataByCdm(cdm);
    }

    public GraphData queryGraphDataById(int id){
        return graphDataMapper.queryGraphDataById(id);
    }

    public int updateGraphDataById(int id,GraphData graphData){
        return graphDataMapper.updateGraphDataById(id, graphData);
    }

    public int deleteGraphDataById(int id){
        return graphDataMapper.deleteGraphDataById(id);
    }

    public int deleteGraphDataByCdm(Circuitdiagram cdm){
        return graphDataMapper.deleteGraphDataByCdm(cdm);
    }
}
