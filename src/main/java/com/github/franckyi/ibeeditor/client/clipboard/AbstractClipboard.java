package com.github.franckyi.ibeeditor.client.clipboard;

import com.github.franckyi.guapi.Scene;
import com.github.franckyi.guapi.group.HBox;
import com.github.franckyi.guapi.group.VBox;
import com.github.franckyi.guapi.math.Insets;
import com.github.franckyi.guapi.math.Pos;
import com.github.franckyi.guapi.node.Label;
import com.github.franckyi.guapi.node.ListExtended;
import com.github.franckyi.guapi.scene.IBackground;
import com.github.franckyi.ibeeditor.client.IResizable;
import com.github.franckyi.ibeeditor.client.clipboard.logic.EntityClipboardEntry;
import com.github.franckyi.ibeeditor.client.clipboard.logic.IBEClipboard;
import com.github.franckyi.ibeeditor.client.clipboard.logic.ItemClipboardEntry;
import com.github.franckyi.ibeeditor.config.IBEEditorConfig;
import net.minecraft.util.text.TextFormatting;

public abstract class AbstractClipboard extends Scene {

    protected final VBox content;
    protected final Label header;
    protected final ListExtended<ListExtended.NodeEntry<?>> body;
    protected final HBox footer;
    protected Filter filter;

    public AbstractClipboard(String headerText) {
        super(new VBox());
        content = (VBox) this.getContent();
        header = new Label(headerText);
        header.setPrefHeight(30);
        header.setCentered(true);
        body = new ListExtended<>(25);
        body.setOffset(new Insets(0, 10, 10, 10));
        footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPrefHeight(20);
        content.getChildren().add(header);
        content.getChildren().add(body);
        content.getChildren().add(footer);
        this.setContentFullScreen();
        this.setBackground(IBackground.texturedBackground(1));
        this.getOnInitGuiListeners().add(e -> {
            this.setContentFullScreen();
            this.scaleChildrenSize();
        });
        this.setGuiPauseGame(IBEEditorConfig.doesGuiPauseGame);
    }

    protected void scaleChildrenSize() {
        header.setPrefWidth(content.getWidth());
        footer.setPrefWidth(content.getWidth());
        body.setPrefSize(content.getWidth(), content.getHeight() - 60);
        this.scaleEntriesSize();
    }

    private void scaleEntriesSize() {
        body.getChildren().stream()
                .filter(node -> node instanceof IResizable)
                .map(node -> (IResizable) node)
                .forEach(resizable -> resizable.updateSize(content.getWidth()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        body.render(mouseX, mouseY, partialTicks);
        header.render(mouseX, mouseY, partialTicks);
        footer.render(mouseX, mouseY, partialTicks);
        if (body.getChildren().isEmpty()) {
            this.getScreen().drawCenteredString(mc.fontRenderer, "The selection is empty !", this.getScreen().width / 2, body.getY() + body.getHeight() / 2 - 4, TextFormatting.DARK_RED.getColor());
        }
    }

    protected void setFilter(Filter filter) {
        this.filter = filter;
        IBEClipboard clipboard = IBEClipboard.getInstance();
        body.getChildren().clear();
        switch (filter) {
            case ALL:
                clipboard.getItems().forEach(this::newItemEntry);
                clipboard.getEntities().forEach(this::newEntityEntry);
                break;
            case ITEM:
                clipboard.getItems().forEach(this::newItemEntry);
                break;
            case ENTITY:
                clipboard.getEntities().forEach(this::newEntityEntry);
                break;
        }
        this.scaleEntriesSize();
    }

    protected abstract void newItemEntry(ItemClipboardEntry item);

    protected abstract void newEntityEntry(EntityClipboardEntry entity);

    public enum Filter {
        ALL("All"),
        ITEM("Item"),
        ENTITY("Entity");

        private final String s;

        Filter(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

}
