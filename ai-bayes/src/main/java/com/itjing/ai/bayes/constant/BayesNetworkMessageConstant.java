package com.itjing.ai.bayes.constant;

/**
 * 贝叶斯网络信息常量
 *
 * @author lijing
 * @date 2024-07-04
 */
public interface BayesNetworkMessageConstant {

	String BAYES_NETWORK_NODE_EMPTY = "结构错误：贝叶斯网络节点个数为0，不允许空网络！";

	String BAYES_NETWORK_NODE_ISOLATED = "结构错误：贝叶斯网络综不能出现被孤立（隔离）的节点！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_HAVE_LOOP = "结构错误：贝叶斯网络中不能出现环路！";

	String BAYES_NETWORK_LACK_CPT_DATA = "数据错误：不允许存在缺少数据的网络节点！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_CPT_DATA_NOT_COMPLETE = "数据错误：条件概率组合数据不全！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_JUDGE_NODE_LACK_OUTPUT_NODE = "结构错误：决策节点缺少相关效用节点！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_UTILITY_NODE_LACK_CPT_DATA = "数据错误：效用节点缺少效用数据！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_UTILITY_NODE_LACK_JUDGE_NODE = "结构错误：效用节点缺少对应的决策节点！ ----> 相关节点：《{}》";

	String BAYES_NETWORK_LACK_LINK_BTW_JUDGE_NODES = "结构错误：决策节点之间缺少确定决策顺序的连接线！ ----> 相关节点：《{}》";

}
