/*
 */
package com.infinityraider.agricraft.gui;

import com.infinityraider.agricraft.container.FakeContainer;
import java.util.List;
import net.minecraft.inventory.Container;

/*
 *
 */
public interface IAgriGui<T extends Container> {
    
    // ========================================
    // Constants
    // ========================================
    final Container FAKE_CONTAINER = new FakeContainer();

    // ========================================
    // Getters
    // ========================================
    T getContainer();
    
    int getHeight();

    int getWidth();

    // ========================================
    // Hooks
    // ========================================
    default void onGuiInit(AgriGuiWrapper wrapper) {
    }

    default void onUpdateMouse(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY) {
    }
    
    default void onMouseClicked(AgriGuiWrapper wrapper, int relMouseX, int relMouseY, int mouseButton) {
    }
    
    default void onMouseClickMove(AgriGuiWrapper wrapper, int relMouseX, int relMouseY, int mouseButton) {
    }

    default void onRenderForeground(AgriGuiWrapper wrapper, List<String> tooltips, int relMouseX, int relMouseY) {
    }

    default void onRenderBackground(AgriGuiWrapper wrapper, float f, int relMouseX, int relMouseY) {
    }

}
