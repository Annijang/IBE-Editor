package com.github.franckyi.ibeeditor.client.screen.model.entry;

import com.github.franckyi.ibeeditor.client.screen.model.category.CategoryModel;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class EnumEntryModel<E extends Enum<E>> extends ValueEntryModel<E> {
    public EnumEntryModel(CategoryModel category, MutableComponent label, E value, Consumer<E> action) {
        super(category, label, value, action);
    }

    @Override
    public Type getType() {
        return Type.ENUM;
    }
}