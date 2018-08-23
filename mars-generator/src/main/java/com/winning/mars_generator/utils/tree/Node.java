package com.winning.mars_generator.utils.tree;

import java.util.ArrayList;
import java.util.List;
/**
 * tree node
 * */
public class Node {
    private String name;
    private boolean expandAndCollapse = true;
    private List<Node> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public boolean isExpandAndCollapse() {
        return expandAndCollapse;
    }

    public void setExpandAndCollapse(boolean expandAndCollapse) {
        this.expandAndCollapse = expandAndCollapse;
    }
}
