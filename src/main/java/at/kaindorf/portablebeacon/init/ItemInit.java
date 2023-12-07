package at.kaindorf.portablebeacon.init;

import at.kaindorf.portablebeacon.PortableBeacon;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PortableBeacon.MODID);
    private static FoodProperties FoodProperty;

    public static final RegistryObject<Item> EXAMPLE_ITEM = //neues Item
            ITEMS.register("key_item", //Itemname
                    () -> new Item(new Item.Properties()
                            .stacksTo(8) //Stack size
                            .tab(CreativeModeTab.TAB_MISC) //Creative tab
                    ));

    public static final RegistryObject<Item> PORTABLE_BEACON = ITEMS.register("portable_beacon",
            at.kaindorf.portablebeacon.items.PortableBeacon::new);

    public static final RegistryObject<BlockItem> METAL_BLOCK =
            ITEMS.register("metal_block",
                    () -> new BlockItem(BlockInit.METAL_BLOCK.get(),
                            new Item.Properties()
                                    .tab(CreativeModeTab.TAB_BUILDING_BLOCKS)
                                    .stacksTo(64)));


}
