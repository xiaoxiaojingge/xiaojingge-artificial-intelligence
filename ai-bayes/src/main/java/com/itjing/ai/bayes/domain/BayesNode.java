package com.itjing.ai.bayes.domain;

/**
 * 贝叶斯-节点
 *
 * @author lijing
 * @date 2024-07-03
 */
public class BayesNode {

    /**
     * 节点标识
     */
    private Integer index;

    /**
     * 节点类型
     */
    private Integer type;

    /**
     * 节点文本
     */
    private String text;

    /**
     * 父级节点
     */
    private Integer parent;

    public BayesNode() {
    }

    public BayesNode(Integer index, Integer type, String text, Integer parent) {
        this.index = index;
        this.type = type;
        this.text = text;
        this.parent = parent;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "BayesNode{" +
                "index=" + index +
                ", type=" + type +
                ", text='" + text + '\'' +
                ", parent=" + parent +
                '}';
    }
}
