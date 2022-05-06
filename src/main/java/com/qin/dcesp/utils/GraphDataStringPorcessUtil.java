package com.qin.dcesp.utils;


//用于处理打包数据的工具类.
/** 拟定数据结构: Map<String,List<String>>
 * 原先的格式:
 * {
 *      lowerPowerTo : [GraphData(cdmId=-1, graphId=-1, from=control, fromPort=control_port5, to=74LS20, toPort=74LS20_port12)],
 *      powerToNode : [GraphData(cdmId=-1, graphId=-1, from=power, fromPort=power_port2, to=74LS20, toPort=74LS20_port14)],
 *      highPowerTo : [GraphData(cdmId=-1, graphId=-1, from=control, fromPort=control_port4, to=74LS20, toPort=74LS20_port13)],
 *      nodeToCheck : [GraphData(cdmId=-1, graphId=-1, from=74LS20, fromPort=74LS20_port8, to=check, toPort=check_port1)]
 * }
 * 拟定格式:
 * 注意点: 当前为单实验模式,也就是单次实验只能使用1个芯片.
 *  {
 *      lowerPowerTo : ["c5",...],//表示开启八路继电器的哪路,并且是输出低电平
 *      powerToNode : ["p2",...],//表示开启继电器的哪一路,注,此处是使用了3-8译码器的,可能可以再封装一次
 *      highPowerTo : ["c4",...],//同lowerPowerTo
 *      nodeToCheck : ["c1",...] //表示那一端检查端需要进行检查.
 *  }
 * */
public class GraphDataStringPorcessUtil {

    /**
     * port : 传入的表示port的字符串,比如graphData对象中的formPort,ToPort.
     * 返回值 : 端口号字符串,比如传入control_port15,返回字符串15.
     * */
    public static String getPort(String port){
        StringBuilder result = new StringBuilder();
        for(char c : port.split("_")[1].toCharArray()){
            if(c >= '0' && c <= '9'){
                result.append(c);
            }
        }
        return result.toString();
    }
}
