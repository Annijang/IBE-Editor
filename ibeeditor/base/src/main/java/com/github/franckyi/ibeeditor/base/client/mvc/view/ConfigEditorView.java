package com.github.franckyi.ibeeditor.base.client.mvc.view;

import com.github.franckyi.gameadapter.api.common.text.TranslatedText;
import com.github.franckyi.guapi.api.node.Node;

import static com.github.franckyi.guapi.GuapiHelper.*;

public class ConfigEditorView extends ListEditorView {
    @Override
    public void build() {
        super.build();
        ((TranslatedText) getDoneButton().getLabel()).setTranslate("ibeeditor.gui.save");
    }

    @Override
    protected Node createHeader() {
        return label(translated("ibeeditor.gui.settings").aqua().bold(), true).textAlign(CENTER).prefHeight(20);
    }
}