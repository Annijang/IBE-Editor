package com.github.franckyi.ibeeditor.impl.client.mvc.editor.controller;

import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.franckyi.ibeeditor.api.client.mvc.editor.controller.CategoryController;
import com.github.franckyi.ibeeditor.api.client.mvc.editor.model.CategoryModel;
import com.github.franckyi.ibeeditor.api.client.mvc.editor.view.CategoryView;
import com.github.franckyi.minecraft.util.common.TextFormatting;

import java.util.List;

import static com.github.franckyi.guapi.GUAPIHelper.*;

public class CategoryControllerImpl extends AbstractController<CategoryModel, CategoryView> implements CategoryController {
    public CategoryControllerImpl(CategoryModel model, CategoryView view) {
        super(model, view);
    }

    private void updateLabel() {
        TextFormatting[] format = null;
        if (model.isSelected()) {
            if (model.isValid()) {
                format = new TextFormatting[]{YELLOW, BOLD};
            } else {
                format = new TextFormatting[]{RED, BOLD};
            }
        } else if (!model.isValid()) {
            format = new TextFormatting[]{RED};
        }
        view.getLabel().setLabel(translatedText(model.getName(), format));
    }

    @Override
    public void bind() {
        model.selectedProperty().addListener(this::updateLabel);
        model.validProperty().addListener(this::updateLabel);
        model.nameProperty().addListener(this::updateLabel);
        view.getLabel().onMouseClick(e -> {
            if (e.getButton() == MouseButtonEvent.LEFT_BUTTON) {
                model.getEditor().setSelectedCategory(model);
            }
        });
        updateLabel();
    }
}