package com.github.franckyi.guapi.impl.node;

import com.github.franckyi.databindings.PropertyFactory;
import com.github.franckyi.databindings.api.*;
import com.github.franckyi.guapi.GUAPI;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.event.ScreenEventHandler;
import com.github.franckyi.guapi.api.event.ScreenEventListener;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Parent;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.impl.event.ScreenEventHandlerDelegate;
import com.github.franckyi.guapi.util.Insets;
import com.github.franckyi.guapi.util.NodeType;
import com.github.franckyi.guapi.util.ScreenEventType;

public abstract class AbstractNode implements Node {
    protected final IntegerProperty xProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue xPropertyReadOnly = PropertyFactory.readOnly(xProperty);
    protected final IntegerProperty yProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue yPropertyReadOnly = PropertyFactory.readOnly(yProperty);
    protected final IntegerProperty widthProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue widthPropertyReadOnly = PropertyFactory.readOnly(widthProperty);
    protected final IntegerProperty heightProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue heightPropertyReadOnly = PropertyFactory.readOnly(heightProperty);

    private final IntegerProperty minWidthProperty = PropertyFactory.ofInteger();
    private final IntegerProperty minHeightProperty = PropertyFactory.ofInteger();
    private final IntegerProperty prefWidthProperty = PropertyFactory.ofInteger(COMPUTED_SIZE);
    private final IntegerProperty prefHeightProperty = PropertyFactory.ofInteger(COMPUTED_SIZE);
    private final IntegerProperty maxWidthProperty = PropertyFactory.ofInteger(INFINITE_SIZE);
    private final IntegerProperty maxHeightProperty = PropertyFactory.ofInteger(INFINITE_SIZE);
    private final IntegerProperty parentPrefWidthProperty = PropertyFactory.ofInteger(NONE);
    private final IntegerProperty parentPrefHeightProperty = PropertyFactory.ofInteger(NONE);

    protected final IntegerProperty computedWidthProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue computedWidthPropertyReadOnly = PropertyFactory.readOnly(computedWidthProperty);
    protected final IntegerProperty computedHeightProperty = PropertyFactory.ofInteger();
    private final ObservableIntegerValue computedHeightPropertyReadOnly = PropertyFactory.readOnly(computedHeightProperty);

    private final IntegerProperty backgroundColorProperty = PropertyFactory.ofInteger(DEFAULT_BACKGROUND_COLOR);
    private final ObjectProperty<Insets> paddingProperty = PropertyFactory.ofObject(Insets.NONE);

    protected final ObjectProperty<Parent> parentProperty = PropertyFactory.ofObject();
    private final ObservableObjectValue<Parent> parentPropertyReadOnly = PropertyFactory.readOnly(parentProperty);
    protected final ObjectProperty<Scene> sceneProperty = PropertyFactory.ofObject();
    private final ObservableObjectValue<Scene> scenePropertyReadOnly = PropertyFactory.readOnly(sceneProperty);

    private final BooleanProperty disableProperty = PropertyFactory.ofBoolean();
    private final ObservableBooleanValue disabledProperty = disableProperty()
            .or(parentProperty().bindMapToBoolean(Parent::disabledProperty, false));

    private final ObservableBooleanValue rootProperty = sceneProperty()
            .bindMap(Scene::rootProperty, null)
            .mapToBoolean(node -> node == AbstractNode.this);
    private final ObservableBooleanValue focusedProperty = sceneProperty()
            .bindMap(Scene::focusedProperty, null)
            .mapToBoolean(node -> node == AbstractNode.this);
    private final ObservableBooleanValue hoveredProperty = sceneProperty()
            .bindMap(Scene::hoveredProperty, null)
            .mapToBoolean(node -> node == AbstractNode.this);

    private final IntegerProperty renderPriorityProperty = PropertyFactory.ofInteger();

    protected Skin<? super Node> skin;
    protected final ScreenEventHandler eventHandlerDelegate = new ScreenEventHandlerDelegate();
    protected boolean shouldComputeSize = true;
    protected boolean shouldUpdateSize = true;

    protected AbstractNode() {
        minWidthProperty().addListener(this::shouldUpdateSize);
        minHeightProperty().addListener(this::shouldUpdateSize);
        prefWidthProperty().addListener(this::shouldUpdateSize);
        prefHeightProperty().addListener(this::shouldUpdateSize);
        maxWidthProperty().addListener(this::shouldUpdateSize);
        maxHeightProperty().addListener(this::shouldUpdateSize);
        parentPrefWidthProperty().addListener(this::shouldUpdateSize);
        parentPrefHeightProperty().addListener(this::shouldUpdateSize);
        computedWidthProperty().addListener(this::shouldUpdateSize);
        computedHeightProperty().addListener(this::shouldUpdateSize);
        widthProperty().addListener(this::updateParentWidth);
        heightProperty().addListener(this::updateParentHeight);
        paddingProperty().addListener(this::shouldUpdateSize);
        parentProperty().addListener(this::updateScene);
    }

    @Override
    public int getX() {
        return xProperty().getValue();
    }

    @Override
    public ObservableIntegerValue xProperty() {
        return xPropertyReadOnly;
    }

    @Override
    public void setX(int value) {
        xProperty.setValue(value);
    }

    @Override
    public int getY() {
        return yProperty().getValue();
    }

    @Override
    public ObservableIntegerValue yProperty() {
        return yPropertyReadOnly;
    }

    @Override
    public void setY(int value) {
        yProperty.setValue(value);
    }

    @Override
    public int getWidth() {
        return widthProperty().getValue();
    }

    @Override
    public ObservableIntegerValue widthProperty() {
        return widthPropertyReadOnly;
    }

    @Override
    public void setWidth(int value) {
        widthProperty.setValue(value);
    }

    @Override
    public int getHeight() {
        return heightProperty().getValue();
    }

    @Override
    public ObservableIntegerValue heightProperty() {
        return heightPropertyReadOnly;
    }

    @Override
    public void setHeight(int value) {
        heightProperty.setValue(value);
    }

    @Override
    public int getMinWidth() {
        return minWidthProperty().getValue();
    }

    @Override
    public IntegerProperty minWidthProperty() {
        return minWidthProperty;
    }

    @Override
    public void setMinWidth(int value) {
        minWidthProperty().setValue(value);
    }

    @Override
    public int getMinHeight() {
        return minHeightProperty().getValue();
    }

    @Override
    public IntegerProperty minHeightProperty() {
        return minHeightProperty;
    }

    @Override
    public void setMinHeight(int value) {
        minHeightProperty().setValue(value);
    }

    @Override
    public int getPrefWidth() {
        return prefWidthProperty().getValue();
    }

    @Override
    public IntegerProperty prefWidthProperty() {
        return prefWidthProperty;
    }

    @Override
    public void setPrefWidth(int value) {
        prefWidthProperty().setValue(value);
    }

    @Override
    public int getPrefHeight() {
        return prefHeightProperty().getValue();
    }

    @Override
    public IntegerProperty prefHeightProperty() {
        return prefHeightProperty;
    }

    @Override
    public void setPrefHeight(int value) {
        prefHeightProperty().setValue(value);
    }

    @Override
    public int getMaxWidth() {
        return maxWidthProperty().getValue();
    }

    @Override
    public IntegerProperty maxWidthProperty() {
        return maxWidthProperty;
    }

    @Override
    public void setMaxWidth(int value) {
        maxWidthProperty().setValue(value);
    }

    @Override
    public int getMaxHeight() {
        return maxHeightProperty().getValue();
    }

    @Override
    public IntegerProperty maxHeightProperty() {
        return maxHeightProperty;
    }

    @Override
    public void setMaxHeight(int value) {
        maxHeightProperty().setValue(value);
    }

    @Override
    public int getParentPrefWidth() {
        return parentPrefWidthProperty().getValue();
    }

    @Override
    public IntegerProperty parentPrefWidthProperty() {
        return parentPrefWidthProperty;
    }

    @Override
    public void setParentPrefWidth(int value) {
        parentPrefWidthProperty().setValue(value);
    }

    @Override
    public int getParentPrefHeight() {
        return parentPrefHeightProperty().getValue();
    }

    @Override
    public IntegerProperty parentPrefHeightProperty() {
        return parentPrefHeightProperty;
    }

    @Override
    public void setParentPrefHeight(int value) {
        parentPrefHeightProperty().setValue(value);
    }

    @Override
    public int getComputedWidth() {
        return computedWidthProperty().getValue();
    }

    @Override
    public ObservableIntegerValue computedWidthProperty() {
        return computedWidthPropertyReadOnly;
    }

    protected void setComputedWidth(int value) {
        computedWidthProperty.setValue(value);
    }

    @Override
    public int getComputedHeight() {
        return computedHeightProperty().getValue();
    }

    @Override
    public ObservableIntegerValue computedHeightProperty() {
        return computedHeightPropertyReadOnly;
    }

    protected void setComputedHeight(int value) {
        computedHeightProperty.setValue(value);
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColorProperty().getValue();
    }

    @Override
    public IntegerProperty backgroundColorProperty() {
        return backgroundColorProperty;
    }

    @Override
    public void setBackgroundColor(int value) {
        backgroundColorProperty().setValue(value);
    }

    @Override
    public Insets getPadding() {
        return paddingProperty().getValue();
    }

    @Override
    public ObjectProperty<Insets> paddingProperty() {
        return paddingProperty;
    }

    @Override
    public void setPadding(Insets value) {
        paddingProperty().setValue(value);
    }

    @Override
    public Parent getParent() {
        return parentProperty().getValue();
    }

    @Override
    public ObservableObjectValue<Parent> parentProperty() {
        return parentPropertyReadOnly;
    }

    @Override
    public void setParent(Parent value) {
        parentProperty.setValue(value);
    }

    @Override
    public Scene getScene() {
        return sceneProperty().getValue();
    }

    @Override
    public ObservableObjectValue<Scene> sceneProperty() {
        return scenePropertyReadOnly;
    }

    @Override
    public boolean isDisable() {
        return disableProperty().getValue();
    }

    @Override
    public BooleanProperty disableProperty() {
        return disableProperty;
    }

    @Override
    public void setDisable(boolean value) {
        disableProperty().setValue(value);
    }

    @Override
    public boolean isDisabled() {
        return disabledProperty().getValue();
    }

    @Override
    public ObservableBooleanValue disabledProperty() {
        return disabledProperty;
    }

    @Override
    public boolean isRoot() {
        return rootProperty().getValue();
    }

    @Override
    public ObservableBooleanValue rootProperty() {
        return rootProperty;
    }

    @Override
    public boolean isFocused() {
        return focusedProperty().getValue();
    }

    @Override
    public ObservableBooleanValue focusedProperty() {
        return focusedProperty;
    }

    @Override
    public boolean isHovered() {
        return hoveredProperty().getValue();
    }

    @Override
    public ObservableBooleanValue hoveredProperty() {
        return hoveredProperty;
    }

    protected <N extends Node> Skin<? super N> getSkin() {
        if (skin == null) {
            skin = GUAPI.getTheme().provideSkin(this, getType());
        }
        return skin;
    }

    protected abstract <N extends Node> NodeType<N> getType();

    protected void resetSkin() {
        skin = null;
    }

    @Override
    public <E extends ScreenEvent> void handleEvent(ScreenEventType<E> type, E event) {
        type.ifMouseEvent(event, this::handleMouseEvent, () -> notifyEvent(type, event));
    }

    protected <E extends ScreenEvent> void notifyEvent(ScreenEventType<E> type, E event) {
        type.onEvent(this, event);
        eventHandlerDelegate.handleEvent(type, event);
        getSkin().onEvent(type, event);
    }

    @Override
    public <E extends ScreenEvent> void addListener(ScreenEventType<E> type, ScreenEventListener<E> listener) {
        eventHandlerDelegate.addListener(type, listener);
    }

    @Override
    public <E extends ScreenEvent> void removeListener(ScreenEventType<E> type, ScreenEventListener<E> listener) {
        eventHandlerDelegate.removeListener(type, listener);
    }

    @Override
    public boolean preRender(Object matrices, int mouseX, int mouseY, float delta) {
        boolean res = checkRender();
        return getSkin().preRender(this, matrices, mouseX, mouseY, delta) || res;
    }

    @Override
    public void render(Object matrices, int mouseX, int mouseY, float delta) {
        getSkin().render(this, matrices, mouseX, mouseY, delta);
    }

    @Override
    public void postRender(Object matrices, int mouseX, int mouseY, float delta) {
        getSkin().postRender(this, matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        getSkin().tick();
    }

    @Override
    public boolean checkRender() {
        boolean res = false;
        if (shouldComputeSize) {
            computeSize();
            res = true;
        }
        if (shouldUpdateSize) {
            updateSize();
            res = true;
        }
        return res;
    }

    @Override
    public void shouldComputeSize() {
        shouldComputeSize = true;
    }

    protected void computeSize() {
        shouldComputeSize = false;
        computeWidth();
        computeHeight();
    }

    private void computeWidth() {
        setComputedWidth(getSkin().computeWidth(this) + getPadding().getHorizontal());
    }

    private void computeHeight() {
        setComputedHeight(getSkin().computeHeight(this) + getPadding().getVertical());
    }

    protected void shouldUpdateSize() {
        shouldUpdateSize = true;
    }

    protected void updateSize() {
        shouldUpdateSize = false;
        updateWidth();
        updateHeight();
    }

    private void updateWidth() {
        int width = getPrefWidth();
        if (width == COMPUTED_SIZE) {
            if (getParentPrefWidth() != NONE) {
                width = getParentPrefWidth();
            } else {
                width = getComputedWidth();
            }
        }
        width = Math.max(Math.min(width, getMaxWidth()), getMinWidth());
        if (parentProperty().hasValue()) {
            width = Math.min(width, getParent().getMaxChildrenWidth());
        }
        setWidth(width);
    }

    private void updateHeight() {
        int height = getPrefHeight();
        if (height == COMPUTED_SIZE) {
            if (getParentPrefHeight() != NONE) {
                height = getParentPrefHeight();
            } else {
                height = getComputedHeight();
            }
        }
        height = Math.max(Math.min(height, getMaxHeight()), getMinHeight());
        if (parentProperty().hasValue()) {
            height = Math.min(height, getParent().getMaxChildrenHeight());
        }
        setHeight(height);
    }

    private void updateParentWidth() {
        if (getParent() != null) {
            getParent().shouldComputeSize();
            getParent().shouldUpdateChildren();
        }
    }

    private void updateParentHeight() {
        if (getParent() != null) {
            getParent().shouldComputeSize();
            getParent().shouldUpdateChildren();
        }
    }

    private void updateScene(Parent newVal) {
        sceneProperty.unbind();
        if (newVal != null) {
            sceneProperty.bind(newVal.sceneProperty());
        } else {
            sceneProperty.setValue(null);
        }
    }
}
