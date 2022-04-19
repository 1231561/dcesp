package com.qin.dcesp.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电路图测量数据
 * 对应表:
 *  GraphMeasureData表
 * */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GraphMeasureData {
    private int cdmId;
    private String from;
    private String to;
    private String dataType;
    private String data;
}
