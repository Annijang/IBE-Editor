package com.github.franckyi.ibeeditor.client.editor.entity;

import com.github.franckyi.ibeeditor.IBEEditorMod;
import com.github.franckyi.ibeeditor.client.editor.AbstractEditor;
import com.github.franckyi.ibeeditor.client.util.IBENotification;
import com.github.franckyi.ibeeditor.network.entity.EntityEditorMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Consumer;

public class EntityEditor extends AbstractEditor {

    private final Entity entity;
    private final Consumer<Entity> action;

    public EntityEditor(Entity entity) {
        this(entity, null);
    }

    public EntityEditor(Entity entity, Consumer<Entity> action) {
        super("Entity Editor");
        this.entity = entity;
        this.action = action;
        this.addCategory("General", new GeneralEntityCategory(entity));
        if (!(entity instanceof EntityPlayer)) {
            if (entity instanceof EntityLiving) {
                this.addCategory("Inventory", new InventoryEntityCategory((EntityLiving) entity));
            }
            this.addCategory("Tools", new ToolsEntityCategory(entity));
        }
        this.show();
    }

    @Override
    protected void apply() {
        NBTTagCompound entityTag = entity.writeWithoutTypeId(new NBTTagCompound());
        super.apply();
        if (entityTag.equals(entity.writeWithoutTypeId(new NBTTagCompound()))) {
            IBENotification.show(IBENotification.Type.EDITOR, 3, TextFormatting.YELLOW + "Nothing to save.");
        } else {
            if (action != null) {
                action.accept(entity);
            } else {
                IBEEditorMod.CHANNEL.sendToServer(new EntityEditorMessage(entity));
                IBENotification.show(IBENotification.Type.EDITOR, 3, TextFormatting.GREEN + "Entity saved.");
            }
        }
    }
}
