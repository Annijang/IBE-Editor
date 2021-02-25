package com.github.franckyi.ibeeditor.impl.client;

import com.github.franckyi.gamehooks.GameHooks;
import com.github.franckyi.gamehooks.api.ClientHooks;
import com.github.franckyi.gamehooks.api.client.KeyBinding;
import com.github.franckyi.gamehooks.api.client.Screen;
import com.github.franckyi.gamehooks.api.common.*;
import com.github.franckyi.gamehooks.util.common.tag.ObjectTag;
import com.github.franckyi.guapi.GUAPI;
import com.github.franckyi.guapi.GUAPIFactory;
import com.github.franckyi.guapi.api.ScreenHandler;
import com.github.franckyi.ibeeditor.api.client.mvc.view.*;
import com.github.franckyi.ibeeditor.impl.client.mvc.*;
import com.github.franckyi.ibeeditor.impl.client.mvc.model.EditorModelImpl;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static com.github.franckyi.guapi.GUAPIFactory.*;
import static com.github.franckyi.ibeeditor.impl.common.IBEEditorCommon.LOGGER;

public final class IBEEditorClient {
    private static final Marker MARKER = MarkerManager.getMarker("Client");
    public static KeyBinding editorKey;
    public static KeyBinding nbtEditorKey;
    public static KeyBinding clipboardKey;
    private static boolean serverModInstalled;

    public static void init(ClientHooks clientHooks, ScreenHandler screenHandler) {
        LOGGER.info(MARKER, "Initializing IBE Editor - client");
        GameHooks.initClient(clientHooks);
        initGUAPI(screenHandler);
        editorKey = GameHooks.client().registerKeyBinding("ibeeditor.key.editor", GLFW.GLFW_KEY_I, "ibeeditor.category");
        nbtEditorKey = GameHooks.client().registerKeyBinding("ibeeditor.key.nbt_editor", GLFW.GLFW_KEY_N, "ibeeditor.category");
        clipboardKey = GameHooks.client().registerKeyBinding("ibeeditor.key.clipboard", GLFW.GLFW_KEY_J, "ibeeditor.category");
    }

    private static void initGUAPI(ScreenHandler screenHandler) {
        GUAPI.init(screenHandler);
        GUAPIFactory.registerMVC(EditorView.class, EditorMVCImpl.INSTANCE);
        GUAPIFactory.registerMVC(CategoryView.class, CategoryMVCImpl.INSTANCE);
        GUAPIFactory.registerMVC(StringEntryView.class, StringEntryMVCImpl.INSTANCE);
        GUAPIFactory.registerMVC(IntegerEntryView.class, IntegerEntryMVCImpl.INSTANCE);
        GUAPIFactory.registerMVC(NBTEditorView.class, NBTEditorMVCImpl.INSTANCE);
        GUAPIFactory.registerMVC(TagView.class, TagMVCImpl.INSTANCE);
    }

    public static void tick() {
        if (editorKey.isPressed()) {
            GameHooks.client().unlockCursor();
            openWorldEditor(false);
        } else if (nbtEditorKey.isPressed()) {
            GameHooks.client().unlockCursor();
            openWorldEditor(true);
        } else if (clipboardKey.isPressed()) {
            GameHooks.client().unlockCursor();
            openClipboard();
        }
    }

    public static void openWorldEditor(boolean nbt) {
        if (!(tryOpenEntityEditor(nbt) || tryOpenBlockEditor(nbt) || tryOpenItemEditor(nbt))) {
            requestOpenSelfEditor(nbt);
        }
    }

    public static boolean tryOpenEntityEditor(boolean nbt) {
        WorldEntity entity = GameHooks.client().getEntityMouseOver();
        if (entity != null) {
            requestOpenEntityEditor(entity.getEntityId(), nbt);
            return true;
        }
        return false;
    }

    public static boolean tryOpenBlockEditor(boolean nbt) {
        WorldBlock block = GameHooks.client().getBlockMouseOver();
        if (block != null) {
            requestOpenBlockEditor(block.getPos(), nbt);
            return true;
        }
        return false;
    }

    public static boolean tryOpenItemEditor(boolean nbt) {
        Item item = GameHooks.client().getPlayer().getItemMainHand();
        if (item != null) {
            openItemEditor(item, nbt, IBEEditorClient::updatePlayerMainHandItem);
            return true;
        }
        return false;
    }

    public static void openClipboard() {
        GUAPI.getScreenHandler().showScene(scene(mvc(EditorView.class, new EditorModelImpl()), true, true));
    }

    public static void handleScreenEvent(Screen screen, int keyCode) {
        if (keyCode == editorKey.getKeyCode() || keyCode == nbtEditorKey.getKeyCode()) {
            Slot slot = screen.getInventoryFocusedSlot();
            if (slot.hasStack()) {
                if (slot.isInPlayerInventory()) {
                    openItemEditor(slot.getStack(), keyCode == nbtEditorKey.getKeyCode(), item -> updatePlayerInventoryItem(item, slot.getIndex()));
                } else {
                    WorldBlock block = GameHooks.client().getBlockMouseOver();
                    if (block != null) {
                        openItemEditor(slot.getStack(), keyCode == nbtEditorKey.getKeyCode(), item -> updateBlockInventoryItem(item, slot.getIndex(), block.getPos()));
                    }
                }
            }
        }
    }

    public static void openItemEditor(Item item, boolean nbt, Consumer<Item> action) {
        GameHooks.logger().debug(MARKER, "Opening Item Editor (item={};nbt={})", item.getStack(), nbt);
        if (nbt) {
            EditorHandler.showNBTEditor(item.getTag(), tag -> action.accept(GameHooks.common().createItemFromTag(tag)));
        } else {
            EditorHandler.showItemEditor(item);
        }
    }

    public static void requestOpenBlockEditor(Pos blockPos, boolean nbt) {
        GameHooks.logger().debug(MARKER, "Requesting Block Editor (pos={};nbt={})", blockPos.getPos(), nbt);
        ClientNetworkEmitter.requestOpenBlockEditor(blockPos, nbt);
    }

    public static void openBlockEditor(Block block, Pos blockPos, boolean nbt) {
        GameHooks.logger().debug(MARKER, "Opening Block Editor (pos={};nbt={})", blockPos.getPos(), nbt);
        if (nbt) {
            EditorHandler.showNBTEditor(block.getTag(), tag -> updateBlock(blockPos, tag));
        } else {
            //EditorHandler.showBlockEditor(block);
        }
    }

    public static void requestOpenEntityEditor(int entityId, boolean nbt) {
        GameHooks.logger().debug(MARKER, "Requesting Entity Editor (id={};nbt={})", entityId, nbt);
        ClientNetworkEmitter.requestOpenEntityEditor(entityId, nbt);
    }

    public static void openEntityEditor(Entity entity, int entityId, boolean nbt) {
        GameHooks.logger().debug(MARKER, "Opening Entity Editor (id={};nbt={})", entityId, nbt);
        if (nbt) {
            EditorHandler.showNBTEditor(entity.getTag(), tag -> updateEntity(entityId, tag));
        } else {
            //EditorHandler.showEntityEditor(entity);
        }
    }

    public static void requestOpenSelfEditor(boolean nbt) {
        requestOpenEntityEditor(GameHooks.client().getPlayer().getEntityId(), nbt);
    }

    public static boolean isServerModInstalled() {
        return serverModInstalled;
    }

    public static void setServerModInstalled(boolean serverModInstalled) {
        GameHooks.logger().debug(MARKER, "Setting 'serverModInstalled' to {}", serverModInstalled);
        IBEEditorClient.serverModInstalled = serverModInstalled;
    }

    private static void updatePlayerMainHandItem(Item item) {
        ClientNetworkEmitter.updatePlayerMainHandItem(item);
        GUAPI.getScreenHandler().hideScene();
    }

    private static void updatePlayerInventoryItem(Item item, int slotId) {
        ClientNetworkEmitter.updatePlayerInventoryItem(item, slotId);
        GUAPI.getScreenHandler().hideScene();
    }

    private static void updateBlockInventoryItem(Item item, int slotId, Pos blockPos) {
        ClientNetworkEmitter.updateBlockInventoryItem(item, slotId, blockPos);
        GUAPI.getScreenHandler().hideScene();
    }

    private static void updateBlock(Pos blockPos, ObjectTag tag) {
        ClientNetworkEmitter.updateBlock(blockPos, tag);
        GUAPI.getScreenHandler().hideScene();
    }

    private static void updateEntity(int entityId, ObjectTag tag) {
        ClientNetworkEmitter.updateEntity(entityId, tag);
        GUAPI.getScreenHandler().hideScene();
    }
}