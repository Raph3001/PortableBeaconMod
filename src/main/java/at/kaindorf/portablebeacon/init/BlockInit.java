package at.kaindorf.portablebeacon.init;

import at.kaindorf.portablebeacon.PortableBeacon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PortableBeacon.MODID);

    public static final RegistryObject<Block> METAL_BLOCK = //neues Item
            BLOCKS.register("metal_block", //Itemname
                    () -> new Block(BlockBehaviour.Properties.of(Material.METAL)
                            .strength(6f) //Stack size
                            .sound(SoundType.AMETHYST)

                    ));

}
