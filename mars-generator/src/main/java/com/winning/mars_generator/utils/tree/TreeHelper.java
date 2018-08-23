package com.winning.mars_generator.utils.tree;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class TreeHelper {
    private static List<Node> tree = new ArrayList<>();
    public static void addNode(Activity parent, Activity sub){
        if (null != parent){
           if (tree.size() > 0){
               addSubNode(parent, sub, tree.get(0));
           }
        }else {
            addRoot(sub);
        }
    }

    private static void addSubNode(Activity parent, Activity sub, Node node) {
        if (null != node){
            if (parent.getClass().getSimpleName().equalsIgnoreCase(node.getName())){
                Node subNode = new Node();
                subNode.setName(sub.getClass().getSimpleName());
                node.getChildren().add(subNode);
                return;
            }

            if (null == node.getChildren()){
                return;
            }

            for (int i = 0; i < node.getChildren().size();i ++){
                addSubNode(parent, sub, node.getChildren().get(i));
            }
        }
    }

    private static void addRoot(Activity root){
        if (tree.size() <= 0){
            Node node = new Node();
            node.setName(root.getClass().getSimpleName());
            tree.add(node);
        }
    }

    public static List<Node> getTree() {
        return tree;
    }
}
