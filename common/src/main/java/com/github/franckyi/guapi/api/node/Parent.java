package com.github.franckyi.guapi.api.node;

import com.github.franckyi.databindings.api.ObservableValue;
import com.github.franckyi.guapi.util.Insets;

public interface Parent {
    int getWidth();

    int getHeight();

    Insets getPadding();

    ObservableValue<Scene> sceneProperty();

    ObservableValue<Boolean> disabledProperty();

    default int getMaxChildrenWidth() {
        return getWidth() - getPadding().getHorizontal();
    }

    default int getMaxChildrenHeight() {
        return getHeight() - getPadding().getVertical();
    }

    void shouldComputeSize();

    void shouldUpdateChildren();
}
