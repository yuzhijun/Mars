package com.winning.mars_generator.core.modules.inflate;

import com.winning.mars_generator.core.BaseBean;
import com.winning.mars_generator.utils.tree.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserBehaviorBean  extends BaseBean {
    private List<Node> tree = new ArrayList<>();

    public UserBehaviorBean(String appKey,String modelIMEI,Collection<Node> cTree){
        tree.clear();
        tree.addAll(cTree);
        this.appKey = appKey;
        this.modelIMEI = modelIMEI;
    }

    public UserBehaviorBean(List<Node> tree) {
        this.tree = tree;
    }

    public List<Node> getTree() {
        return tree;
    }

    public void setTree(List<Node> tree) {
        this.tree = tree;
    }
}
