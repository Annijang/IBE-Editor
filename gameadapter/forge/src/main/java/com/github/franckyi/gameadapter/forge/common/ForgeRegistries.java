package com.github.franckyi.gameadapter.forge.common;

import com.github.franckyi.gameadapter.api.common.Registries;
import com.github.franckyi.gameadapter.api.common.registry.Enchantment;
import com.github.franckyi.gameadapter.api.common.registry.RegistryEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public final class ForgeRegistries implements Registries {
    public static final ForgeRegistries INSTANCE = new ForgeRegistries();
    private List<RegistryEntry> items;
    private List<RegistryEntry> blocks;
    private List<Enchantment> enchantments;
    private List<RegistryEntry> attributes;
    private List<RegistryEntry> potions;
    private List<RegistryEntry> effects;

    private ForgeRegistries() {
    }

    @Override
    public List<RegistryEntry> getItems() {
        if (items == null) {
            items = net.minecraftforge.registries.ForgeRegistries.ITEMS.getEntries().stream().map(entry -> RegistryEntry.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getDescriptionId()
            )).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public List<RegistryEntry> getBlocks() {
        if (blocks == null) {
            blocks = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getEntries().stream().map(entry -> RegistryEntry.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getDescriptionId()
            )).collect(Collectors.toList());
        }
        return blocks;
    }

    @Override
    public List<Enchantment> getEnchantments() {
        if (enchantments == null) {
            enchantments = net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS.getEntries().stream().map(entry -> Enchantment.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getDescriptionId(),
                    entry.getValue().isCurse(),
                    item -> entry.getValue().canEnchant(item.get())
            )).collect(Collectors.toList());
        }
        return enchantments;
    }

    @Override
    public List<RegistryEntry> getAttributes() {
        if (attributes == null) {
            attributes = net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getEntries().stream().map(entry -> RegistryEntry.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getDescriptionId()
            )).collect(Collectors.toList());
        }
        return attributes;
    }

    @Override
    public List<RegistryEntry> getPotions() {
        if (potions == null) {
            potions = net.minecraftforge.registries.ForgeRegistries.POTION_TYPES.getEntries().stream().map(entry -> RegistryEntry.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getName("item.minecraft.potion.effect.")
            )).collect(Collectors.toList());
        }
        return potions;
    }

    @Override
    public List<RegistryEntry> getEffects() {
        if (effects == null) {
            effects = net.minecraftforge.registries.ForgeRegistries.POTIONS.getEntries().stream().map(entry -> RegistryEntry.of(
                    entry.getKey().location().toString(),
                    entry.getValue().getDescriptionId()
            )).collect(Collectors.toList());
        }
        return effects;
    }

    @Override
    public int getEffectId(String name) {
        return Registry.MOB_EFFECT.getId(net.minecraftforge.registries.ForgeRegistries.POTIONS.getValue(ResourceLocation.tryParse(name)));
    }

    @Override
    public String getEffectFromId(int id) {
        ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.POTIONS.getKey(Registry.MOB_EFFECT.byId(id));
        return key == null ? null : key.toString();
    }
}