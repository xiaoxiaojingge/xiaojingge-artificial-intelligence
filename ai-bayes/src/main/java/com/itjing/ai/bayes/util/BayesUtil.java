package com.itjing.ai.bayes.util;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itjing.ai.bayes.constant.BayesNetworkMessageConstant;
import com.itjing.ai.bayes.constant.BayesNodeTypeConstant;
import com.itjing.ai.bayes.domain.*;
import com.itjing.ai.bayes.enums.BayesErrorModifierTypeEnum;
import com.itjing.ai.bayes.enums.BayesTipInfoTypeEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 贝叶斯工具类
 *
 * @author lijing
 * @date 2024-07-04
 */
public class BayesUtil {

	/**
	 * 开始对贝叶斯网络进行分析。 此方法初始化分析上下文，其中包含了网络的节点、链接、条件概率表等信息。
	 * 还构建了从节点索引到节点对象的映射，以及节点之间的父子关系映射，以便于后续的网络分析。
	 * @param network 贝叶斯网络，包含了待分析的节点和链接。
	 */
	public static void startAnalysis(BayesNetwork network) {
		// 创建分析上下文对象，并设置网络的节点、链接和条件概率表信息。
		AnalysisContext analysisContext = new AnalysisContext();
		analysisContext.setNodes(network.getNodes());
		analysisContext.setLinks(network.getLinks());
		analysisContext.setNodeCPTs(network.getNodeCPTs());

		// 构建节点索引到节点对象的映射，方便快速查找节点。
		Map<Integer, BayesNode> nodeMap = network.getNodes()
			.stream()
			.collect(Collectors.toMap(BayesNode::getIndex, node -> node));
		analysisContext.setNodeMap(nodeMap);

		// 构建从节点索引到节点CPT的映射
		Map<Integer, BayesNodeCPT> nodeCPTMap = network.getNodeCPTs()
			.stream()
			.collect(Collectors.toMap(BayesNodeCPT::getIndex, cpt -> cpt));
		analysisContext.setNodeCPTMap(nodeCPTMap);

		// 构建节点到其父节点集合的映射，用于后续的概率计算。
		Map<Integer, Set<Integer>> nodeParentMap = Maps.newHashMap();
		for (BayesLink link : network.getLinks()) {
			nodeParentMap.computeIfAbsent(link.getTo(), k -> new HashSet<>()).add(link.getFrom());
		}
		analysisContext.setNodeParentMap(nodeParentMap);

		// 构建节点到其子节点集合的映射，用于后续的概率计算。
		Map<Integer, Set<Integer>> nodeChildrenMap = Maps.newHashMap();
		for (BayesLink link : network.getLinks()) {
			nodeChildrenMap.computeIfAbsent(link.getFrom(), k -> new HashSet<>()).add(link.getTo());
		}
		analysisContext.setNodeChildrenMap(nodeChildrenMap);

		boolean flag = false;
		for (BayesNode node : network.getNodes()) {
			if (node.getType().equals(BayesNodeTypeConstant.NODE_OPPORTUNITY)) {

			}
			else if (node.getType().equals(BayesNodeTypeConstant.NODE_JUDGE)) {

			}
		}
	}

	/**
	 * 计算贝叶斯网络中的一个节点的结束值表。 结束值表用于存储节点的所有可能状态及其对应的概率值。
	 * @param currentNode 当前处理的节点对象，包含节点的索引和状态信息。
	 * @param analysisContext 分析上下文对象，包含整个网络的结构和计算所需的信息。
	 * @return 返回一个包含节点索引和结束值表的贝叶斯节点结束值表对象。
	 */
	private static BayesNodeEndValTable computeOneNetNode(BayesNode currentNode, AnalysisContext analysisContext) {
		// 初始化结果对象
		BayesNodeEndValTable result = new BayesNodeEndValTable();
		// 获取当前节点的条件概率表（CPT）
		BayesNodeCPT currentNodeCPT = analysisContext.getNodeCPTMap().get(currentNode.getIndex());
		// 如果是决策节点
		if (Objects.equals(currentNode.getType(), BayesNodeTypeConstant.NODE_JUDGE)) {
			// 对于决策节点，其结束值表中的每个状态值都设为相同的概率值
			List<Double> endValTable = Lists.newArrayList();
			// 计算每个状态的概率值，假设所有状态概率相等
			int stateCount = currentNode.getStateNameList().size();
			double rate = new BigDecimal("100").divide(new BigDecimal(stateCount), 2, RoundingMode.HALF_UP)
				.doubleValue();
			for (int i = 0; i < currentNode.getStateNameList().size(); i++) {
				endValTable.add(rate);
			}
			// 设置节点索引和结束值表到结果对象
			result.setNodeIndex(currentNode.getIndex());
			result.setEndValTable(endValTable);
		}
		else {
			// 对于普通节点，根据条件概率表的内容设置结束值表
			result.setNodeIndex(currentNode.getIndex());
			List<Double> endValTable = Lists.newArrayList();

			// 如果条件概率表不为空
			if (Objects.nonNull(currentNodeCPT)) {
				// 如果条件概率表中包含具体的条件概率
				if (Objects.nonNull(currentNodeCPT.getCpt())) {
					endValTable = getStateValListForNotTopNode(currentNodeCPT, analysisContext);
				}
				else {
					// 先验概率
					List<BayesNodeCPT.StateVal> stateVals = currentNodeCPT.getStateVals();
					if (!CollectionUtils.isEmpty(stateVals)) {
						for (int i = 0; i < stateVals.size(); i++) {
							endValTable.add(stateVals.get(i).getValue());
						}
					}
				}
			}
			result.setEndValTable(endValTable);
		}
		// 返回计算结果
		return result;
	}

	/**
	 * 从非顶部节点的条件概率表中获取状态值列表。 此方法通过遍历当前节点的条件概率表中的每个组合，计算每个状态值的累积概率。
	 * 它考虑了当前节点的所有父节点的状态，将父节点的状态概率与当前节点的状态值相乘， 最终得到当前节点在给定父节点状态下的累积概率。
	 * @param currentNodeCPT 当前节点的条件概率表。
	 * @param analysisContext 分析上下文，包含整个分析过程中的状态值表和其他必要信息。
	 * @return 当前节点的状态值列表，表示每个状态的累积概率。
	 */
	private static List<Double> getStateValListFromNotTopNode(BayesNodeCPT currentNodeCPT,
			AnalysisContext analysisContext) {
		// 初始化状态值列表
		List<Double> endValTable = Lists.newArrayList();
		// 获取当前节点条件概率表中的组合列表和父节点列表
		List<BayesNodeCPT.OneCombin> combinList = currentNodeCPT.getCpt().getCombinList();
		List<BayesNode> fatherNodes = currentNodeCPT.getCpt().getFatherNodes();

		// 遍历父节点列表，计算每个父节点的状态值，并添加到分析上下文中
		for (BayesNode fatherNode : fatherNodes) {
			BayesNodeEndValTable endVal = computeOneNetNode(fatherNode, analysisContext);
			analysisContext.getEndValTableList().add(endVal);
		}

		// 遍历组合列表，计算每个状态的累积概率
		for (BayesNodeCPT.OneCombin oneCombin : combinList) {
			// 初始化概率为1.00，即初始概率为1
			BigDecimal rate = new BigDecimal("1.00");
			// 获取当前组合的父节点状态列表
			int i = 0;
			List<BayesNodeCPT.StateVal> parentsStates = oneCombin.getParentsStates();
			// 遍历父节点状态列表，计算累积概率
			for (BayesNodeCPT.StateVal parentsState : parentsStates) {
				// 根据索引获取对应的父节点
				BayesNode fatherNode = fatherNodes.get(i++);
				List<String> fatherStateNameList = fatherNode.getStateNameList();
				// 状态名
				String name = parentsState.getName();
				BayesNodeEndValTable bayesNodeEndValTable = analysisContext.getEndValTableList()
					.stream()
					.filter(endVal -> Objects.equals(endVal.getNodeIndex(), fatherNode.getIndex()))
					.findFirst()
					.orElse(null);
				// 如果找到对应的状态值表
				if (Objects.nonNull(bayesNodeEndValTable)) {
					// 获取当前父节点状态的索引
					int order = fatherStateNameList.indexOf(name);
					// 如果索引存在，则将父节点的状态概率乘以当前累积概率
					if (order != -1) {
						rate = rate.multiply(BigDecimal.valueOf(bayesNodeEndValTable.getEndValTable().get(order)));
					}
				}
			}

			// 初始化当前节点的状态列表
			// 当前节点的状态参数和值
			int j = 0;
			List<BayesNodeCPT.StateVal> currNodeStates = oneCombin.getCurrNode();
			// 遍历当前节点的状态列表，计算每个状态的累积概率
			for (BayesNodeCPT.StateVal currNodeState : currNodeStates) {
				// 获取当前状态的值
				Double value = currNodeState.getValue();
				// 如果状态值列表中已存在当前状态，则更新其累积概率
				if (ListUtil.isElementPresent(endValTable, j)) {
					// 存在就累加，把ParentsState概率*当前的CurrNode的StateVa值累加进去
					BigDecimal calculateResult = BigDecimal.valueOf(endValTable.get(j))
						.add(rate.multiply(BigDecimal.valueOf(value)));
					endValTable.set(j, calculateResult.doubleValue());
				}
				else {
					// 如果状态值列表中不存在当前状态，则添加新的累积概率
					// 不存在就把ParentsState概率*当前的CurrNode的StateVa值
					BigDecimal calculateResult = rate.multiply(BigDecimal.valueOf(value));
					endValTable.add(calculateResult.doubleValue());
				}
				j++;
			}
		}
		// 将所有累积概率除以10000并保留两位小数，以归一化结果
		endValTable = endValTable.stream()
			.map(val -> BigDecimal.valueOf(val)
				.divide(BigDecimal.valueOf(10000))
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue())
			.collect(Collectors.toList());
		return endValTable;
	}

	/**
	 * 获取非顶级节点的状态值列表。 此方法用于分析贝叶斯网络中的非顶级节点，确定其状态值列表。通过检查当前节点的父节点之间是否存在连接，
	 * 来判断是否可以从非顶级节点获取状态值。如果一个父节点与其他父节点没有连接，则认为可以从这个父节点获取状态值。
	 * @param currentNodeCPT 当前节点的条件概率表。
	 * @param analysisContext 分析上下文，包含贝叶斯网络的结构和分析相关信息。
	 * @return
	 */
	private static List<Double> getStateValListForNotTopNode(BayesNodeCPT currentNodeCPT,
			AnalysisContext analysisContext) {
		List<Double> result = new ArrayList<>();
		if (Objects.nonNull(currentNodeCPT) && Objects.nonNull(currentNodeCPT.getCpt())) {
			// 获取当前节点的父节点列表
			List<BayesNode> fatherNodes = currentNodeCPT.getCpt().getFatherNodes();
			List<Integer> noLinkFatherNodes = new ArrayList<>();
			// 遍历父节点，检查它们之间是否存在连接
			for (BayesNode fatherNode : fatherNodes) {
				boolean flag = false;
				// 其他父节点列表，用于检查它们与当前父节点之间是否存在连接
				List<Integer> otherFatherNodeList = fatherNodes.stream()
					.map(BayesNode::getIndex)
					.filter(index -> !Objects.equals(index, fatherNode.getIndex()))
					.collect(Collectors.toList());
				// 检查其他父节点与当前父节点之间是否存在连接
				for (Integer otherFatherIndex : otherFatherNodeList) {
					if (ifHavePathsBetweenNodes(otherFatherIndex, fatherNode.getIndex(), analysisContext)) {
						flag = true;
						break;
					}
				}
				// 如果其他父节点与当前父节点之间没有连接，则将当前父节点加入到noLinkFatherNodes列表中
				if (!flag) {
					noLinkFatherNodes.add(fatherNode.getIndex());
				}
			}
			// 如果所有父节点之间都没有连接，意味着所有父节点都是独立的，没有额外的变量需要考虑。
			if (fatherNodes.size() == noLinkFatherNodes.size()) {
				result = getStateValListFromNotTopNode(currentNodeCPT, analysisContext);

			}
			else {
				// 如果存在父节点之间有连接的情况，表明存在某些父节点的状态之间或与其他未考虑变量有关联，即存在依赖关系，需要特别处理
				System.out.println("完犊子了，某些父节点的状态之间或与其他未考虑变量有关联，即存在依赖关系，需要特别处理");
			}
		}

		return result;
	}

	/**
	 * 两个节点之间是否有路径
	 * @param srcIndex
	 * @param targetIndex
	 * @param context
	 * @return boolean
	 */
	private static boolean ifHavePathsBetweenNodes(Integer srcIndex, Integer targetIndex, AnalysisContext context) {
		Map<Integer, Set<Integer>> nodeChildrenMap = context.getNodeChildrenMap();
		Set<Integer> children = nodeChildrenMap.get(srcIndex);
		if (!CollectionUtils.isEmpty(children)) {
			boolean contains = children.contains(targetIndex);
			if (contains) {
				return true;
			}
			for (Integer child : children) {
				if (ifHavePathsBetweenNodes(child, targetIndex, context)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 贝叶斯网络检查
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
			BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
					BayesNetworkMessageConstant.BAYES_NETWORK_NODE_EMPTY, BayesErrorModifierTypeEnum.MUST_MODIFY);
			result.add(tipInfo);
		}
		else {
			boolean flag = false;
			// 遍历贝叶斯网络节点
			for (BayesNode node : nodes) {
				// 查询目标节点是该节点的关系
				List<BayesLink> toNodeLinks = links.stream()
					.filter(link -> Objects.equals(link.getTo(), node.getIndex()))
					.collect(Collectors.toList());
				// 查询源节点是该节点的关系
				List<BayesLink> fromNodeLinks = links.stream()
					.filter(link -> Objects.equals(link.getFrom(), node.getIndex()))
					.collect(Collectors.toList());
				// 如果是机会节点
				if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_OPPORTUNITY)) {
					if ((toNodeLinks.size() + fromNodeLinks.size()) == 0) {
						BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
								StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_NODE_ISOLATED, node.getText()),
								BayesErrorModifierTypeEnum.MUST_MODIFY);
						result.add(tipInfo);
					}
					if (!flag) {
						// 检查贝叶斯网络是否有环
						if (!CollectionUtils.isEmpty(fromNodeLinks)) {
							flag = checkIfHaveLoopInNet(node, node, links);
							if (flag) {
								BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR, StrUtil
									.format(BayesNetworkMessageConstant.BAYES_NETWORK_HAVE_LOOP, node.getText()),
										BayesErrorModifierTypeEnum.MUST_MODIFY);
								result.add(tipInfo);
							}
						}
					}
					// 检查条件概率表是否填写完整
					BayesTipInfo checkCptTip = checkCPTData(network, node);
					if (Objects.nonNull(checkCptTip)) {
						result.add(checkCptTip);
					}
				}
				else if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_JUDGE)) {
					// 决策节点
					long count = links.stream().filter(link -> Objects.equals(link.getFrom(), node.getIndex())).count();
					if (count == 0) {
						BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
								StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_JUDGE_NODE_LACK_OUTPUT_NODE,
										node.getText()),
								BayesErrorModifierTypeEnum.MUST_MODIFY);
						result.add(tipInfo);
					}
				}
				else if (Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_UTILITY)) {
					BayesNodeCPT nodeCPT = nodeCPTs.stream()
						.filter(cpt -> Objects.equals(cpt.getIndex(), node.getIndex()))
						.findFirst()
						.orElse(null);
					// 效用节点
					if (Objects.isNull(nodeCPT)) {
						BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
								StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_UTILITY_NODE_LACK_CPT_DATA,
										node.getText()),
								BayesErrorModifierTypeEnum.MUST_MODIFY);
						result.add(tipInfo);
					}
					Map<Integer, List<BayesNode>> typeGroup = links.stream()
						.filter(link -> Objects.equals(link.getTo(), node.getIndex()))
						.map(link -> nodes.stream()
							.filter(node1 -> Objects.equals(node1.getIndex(), link.getFrom()))
							.findFirst()
							.orElse(null))
						.filter(Objects::nonNull)
						.collect(Collectors.groupingBy(BayesNode::getType));
					// 判断当前效用节点是否有决策节点
					if (CollectionUtils.isEmpty(typeGroup.get(BayesNodeTypeConstant.NODE_JUDGE))) {
						BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
								StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_UTILITY_NODE_LACK_JUDGE_NODE,
										node.getText()),
								BayesErrorModifierTypeEnum.MUST_MODIFY);
						result.add(tipInfo);
					}
				}
			}
			;
			// 决策节点的数量
			List<BayesNode> judgeNodeList = nodes.stream()
				.filter(node -> Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_JUDGE))
				.collect(Collectors.toList());
			int judgeNodeCount = judgeNodeList.size();
			if (judgeNodeCount > 1) {
				boolean sign = false;
				for (BayesNode judgeNode : judgeNodeList) {
					sign = false;
					// 查询从当前节点出去的关系
					List<BayesLink> judgeNodeOutLinks = links.stream()
						.filter(link -> Objects.equals(link.getFrom(), judgeNode.getIndex()))
						.collect(Collectors.toList());
					// 查询从当前节点进来的关系
					List<BayesLink> judgeNodeInLinks = links.stream()
						.filter(link -> Objects.equals(link.getTo(), judgeNode.getIndex()))
						.collect(Collectors.toList());
					long fromJudgeNodeCount = judgeNodeInLinks.stream().map(link -> {
						BayesNode fromNode = nodes.stream()
							.filter(node -> Objects.equals(node.getIndex(), link.getFrom())
									&& Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_JUDGE))
							.findFirst()
							.orElse(null);
						return fromNode;
					}).filter(Objects::nonNull).count();
					if (fromJudgeNodeCount > 0) {
						sign = true;
					}
					if (!sign) {
						long toJudgeNodeCount = judgeNodeOutLinks.stream().map(link -> {
							BayesNode toNode = nodes.stream()
								.filter(node -> Objects.equals(node.getIndex(), link.getTo())
										&& Objects.equals(node.getType(), BayesNodeTypeConstant.NODE_JUDGE))
								.findFirst()
								.orElse(null);
							return toNode;
						}).filter(Objects::nonNull).count();
						if (toJudgeNodeCount > 0) {
							sign = true;
						}
					}
					if (!sign) {
						BayesTipInfo tipInfo = getTipInfo(BayesTipInfoTypeEnum.ERROR,
								StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_LACK_LINK_BTW_JUDGE_NODES,
										judgeNode.getText()),
								BayesErrorModifierTypeEnum.MUST_MODIFY);
						result.add(tipInfo);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 检查贝叶斯网络中是否存在循环依赖。 通过深度优先搜索的方式，遍历从当前节点出发的链接，检查是否存在回到起始节点的路径。
	 * 这个方法用于确保贝叶斯网络的拓扑结构是有效的，不存在环路，因为环路可能导致计算上的困难。
	 * @param curNode 当前检查的节点。
	 * @param startNode 起始节点，用于判断是否回到起始点，形成环路。
	 * @param links 网络中的所有链接，用于遍历节点之间的关系。
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
	 * @param network
	 * @param checkNode
	 * @return {@link Integer }
	 */
	public static BayesTipInfo checkCPTData(BayesNetwork network, BayesNode checkNode) {
		BayesTipInfo tipInfo = null;
		List<BayesLink> links = network.getLinks();
		BayesNodeCPT nodeCPT = network.getNodeCPTs()
			.stream()
			.filter(cpt -> Objects.equals(cpt.getIndex(), checkNode.getIndex()))
			.findFirst()
			.orElse(null);
		// 条件概率表不为空
		if (Objects.nonNull(nodeCPT)) {
			if (Objects.nonNull(nodeCPT.getCpt())) {
				List<BayesNode> parentNodeList = links.stream()
					.filter(link -> Objects.equals(link.getTo(), checkNode.getIndex()))
					.map(link -> {
						BayesNode node = network.getNodes()
							.stream()
							.filter(n -> Objects.equals(n.getIndex(), link.getFrom()))
							.findFirst()
							.orElse(null);
						return node;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
				List<BayesNodeCPT.OneCombin> combinList = nodeCPT.getCpt().getCombinList();
				// 应该的组合数
				Integer shouldCombinCount = parentNodeList.stream()
					.map(node -> node.getStateNameList().size())
					.reduce(1, (a, b) -> a * b);
				if (combinList.size() < shouldCombinCount) {
					return getTipInfo(BayesTipInfoTypeEnum.ERROR, StrUtil
						.format(BayesNetworkMessageConstant.BAYES_NETWORK_CPT_DATA_NOT_COMPLETE, checkNode.getText()),
							BayesErrorModifierTypeEnum.MUST_MODIFY);
				}
				long count = combinList.stream().filter(combin -> combin.getCurrNode().isEmpty()).count();
				if (count > 0) {
					return getTipInfo(BayesTipInfoTypeEnum.ERROR, StrUtil
						.format(BayesNetworkMessageConstant.BAYES_NETWORK_CPT_DATA_NOT_COMPLETE, checkNode.getText()),
							BayesErrorModifierTypeEnum.MUST_MODIFY);
				}
			}
			else {
				// 如果条件概率表这个字段为空，则判断是否有概率数据
				List<BayesNodeCPT.StateVal> stateVals = nodeCPT.getStateVals();
				if (CollectionUtils.isEmpty(stateVals)) {
					return getTipInfo(
							BayesTipInfoTypeEnum.ERROR, StrUtil
								.format(BayesNetworkMessageConstant.BAYES_NETWORK_LACK_CPT_DATA, checkNode.getText()),
							BayesErrorModifierTypeEnum.MUST_MODIFY);
				}
			}
		}
		else {
			return getTipInfo(BayesTipInfoTypeEnum.ERROR,
					StrUtil.format(BayesNetworkMessageConstant.BAYES_NETWORK_LACK_CPT_DATA, checkNode.getText()),
					BayesErrorModifierTypeEnum.MUST_MODIFY);
		}
		return tipInfo;
	}

	/**
	 * 获取提示信息
	 * @param type
	 * @param message
	 * @param modifierType
	 * @return {@link BayesTipInfo }
	 */
	private static BayesTipInfo getTipInfo(BayesTipInfoTypeEnum type, String message,
			BayesErrorModifierTypeEnum modifierType) {
		BayesTipInfo tipInfo = new BayesTipInfo();
		tipInfo.setType(type);
		tipInfo.setMessage(message);
		tipInfo.setModifierType(modifierType);
		return tipInfo;
	}

	public static void main(String[] args) {
		// 贝叶斯网络示例
		BayesNode A = new BayesNode(1, false, 1, "暴雨", List.of("蓝", "橙", "红"));
		BayesNodeCPT aNodeCPT = new BayesNodeCPT();
		aNodeCPT.setIndex(1);
		aNodeCPT.setStateVals(List.of(new BayesNodeCPT.StateVal("蓝", 40.00), new BayesNodeCPT.StateVal("橙", 35.00),
				new BayesNodeCPT.StateVal("红", 25.00)));

		BayesNode B = new BayesNode(2, false, 1, "冲击力大小", List.of("大", "小"));
		BayesNodeCPT bNodeCPT = new BayesNodeCPT();
		bNodeCPT.setIndex(2);
		bNodeCPT.setStateVals(List.of(new BayesNodeCPT.StateVal("大", 30.00), new BayesNodeCPT.StateVal("小", 70.00)));

		BayesNode C = new BayesNode(3, false, 1, "泥石流", List.of("是", "否"));
		BayesNodeCPT cNodeCPT = new BayesNodeCPT();
		cNodeCPT.setIndex(3);
		BayesNodeCPT.CPT cCPT = new BayesNodeCPT.CPT();
		cCPT.setFatherNodes(List.of(A, B));
		cCPT.setCombinList(List.of(
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("蓝", null), new BayesNodeCPT.StateVal("大", null)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("蓝", null), new BayesNodeCPT.StateVal("小", null)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("橙", null), new BayesNodeCPT.StateVal("大", null)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("橙", null), new BayesNodeCPT.StateVal("小", null)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("红", null), new BayesNodeCPT.StateVal("大", null)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("红", null), new BayesNodeCPT.StateVal("小", null)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00)))));
		cNodeCPT.setCpt(cCPT);

		BayesNode D = new BayesNode(4, false, 1, "泥石流2", List.of("是", "否"));
		BayesNodeCPT dNodeCPT = new BayesNodeCPT();
		BayesNodeCPT.CPT dCPT = new BayesNodeCPT.CPT();
		dCPT.setFatherNodes(List.of(B, C));
		dCPT.setCombinList(List.of(
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("蓝", 40.00), new BayesNodeCPT.StateVal("大", 30.00)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("蓝", 40.00), new BayesNodeCPT.StateVal("小", 70.00)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("橙", 35.00), new BayesNodeCPT.StateVal("大", 30.00)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("橙", 35.00), new BayesNodeCPT.StateVal("小", 70.00)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("红", 25.00), new BayesNodeCPT.StateVal("大", 30.00)),
						List.of(new BayesNodeCPT.StateVal("是", 80.00), new BayesNodeCPT.StateVal("否", 20.00))),
				new BayesNodeCPT.OneCombin(
						List.of(new BayesNodeCPT.StateVal("红", 25.00), new BayesNodeCPT.StateVal("小", 70.00)),
						List.of(new BayesNodeCPT.StateVal("是", 60.00), new BayesNodeCPT.StateVal("否", 40.00)))));
		dNodeCPT.setCpt(dCPT);

		BayesLink link1 = new BayesLink(1, 3);
		BayesLink link2 = new BayesLink(2, 3);
		BayesLink link3 = new BayesLink(3, 4);
		// BayesLink link4 = new BayesLink(2, 4);

		BayesNetwork network = new BayesNetwork(List.of(A, B, C), List.of(link1, link2, link3),
				List.of(aNodeCPT, bNodeCPT, cNodeCPT));

		AnalysisContext analysisContext = new AnalysisContext();
		analysisContext.setNodes(network.getNodes());
		analysisContext.setLinks(network.getLinks());
		analysisContext.setNodeCPTs(network.getNodeCPTs());
		analysisContext
			.setNodeMap(network.getNodes().stream().collect(Collectors.toMap(BayesNode::getIndex, node -> node)));
		analysisContext.setNodeCPTMap(
				network.getNodeCPTs().stream().collect(Collectors.toMap(BayesNodeCPT::getIndex, nodeCPT -> nodeCPT)));
		analysisContext.setNodeChildrenMap(network.getLinks()
			.stream()
			.collect(Collectors.groupingBy(BayesLink::getFrom,
					Collectors.mapping(BayesLink::getTo, Collectors.toSet()))));
		analysisContext.setNodeParentMap(network.getLinks()
			.stream()
			.collect(Collectors.groupingBy(BayesLink::getTo,
					Collectors.mapping(BayesLink::getFrom, Collectors.toSet()))));

		System.out.println(computeOneNetNode(A, analysisContext));
		System.out.println(computeOneNetNode(B, analysisContext));
		System.out.println(computeOneNetNode(C, analysisContext));
		// System.out.println(ifHavePathsBetweenNodes(1, 4, analysisContext));
		// System.out.println(JSONObject.toJSONString(checkBayesNetwork(network)));

	}

}
