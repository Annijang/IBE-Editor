package com.github.franckyi.gamehooks.impl.common;

import com.github.franckyi.gamehooks.api.common.TagFactory;
import com.github.franckyi.gamehooks.util.common.tag.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class FabricTagFactory implements TagFactory<CompoundTag> {
    public static final TagFactory<CompoundTag> INSTANCE = new FabricTagFactory();

    @Override
    public CompoundTag parseObject(ObjectTag obj) {
        if (obj == null) return null;
        CompoundTag c = new CompoundTag();
        obj.getValue().forEach((s, tag) -> c.put(s, parseTag(tag)));
        return c;
    }

    @Override
    public ObjectTag parseCompound(CompoundTag c) {
        if (c == null) return null;
        ObjectTag tag = new ObjectTag();
        for (String key : c.getKeys()) {
            net.minecraft.nbt.Tag tag1 = c.get(key);
            tag.getValue().put(key, parseTag(tag1));
        }
        return tag;
    }

    private Tag<?> parseTag(net.minecraft.nbt.Tag tag) {
        switch (tag.getType()) {
            case Tag.BYTE_ID:
                return new ByteTag(((net.minecraft.nbt.ByteTag) tag).getByte());
            case Tag.SHORT_ID:
                return new ShortTag(((net.minecraft.nbt.ShortTag) tag).getShort());
            case Tag.INT_ID:
                return new IntTag(((net.minecraft.nbt.IntTag) tag).getInt());
            case Tag.LONG_ID:
                return new LongTag(((net.minecraft.nbt.LongTag) tag).getLong());
            case Tag.FLOAT_ID:
                return new FloatTag(((net.minecraft.nbt.FloatTag) tag).getFloat());
            case Tag.DOUBLE_ID:
                return new DoubleTag(((net.minecraft.nbt.DoubleTag) tag).getDouble());
            case Tag.BYTE_ARRAY_ID:
                return new ByteArrayTag(((net.minecraft.nbt.ByteArrayTag) tag).getByteArray());
            case Tag.STRING_ID:
                return new StringTag(tag.asString());
            case Tag.LIST_ID:
                return parseList((ListTag) tag);
            case Tag.COMPOUND_ID:
                return parseCompound((CompoundTag) tag);
            case Tag.INT_ARRAY_ID:
                return new IntArrayTag(((net.minecraft.nbt.IntArrayTag) tag).getIntArray());
            case Tag.LONG_ARRAY_ID:
                return new LongArrayTag(((net.minecraft.nbt.LongArrayTag) tag).getLongArray());
        }
        return null;
    }

    private net.minecraft.nbt.Tag parseTag(Tag<?> tag) {
        switch (tag.getType()) {
            case Tag.BYTE_ID:
                return net.minecraft.nbt.ByteTag.of(((ByteTag) tag).getValue());
            case Tag.SHORT_ID:
                return net.minecraft.nbt.ShortTag.of(((ShortTag) tag).getValue());
            case Tag.INT_ID:
                return net.minecraft.nbt.IntTag.of(((IntTag) tag).getValue());
            case Tag.LONG_ID:
                return net.minecraft.nbt.LongTag.of(((LongTag) tag).getValue());
            case Tag.FLOAT_ID:
                return net.minecraft.nbt.FloatTag.of(((FloatTag) tag).getValue());
            case Tag.DOUBLE_ID:
                return net.minecraft.nbt.DoubleTag.of(((DoubleTag) tag).getValue());
            case Tag.BYTE_ARRAY_ID:
                return new net.minecraft.nbt.ByteArrayTag(((ByteArrayTag) tag).getValue());
            case Tag.STRING_ID:
                return net.minecraft.nbt.StringTag.of(((StringTag) tag).getValue());
            case Tag.LIST_ID:
                return parseArray((ArrayTag) tag);
            case Tag.COMPOUND_ID:
                return parseObject((ObjectTag) tag);
            case Tag.INT_ARRAY_ID:
                return new net.minecraft.nbt.IntArrayTag(((IntArrayTag) tag).getValue());
            case Tag.LONG_ARRAY_ID:
                return new net.minecraft.nbt.LongArrayTag(((LongArrayTag) tag).getValue());
        }
        return null;
    }

    private ListTag parseArray(ArrayTag tag) {
        ListTag listTag = new ListTag();
        for (Tag<?> e : tag.getValue()) {
            listTag.add(parseTag(e));
        }
        return listTag;
    }

    private ArrayTag parseList(ListTag tag) {
        ArrayTag arrayTag = new ArrayTag();
        for (net.minecraft.nbt.Tag e : tag) {
            arrayTag.getValue().add(parseTag(e));
        }
        return arrayTag;
    }
}