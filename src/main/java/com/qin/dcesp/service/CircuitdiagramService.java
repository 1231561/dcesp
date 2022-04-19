package com.qin.dcesp.service;


import com.qin.dcesp.dao.CircuitdiagramMapper;
import com.qin.dcesp.entity.Circuitdiagram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**电路图信息Service
 * */
@Service
@Transactional
public class CircuitdiagramService {

    @Autowired
    CircuitdiagramMapper circuitdiagramMapper;

    @Autowired
    GraphDataService graphDataService;

    //获取用户的所有电路图信息.
    public List<Circuitdiagram> selectAllCdm(int userId){
        return circuitdiagramMapper.selectAllCdmById(userId);
    }
    //查询一个电路图
    public Circuitdiagram selectCdmById(int cdmId){
        return circuitdiagramMapper.selectCdmById(cdmId);
    }
    //新增一个电路图
    public int insertCdm(Circuitdiagram circuitdiagram){
        return circuitdiagramMapper.saveCdm(circuitdiagram);
    }

    //修改一个电路图
    public int updateCdm(Circuitdiagram circuitdiagram){
        //这里只需要去修改电路图中的节点信息,增删改节点信息即可.所以需要去调用GraphDataService.
        return circuitdiagramMapper.updateCdmById(circuitdiagram.getCdmId(), circuitdiagram);
    }

    //删除一个电路图
    public int deleteCdmById(int cdmId){
        return circuitdiagramMapper.deleteCdmById(cdmId);
    }
}
