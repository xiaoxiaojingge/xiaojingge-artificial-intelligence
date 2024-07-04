package com.itjing.ai.bayes.util;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.itjing.ai.bayes.constant.BayesNetworkMessageConstant;
import com.itjing.ai.bayes.constant.BayesNodeTypeConstant;
import com.itjing.ai.bayes.constant.TipInfoTypeConstant;
import com.itjing.ai.bayes.domain.*;
import com.itjing.ai.bayes.enums.BayesErrorModifierTypeEnum;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 贝叶斯工具类
 *
 * @author lijing
 * @date 2024-07-04
 */
public class BayesUtil {

    /**
     * 贝叶斯网络检查
     *
     * @param network
     * @return {@link List }<{@link BayesTipInfo }>
     */
    public static List<BayesTipInfo> checkBayesNetwork(BayesNetwork network) {
        List<BayesTipInfo> result = Lists.newArrayList();
        List<BayesNode> nodes = network.getNodes();
        List<BayesLink> links = network.getLinks();
        List<BayesNodeCPT> nodeCPTs = network.getNodeCPTs();
        // 判断贝叶斯网络中节点是否为空
        if (CollectionUtils.isEmpty(nodes)) {
            BayesTipInfo tipInfo = new BayesTipInfo();
            tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
            tipInfo.setMessage(BayesNetworkMessageConstant.BAYES_NETWORK_NODE_EMPTY);
            tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
            result.add(tipInfo);
        } else {
            final Boolean[] flag = {false};
            // 遍历贝叶斯网络节点
            nodes.forEach(node -> {
                // 查询目标节点是该节点的关系
                List<BayesLink> toNodeLinks = links.stream().filter(link -> Objects.equals(link.getTo(), node.getIndex())).collect(Collectors.toList());
                // 查询源节点是该节点的关系
                List<BayesLink> fromNodeLinks = links.stream().filter(link -> Objects.equals(link.getFrom(), node.getIndex())).collect(Collectors.toList());
                // 如果是机会节点
                if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_OPPORTUNITY)) {
                    if ((toNodeLinks.size() + fromNodeLinks.size()) == 0) {
                        BayesTipInfo tipInfo = new BayesTipInfo();
                        tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                        tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_NODE_ISOLATED, node.getText()));
                        tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                        result.add(tipInfo);
                    }
                    if (!flag[0]) {
                        // 检查贝叶斯网络是否有环
                        if (!CollectionUtils.isEmpty(fromNodeLinks)) {
                            flag[0] = checkIfHaveLoopInNet(node, node, links);
                            if (flag[0]) {
                                BayesTipInfo tipInfo = new BayesTipInfo();
                                tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                                tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_HAVE_LOOP, node.getText()));
                                tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                                result.add(tipInfo);
                            }
                        }
                        // 检查条件概率表是否填写完整
                        BayesTipInfo checkCptTip = checkCPTData(network, node);
                        if (Objects.nonNull(checkCptTip)) {
                            result.add(checkCptTip);
                        }
                    }
                } else if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_DECISION_MAKING)) {
                    // 决策节点
                    long count = links.stream().filter(link -> Objects.equals(link.getFrom(), node.getIndex())).count();
                    if (count == 0) {
                        BayesTipInfo tipInfo = new BayesTipInfo();
                        tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                        tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                        tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_JUDGE_NODE_LACK_OUTPUT_NODE, node.getText()));
                        result.add(tipInfo);
                    }
                } else if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_UTILITY)) {
                    BayesNodeCPT nodeCPT = nodeCPTs.stream()
                            .filter(cpt -> Objects.equals(cpt.getIndex(), node.getIndex()))
                            .findFirst().orElse(null);
                    // 效用节点
                    if (Objects.isNull(nodeCPT)) {
                        BayesTipInfo tipInfo = new BayesTipInfo();
                        tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                        tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                        tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_UTILITY_NODE_LACK_CPT_DATA, node.getText()));
                        result.add(tipInfo);
                    }
                    Map<Integer, List<BayesNode>> typeGroup = links.stream()
                            .filter(link -> Objects.equals(link.getTo(), node.getIndex()))
                            .map(link -> nodes.stream().filter(node1 -> Objects.equals(node1.getIndex(), link.getFrom())).findFirst().orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.groupingBy(BayesNode::getType));
                    if (CollectionUtils.isEmpty(typeGroup.get(BayesNodeTypeConstant.NODE_DECISION_MAKING))) {
                        BayesTipInfo tipInfo = new BayesTipInfo();
                        tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                        tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                        tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_UTILTIY_NODE_LACK_JUDGE_NODE, node.getText()));
                    }


                }
            });
        }
        return result;
    }

    /**
     * 检查贝叶斯网络中是否存在循环依赖。
     * 通过深度优先搜索的方式，遍历从当前节点出发的链接，检查是否存在回到起始节点的路径。
     * 这个方法用于确保贝叶斯网络的拓扑结构是有效的，不存在环路，因为环路可能导致计算上的困难。
     *
     * @param curNode   当前检查的节点。
     * @param startNode 起始节点，用于判断是否回到起始点，形成环路。
     * @param links     网络中的所有链接，用于遍历节点之间的关系。
     * @return boolean 如果存在环路返回true，否则返回false。
     */
    public static boolean checkIfHaveLoopInNet(BayesNode curNode, BayesNode startNode, List<BayesLink> links) {
        // 初始化标志变量，用于标记是否发现环路。
        boolean flag = false;

        // 筛选出当前节点的所有出链接。
        List<BayesLink> outLinks = links.stream()
                .filter(link -> Objects.equals(link.getFrom(), curNode.getIndex()))
                .collect(Collectors.toList());

        // 遍历所有出链接，检查是否存在环路。
        for (BayesLink link : outLinks) {
            // 设置下一个检查的节点。
            BayesNode to = new BayesNode();
            to.setIndex(link.getTo());

            // 如果下一个节点是起始节点，说明发现环路。
            if (Objects.equals(to.getIndex(), startNode.getIndex())) {
                flag = true;
                break;
            }

            // 递归检查下一个节点是否存在环路。
            flag = checkIfHaveLoopInNet(to, startNode, links);

            // 如果已经发现环路，无需继续检查。
            if (flag) {
                break;
            }
        }

        // 返回是否存在环路的标志。
        return flag;
    }

    /**
     * 检查条件概率表是否填写完整
     *
     * @param network
     * @param checkNode
     * @return {@link Integer }
     */
    public static BayesTipInfo checkCPTData(BayesNetwork network, BayesNode checkNode) {
        BayesTipInfo tipInfo = null;
        List<BayesLink> links = network.getLinks();
        BayesNodeCPT nodeCPT = network.getNodeCPTs().stream()
                .filter(cpt -> Objects.equals(cpt.getIndex(), checkNode.getIndex()))
                .findFirst().orElse(null);
        // 条件概率表不为空
        if (Objects.nonNull(nodeCPT) && Objects.nonNull(nodeCPT.getCpt())) {
            List<BayesNode> parentNodeList = links.stream()
                    .filter(link -> Objects.equals(link.getTo(), checkNode.getIndex()))
                    .map(link -> {
                        BayesNode node = new BayesNode();
                        node.setIndex(link.getFrom());
                        return node;
                    }).collect(Collectors.toList());
            List<BayesNodeCPT.OneCombin> combinList = nodeCPT.getCpt().getCombinList();
            if (combinList.size() < parentNodeList.size()) {
                tipInfo = new BayesTipInfo();
                tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_CPT_DATA_NOT_COMPLETE, checkNode.getText()));
                return tipInfo;
            }
            long count = combinList.stream().filter(combin -> combin.getCurrNode().isEmpty()).count();
            if (count > 0) {
                tipInfo = new BayesTipInfo();
                tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
                tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
                tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_CPT_DATA_NOT_COMPLETE, checkNode.getText()));
                return tipInfo;
            }
        } else {
            tipInfo = new BayesTipInfo();
            tipInfo.setType(TipInfoTypeConstant.TIP_INFO_TYPE_ERROR);
            tipInfo.setMessage(StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_LACK_CPT_DATA, checkNode.getText()));
            tipInfo.setModifierType(BayesErrorModifierTypeEnum.MUST_MODIFY);
        }
        return tipInfo;
    }

    public static void main(String[] args) {
        BayesNetwork network = new BayesNetwork();
        List<BayesNode> nodes = List.of(
                new BayesNode(1, 1, "A", 3),
                new BayesNode(2, 3, "B", 1)
                // new BayesNode(3, 1, "C", 2)
        );
        List<BayesLink> links = List.of(
                new BayesLink(1, 2)
                // new BayesLink(2, 3),
                // new BayesLink(3, 1)
        );
        List<BayesNodeCPT> nodeCPTs = Lists.newArrayList();
        network.setNodes(nodes);
        network.setLinks(links);
        network.setNodeCPTs(nodeCPTs);
        System.out.println(checkBayesNetwork(network));
    }
}
