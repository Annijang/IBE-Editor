package com.github.franckyi.guapi.impl.node;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.databindings.api.event.ListChangeEvent;
import com.github.franckyi.databindings.impl.ObservableArrayList;
import com.github.franckyi.gamehooks.GameHooks;
import com.github.franckyi.guapi.GUAPI;
import com.github.franckyi.guapi.api.event.MouseEvent;
import com.github.franckyi.guapi.api.node.Group;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.util.ScreenEventType;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractGroup extends AbstractNode implements Group {
    protected final ObservableList<Node> children = new ChildrenList();

    protected boolean shouldUpdateChildren = true;

    protected AbstractGroup(Collection<? extends Node> children) {
        getChildren().addListener(this::onChildrenChange);
        paddingProperty().addListener(this::shouldComputeSizeAndChildrenPos);
        xProperty().addListener(this::shouldUpdateChildren);
        yProperty().addListener(this::shouldUpdateChildren);
        widthProperty().addListener(this::shouldComputeSizeAndChildrenPos);
        heightProperty().addListener(this::shouldComputeSizeAndChildrenPos);
        getChildren().addAll(children);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return children;
    }

    @Override
    public void tick() {
        super.tick();
        getChildren().forEach(Node::tick);
    }

    @Override
    public boolean checkRender() {
        boolean res = super.checkRender();
        if (shouldUpdateChildren) {
            shouldUpdateChildren = false;
            updateChildren();
            res = true;
        }
        return res;
    }

    @Override
    public void shouldUpdateChildren() {
        shouldUpdateChildren = true;
    }

    protected abstract void updateChildren();

    @Override
    public <E extends MouseEvent> void handleMouseEvent(ScreenEventType<E> type, E event) {
        if (inBounds(event.getMouseX(), event.getMouseY())) {
            for (Node node : getChildren()) {
                node.handleMouseEvent(type, event);
                if (event.getTarget() != null) break;
            }
            if (event.getTarget() == null) {
                event.setTarget(this);
                notifyEvent(type, event);
            }
        }
    }

    @Override
    public int getMaxChildrenWidth() {
        if (getMaxWidth() != INFINITE_SIZE)
            return getMaxWidth() - getPadding().getHorizontal();
        if (getPrefWidth() != COMPUTED_SIZE)
            return getPrefWidth() - getPadding().getHorizontal();
        if (!parentProperty().hasValue())
            return Group.super.getMaxChildrenWidth();
        return getParent().getMaxChildrenWidth() - getPadding().getHorizontal();
    }

    @Override
    public int getMaxChildrenHeight() {
        if (getMaxHeight() != INFINITE_SIZE)
            return getMaxHeight() - getPadding().getVertical();
        if (getPrefHeight() != COMPUTED_SIZE)
            return getPrefHeight() - getPadding().getVertical();
        if (!parentProperty().hasValue())
            return Group.super.getMaxChildrenHeight();
        return getParent().getMaxChildrenHeight() - getPadding().getVertical();
    }

    private void onChildrenChange(ListChangeEvent<? extends Node> event) {
        event.getRemoved(true).forEach(entry -> {
            if (entry.getValue().getParent() == AbstractGroup.this) {
                entry.getValue().setParent(null);
                entry.getValue().setParentPrefWidth(NONE);
                entry.getValue().setParentPrefHeight(NONE);
            }
        });
        event.getAdded(true).forEach(entry -> entry.getValue().setParent(this));
        shouldComputeSizeAndChildrenPos();
    }

    private void shouldComputeSizeAndChildrenPos() {
        this.shouldComputeSize();
        this.shouldUpdateChildren();
    }

    private static class ChildrenList extends ObservableArrayList<Node> {
        @Override
        protected boolean canAdd(Node element) {
            if (element.getParent() != null) {
                GameHooks.logger().error(GUAPI.MARKER, "Can't add Node \"" + element + "\" to Group: already present in Parent \"" + element.getParent() + "\"");
                return false;
            }
            return true;
        }

        @Override
        protected Collection<? extends Node> canAddAll(Collection<? extends Node> c) {
            return super.canAddAll(c).stream().distinct().filter(this::canAdd).collect(Collectors.toList());
        }
    }
}
