package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaTextFieldSkinDelegate;
import com.mojang.blaze3d.vertex.PoseStack;

public class VanillaTextFieldSkin<N extends TextField> extends AbstractVanillaWidgetSkin<N, VanillaTextFieldSkinDelegate<N>> {
    public VanillaTextFieldSkin(N node) {
        this(node, new VanillaTextFieldSkinDelegate<>(node));
    }

    protected VanillaTextFieldSkin(N node, VanillaTextFieldSkinDelegate<N> widget) {
        super(node, widget);
    }

    @Override
    public void render(N node, PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(node, matrices, mouseX, mouseY, delta);
        if (!(node.isValidationForced() || node.getValidator().test(node.getText()))) {
            drawBorder(node, matrices, Color.fromRGBA(1, 0, 0, 0.8));
        } else if (node.isSuggested()) {
            drawBorder(node, matrices, Color.fromRGBA(0, 1, 0, 0.8));
        }
    }

    @Override
    public int computeWidth(N node) {
        return 150;
    }

    @Override
    public int computeHeight(N node) {
        return 20;
    }

    private void drawBorder(N node, PoseStack matrices, int color) {
        RenderHelper.drawRectangle(matrices, node.getX(), node.getY(),
                node.getX() + node.getWidth(), node.getY() + node.getHeight(), color);
    }
}
