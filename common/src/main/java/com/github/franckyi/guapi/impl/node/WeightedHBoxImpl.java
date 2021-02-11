package com.github.franckyi.guapi.impl.node;

import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.builder.WeightedHBoxBuilder;
import com.github.franckyi.guapi.util.NodeType;

import java.util.Collection;

public class WeightedHBoxImpl extends AbstractWeightedHBox implements WeightedHBoxBuilder {
    public WeightedHBoxImpl() {
    }

    public WeightedHBoxImpl(int spacing) {
        super(spacing);
    }

    public WeightedHBoxImpl(Node... children) {
        super(children);
    }

    public WeightedHBoxImpl(Collection<? extends Node> children) {
        super(children);
    }

    public WeightedHBoxImpl(int spacing, Node... children) {
        super(spacing, children);
    }

    public WeightedHBoxImpl(int spacing, Collection<? extends Node> children) {
        super(spacing, children);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected NodeType<?> getType() {
        return NodeType.WEIGHTED_HBOX;
    }

    @Override
    public String toString() {
        return "WeightedHBox" + getChildren();
    }
}
