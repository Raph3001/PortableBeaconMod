package at.kaindorf.portablebeacon.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class KeyItem extends Item {
    public KeyItem() {
        super(new Properties() //New Item
                .tab(CreativeModeTab.TAB_MISC) //Select Creative Menu Tab
                .stacksTo(32) //Stack size

        );
    }
}
