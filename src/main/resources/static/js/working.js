var diagram = new go.Diagram("paintPanel");
var gm = go.GraphObject.make;
diagram.grid.visible = true;
diagram.toolManager.draggingTool.isGridSnapEnabled = true;
diagram.toolManager.resizingTool.isGridSnapEnabled = true;
function mouseEnter(e, obj) {
    var shape = obj.findObject("SHAPE");
    shape.fill = "#6DAB80";
    shape.stroke = "#A6E6A1";
    var text = obj.findObject("TEXT");
    text.stroke = "white";
}

function mouseLeave(e, obj) {
    var shape = obj.findObject("SHAPE");
    // Return the Shape's fill and stroke to the defaults
    shape.fill = '#96D6D9';
    shape.stroke = null;
    // Return the TextBlock's stroke to its default
    var text = obj.findObject("TEXT");
    text.stroke = "black";
}

/**连线模板
 * 无箭头样式,自动规避节点覆盖连线
 * */
diagram.linkTemplate =
    gm(go.Link, { routing: go.Link.Orthogonal ,curve: go.Link.JumpOver,corner: 10},
        new go.Binding("fromEndSegmentLength"),
        new go.Binding("toEndSegmentLength"),
        gm(go.Shape)
    );

/**构建元器件模板*/
// var power;//电源
// var ground;//地
// var dp;//电平
// var dz;//电阻
// var dr;//电容
// var sbq;//示波器
// var node74LS08;//4*2输入与门-14port
// var node74LS86;//4*2输入异或门-14port
// var node74LS02;//4*2输入或非门-14port
// var node74LS00;//4*2输入与非门-14port
// var node74LS20;//4*2输入与非门-14port
// var node74LS04;//六反向器-14port
// var node74LS32;//4*2输入或门-14port
// var node74LS54;//四路2-3-3-2输入与或非门-14port
// var node74LS138;//3线-8线译码器-16port
// var node74LS151;//8选1数据选择器-16port
// var node74LS153;//双路4选1数据选择器-16port
// var node74LS194;//四位双向通用移位寄存器-16port
// var node74LS112;//下降沿触发双JK触发器-16port
// var node74LS74;//双D触发器-14port
// var node555;//555定时器

var powerAnddpTemplate =
    gm(go.Node,'Auto',
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null}),
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,
                {row: 1,alignment: go.Spot.Bottom}
            ),
            gm(go.TextBlock,{column: 1,row: 0,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            gm(go.Panel,"Horizontal",
                //位置
                {row: 1,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "Port",
                        fromLinkable:true,
                        toLinkable:true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
        )
    );

var groundTemplate =
    gm(go.Node,'Auto',
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null}),
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,
                {row: 0,alignment: go.Spot.Top}
            ),
            gm(go.RowColumnDefinition,
                {row: 1,alignment: go.Spot.Bottom}
            ),
            gm(go.TextBlock,{column: 1,row: 1,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            gm(go.Panel,"Horizontal",
                //位置
                {row: 0,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "Port",
                        fromLinkable:true,
                        toLinkable:true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
        )
    );

var dzAnddrTemplate =
    gm(go.Node,'Auto',
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null}),
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,
                {column: 0,alignment: go.Spot.Left}
            ),
            gm(go.RowColumnDefinition,
                {column: 2,alignment: go.Spot.Right}
            ),
            gm(go.TextBlock,{column: 1,row: 0,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            gm(go.Panel,"Horizontal",
                //位置
                {row: 0,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "Port1",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
            gm(go.Panel,"Horizontal",
                //位置
                {row: 0,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "Port2",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
        )
    );

var node14portTemplate =
    gm(go.Node,"Auto",//自动调节
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        //形状矩形,填充颜色亮蓝
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null}),
        //节点面板为表格样式
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,//第一行样式
                {row: 0,alignment: go.Spot.Top}),
            gm(go.RowColumnDefinition,//第三行样式
                {row: 2,alignment:go.Spot.Bottom}),
            //第二行为文本框,显示元件名字
            gm(go.TextBlock,{column: 1,row: 1,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            //定义元件连接端口
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port1",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,1
                {row: 2,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port2",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"2")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,2
                {row: 2,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port3",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"3")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,3
                {row: 2,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port4",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"4")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,4
                {row: 2,column: 4},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port5",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"5")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,5
                {row: 2,column: 5},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port6",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"6")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 6},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port7",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"7")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port14",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"14")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,1
                {row: 0,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port13",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"13")
            ),gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port12",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"12")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port11",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"11")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 4},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port10",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"10")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 5},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port9",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"9")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 6},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port8",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"8")
            )
        )
    );

var node16portTemplate =
    gm(go.Node,"Auto",//自动调节
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        //形状矩形,填充颜色亮蓝
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null,}),
        //节点面板为表格样式
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,//第一行样式
                {row: 0,alignment: go.Spot.Top}),
            gm(go.RowColumnDefinition,//第三行样式
                {row: 2,alignment:go.Spot.Bottom}),
            //第二行为文本框,显示元件名字
            gm(go.TextBlock,{column: 1,row: 1,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            //定义元件连接端口
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port1",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,1
                {row: 2,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port2",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"2")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,2
                {row: 2,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port3",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"3")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,3
                {row: 2,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port4",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"4")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,4
                {row: 2,column: 4},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port5",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"5")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,5
                {row: 2,column: 5},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port6",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"6")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 6},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port7",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"7")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 7},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port8",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"8")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port16",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"16")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,1
                {row: 0,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port15",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"15")
            ),gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port14",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"14")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port13",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"13")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 4},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port12",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"12")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 5},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port11",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"11")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 6},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port10",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"10")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 7},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "port9",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"9")
            )
        )
    );

var node555Template =
    gm(go.Node,"Auto",//自动调节
        {
            mouseEnter: mouseEnter,
            mouseLeave: mouseLeave
        },
        //形状矩形,填充颜色亮蓝
        gm(go.Shape,"Rectangle",{fill:'#96D6D9',name: 'SHAPE',strokeWidth: 2, stroke: null}),
        //节点面板为表格样式
        gm(go.Panel,"Table",
            gm(go.RowColumnDefinition,//第一行样式
                {row: 0,alignment: go.Spot.Top}),
            gm(go.RowColumnDefinition,//第三行样式
                {row: 2,alignment:go.Spot.Bottom}),
            //第二行为文本框,显示元件名字
            gm(go.TextBlock,{column: 1,row: 1,columnSpan: 3,alignment: go.Spot.Center,
                    font: "bold 10pt sans-serif", margin: new go.Margin(4, 2),name: 'TEXT'},
                new go.Binding("text", "key")),
            //定义元件连接端口
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 2,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port1",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"1")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,1
                {row: 2,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port2",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"2")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,2
                {row: 2,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port3",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"3")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,3
                {row: 2,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port4",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"4")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,4
                {row: 0,column: 0},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port8",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"8")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,5
                {row: 0,column: 1},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port7",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"7")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 2},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port6",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"6")
            ),
            gm(go.Panel,"Horizontal",
                //位置在0,0
                {row: 0,column: 3},
                //可连可被连
                gm(go.Shape,
                    {width: 6, height: 6, portId: "555port5",
                        fromLinkable:true,
                        toLinkable:true,
                        fromLinkableSelfNode: true,
                        toLinkableSelfNode: true
                    }
                ),
                gm(go.TextBlock,"5")
            )
        )
    );

var templateMap = new go.Map();

templateMap.add("powerAnddp",powerAnddpTemplate);
templateMap.add("ground",groundTemplate);
templateMap.add('dzAnddr',dzAnddrTemplate);
templateMap.add("node14port",node14portTemplate);
templateMap.add("node16port",node16portTemplate);
templateMap.add("node555",node555Template);
templateMap.add("",diagram.nodeTemplate);
diagram.nodeTemplateMap = templateMap;

diagram.model =
    gm(go.GraphLinksModel,
        { linkFromPortIdProperty: "fromPort",  // required information:
            linkToPortIdProperty: "toPort",      // identifies data property names
            nodeDataArray: [
                { key: "电源,0", category: "powerAnddp"},
                { key: "高电平,0",category: "powerAnddp"},
                { key: "低电平,0",category: "powerAnddp"}
            ],
            linkDataArray: [
                // no predeclared links
            ]
        }
    );

// var newnode = {key: '74LS20,1',category: 'node14port'}
// diagram.model.addNodeData(newnode);

var lis = document.getElementsByTagName('li');
for(let i = 0;i < lis.length;i++){
    lis[i].onclick = function(){
        let nodes = diagram.model.nodeDataArray;
        var nodeName = lis[i].innerText;
        var nowNodeCategory = '';
        switch(nodeName){
            case '接地': nowNodeCategory = 'ground';break;
            case '电源':
            case '电平检测':
            case '高电平':
            case '低电平':
            case '示波器': nowNodeCategory = 'powerAnddp';break;
            case '电阻':
            case '电容': nowNodeCategory = 'dzAnddr';break;
            case '74LS20':
            case '74LS00':
            case '74LS08':
            case '74LS02':
            case '74LS86':
            case '74LS32':
            case '74LS54':
            case '74LS04':
            case '74LS74': nowNodeCategory = 'node14port';break;
            case '74LS138':
            case '74LS151':
            case '74LS153':
            case '74LS112':
            case '74LS194': nowNodeCategory = 'node16port';break;
            case '555': nowNodeCategory = 'node555';break;
        }
        var maxno = 0;
        for(let j = 0;j < nodes.length;j++){
            if(nodeName === nodes[j].key.split(',')[0]){
                maxno = Math.max(maxno,parseInt(nodes[j].key.split(',')[1]));
            }
        }
        nodeName += ',' + (maxno + 1);
        var newnode = {key: nodeName,category: nowNodeCategory}
        diagram.model.addNodeData(newnode);
    }
}

var btn = document.getElementById("databtn");
btn.onclick = function(){
    var graphData = diagram.model.linkDataArray;
    var graphNodes = diagram.model.nodeDataArray;
    var nodes = [];
    for(let i = 0;i < graphNodes.length;i++){
        if(!graphNodes[i].key.split(',')[0] in nodes){
            nodes.push(graphNodes[i].key.split(',')[0]);
        }
    }
    console.log(graphData);
    console.log(diagram.model.nodeDataArray);
    console.log(graphNodes);
    console.log(nodes);
    $.ajax({
            type: 'post',
            url: 'http://localhost:8080/dcesp/working/sumitCircuitdiagram',
            data: {'dlt' : JSON.stringify(graphData),'nodes' : JSON.stringify(graphNodes)},
            success: function(data){
                alert(data);
            }
        }
    )
}


