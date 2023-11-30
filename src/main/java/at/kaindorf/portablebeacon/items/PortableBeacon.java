package at.kaindorf.portablebeacon.items;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PortableBeacon extends Item {

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

}
