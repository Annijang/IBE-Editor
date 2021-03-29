package com.github.franckyi.ibeeditor.impl.client.mvc.editor.model;

import com.github.franckyi.databindings.Bindings;
import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.ibeeditor.api.client.mvc.editor.model.CategoryModel;
import com.github.franckyi.ibeeditor.api.client.mvc.editor.model.EntryModel;

public abstract class AbstractEntryModel implements EntryModel {
    private final CategoryModel category;
    private final BooleanProperty validProperty = Bindings.getPropertyFactory().ofBoolean(true);

    protected AbstractEntryModel(CategoryModel category) {
        this.category = category;
        validProperty().addListener(() -> getCategory().updateValidity());
    }

    @Override
    public BooleanProperty validProperty() {
        return validProperty;
    }

    @Override
    public CategoryModel getCategory() {
        return category;
    }
}