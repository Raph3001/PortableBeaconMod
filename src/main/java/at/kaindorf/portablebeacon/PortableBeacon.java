package at.kaindorf.portablebeacon;

import at.kaindorf.portablebeacon.init.BlockInit;
import at.kaindorf.portablebeacon.init.ItemInit;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Random;

import static at.kaindorf.portablebeacon.items.PortableBeacon.BEACON_PORTABLE;

//import static at.kaindorf.portablebeacon.init.ItemInit.BEACON_PORTABLE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PortableBeacon.MODID)
public class PortableBeacon {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "portablebeacon";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "portablebeacon" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "portablebeacon" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "portablebeacon:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "portablebeacon:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public PortableBeacon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(modEventBus); //Items aus ItemInit einladen
        BlockInit.BLOCKS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private float timeSinceLastConsumed = 100000.0f;
    public int standardItem;
    public int standardDuration;
    public Item standard;

    public void consumeItem(ServerPlayer serverPlayer) {
        final int MINUTE_TO_SECONDS = 60;
        final int SECOND_TO_TICKS = 20;
        Inventory inv = serverPlayer.getInventory();
        int iron = inv.findSlotMatchingItem(new ItemStack(Items.IRON_INGOT));
        int ironDuration = MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        int gold = inv.findSlotMatchingItem(new ItemStack(Items.GOLD_INGOT));
        int goldDuration = (int) (1.5 * MINUTE_TO_SECONDS * SECOND_TO_TICKS);
        int emerald = inv.findSlotMatchingItem(new ItemStack(Items.EMERALD));
        int emeraldDuration = (int) (0.5 * MINUTE_TO_SECONDS * SECOND_TO_TICKS);
        int diamond = inv.findSlotMatchingItem(new ItemStack(Items.DIAMOND));
        int diamondDuration = 10 * MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        int netherite = inv.findSlotMatchingItem(new ItemStack(Items.NETHERITE_INGOT));
        int netheriteDuration = 20 * MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        switch (new Random().nextInt(5)) {
            case 0:
                standardItem = iron;
                standardDuration = ironDuration;
                standard = Items.IRON_INGOT;
                break;
            case 1:
                standardItem = gold;
                standardDuration = goldDuration;
                standard = Items.GOLD_INGOT;
                break;
            case 2:
                standardItem = emerald;
                standardDuration = emeraldDuration;
                standard = Items.EMERALD;
                break;
            case 3:
                standardItem = diamond;
                standardDuration = diamondDuration;
                standard = Items.DIAMOND;
                break;
            case 4:
                standardItem = netherite;
                standardDuration = netheriteDuration;
                standard = Items.NETHERITE_INGOT;
                break;
            default:
                standardItem = iron;
                standardDuration = ironDuration;
                standard = Items.IRON_INGOT;
                break;
        }
        if ((standardItem == -1) && (!((iron == -1) && (gold == -1) && (diamond == -1) && (netherite == -1) && (emerald == -1)))) {
            consumeItem(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isHolding(BEACON_PORTABLE.get()) && event.phase.equals(TickEvent.Phase.END)) {
            if (event.player instanceof ServerPlayer serverPlayer) {
                consumeItem(serverPlayer);

                Inventory inv = serverPlayer.getInventory();
                if (event.player.getInventory().findSlotMatchingItem(new ItemStack(standard)) > -1) {
                    if (!(event.player.getActiveEffects().toString().contains("speed"))) {
                        event.player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, standardDuration, 1));
                    }
                    timeSinceLastConsumed += 0.05f;
                    if (timeSinceLastConsumed > ((float) (standardDuration / 20))) {
                        ItemStack itemToRemove = new ItemStack(standard, inv.getItem(standardItem).getCount()-1);
                        serverPlayer.getInventory().setItem(standardItem, itemToRemove);
                        event.player.getInventory().setItem(standardItem, itemToRemove);
                        timeSinceLastConsumed = 0.0f;
                        serverPlayer.connection.send(
                                new ClientboundContainerSetSlotPacket(-2, 0, standardItem, itemToRemove)
                        );
                    }
                }
            }
        }
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }


}
