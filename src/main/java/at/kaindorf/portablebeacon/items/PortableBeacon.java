package at.kaindorf.portablebeacon.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class PortableBeacon extends Item implements MenuProvider{
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public PortableBeacon() {
        super(new Properties()
                .stacksTo(1) //Stack size
                .tab(CreativeModeTab.TAB_MISC) //Creative tab
                .fireResistant());
    }

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, at.kaindorf.portablebeacon.PortableBeacon.MODID);

    public static final RegistryObject<Item> BEACON_PORTABLE = //neues Item
            ITEMS.register("portable_beacon", //Itemname
                    () -> new Item(new Item.Properties()
                            .stacksTo(1) //Stack size
                            .tab(CreativeModeTab.TAB_MISC) //Creative tab
                            .fireResistant()
                    ));

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }

    
}
