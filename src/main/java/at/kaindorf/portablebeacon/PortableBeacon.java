package at.kaindorf.portablebeacon;

import at.kaindorf.portablebeacon.init.BlockInit;
import at.kaindorf.portablebeacon.init.ItemInit;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Random;

import static at.kaindorf.portablebeacon.items.PortableBeacon.BEACON_PORTABLE;

//import static at.kaindorf.portablebeacon.init.ItemInit.BEAcON_PORTABLE;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PortableBeacon.MODID)
public class PortableBeacon {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "portablebeacon";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // create a Deferred Register to hold Blocks which will all be registered under the "portablebeacon" namespace
    public static final DeferredRegister<Block> BLOcKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // create a Deferred Register to hold Items which will all be registered under the "portablebeacon" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // creates a new Block with the id "portablebeacon:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOcK = BLOcKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // creates a new BlockItem with the id "portablebeacon:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOcK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public PortableBeacon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(modEventBus); //Items aus ItemInit einladen
        BlockInit.BLOCKS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOcKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM cOMMON SETUP");
        LOGGER.info("DIRT BLOcK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
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
    public MobEffect standardEffect;
    private final int MINUTE_TO_SECONDS = 60;
    private final int SECOND_TO_TICKS = 20;
    private int ticksUntilRecursion = 0;

    public void consumeItem(ServerPlayer serverPlayer, TickEvent.PlayerTickEvent event) {
        Inventory inv = serverPlayer.getInventory();
        int iron = inv.findSlotMatchingItem(new ItemStack(Items.IRON_INGOT));
        int ironDuration = MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        MobEffect ironEffect = MobEffects.MOVEMENT_SPEED;
        int gold = inv.findSlotMatchingItem(new ItemStack(Items.GOLD_INGOT));
        int goldDuration = (int) (1.5 * MINUTE_TO_SECONDS * SECOND_TO_TICKS);
        MobEffect goldEffect = MobEffects.DIG_SPEED;
        int emerald = inv.findSlotMatchingItem(new ItemStack(Items.EMERALD));
        int emeraldDuration = (int) (0.5 * MINUTE_TO_SECONDS * SECOND_TO_TICKS);
        MobEffect emeraldEffect = MobEffects.DAMAGE_BOOST;
        int diamond = inv.findSlotMatchingItem(new ItemStack(Items.DIAMOND));
        int diamondDuration = 10 * MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        MobEffect diamondEffect = MobEffects.REGENERATION;
        int netherite = inv.findSlotMatchingItem(new ItemStack(Items.NETHERITE_INGOT));
        int netheriteDuration = 20 * MINUTE_TO_SECONDS * SECOND_TO_TICKS;
        MobEffect netheriteEffect = MobEffects.DAMAGE_RESISTANCE;
        switch (new Random().nextInt(5)) {
            case 0:
                standardItem = iron;
                standardDuration = ironDuration;
                standard = Items.IRON_INGOT;
                standardEffect = ironEffect;
                break;
            case 1:
                standardItem = gold;
                standardDuration = goldDuration;
                standard = Items.GOLD_INGOT;
                standardEffect = goldEffect;
                break;
            case 2:
                standardItem = emerald;
                standardDuration = emeraldDuration;
                standard = Items.EMERALD;
                standardEffect = emeraldEffect;
                break;
            case 3:
                standardItem = diamond;
                standardDuration = diamondDuration;
                standard = Items.DIAMOND;
                standardEffect = diamondEffect;
                break;
            case 4:
                standardItem = netherite;
                standardDuration = netheriteDuration;
                standard = Items.NETHERITE_INGOT;
                standardEffect = netheriteEffect;
                break;
            default:
                standardDuration = 0;
                standard = null;
                break;
        }

        System.out.println(standard);

        if (event.player.getInventory().findSlotMatchingItem(new ItemStack(standard)) == -1) ticksUntilRecursion = 10;
        else ticksUntilRecursion = standardDuration - 100;
        if (standardItem == -1) {
            standardDuration = 0;
            // Check for the presence of required items before consuming
            if (iron != -1 || gold != -1 || emerald != -1 || diamond != -1 || netherite != -1) {
                // Recursively call consumeItem to find a valid item for consumption
                standardDuration = 0;
                standard = null;
                consumeItem(serverPlayer, event);
            }
        }
    }

    private int timeSinceLastRecursion = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isHolding(BEACON_PORTABLE.get()) && event.phase.equals(TickEvent.Phase.END)) {
            timeSinceLastRecursion++;
            System.out.println("Time since last run: " + timeSinceLastRecursion + " \nTime until recursion: " + ticksUntilRecursion);
            if (event.player instanceof ServerPlayer serverPlayer && (timeSinceLastRecursion >= ticksUntilRecursion-500)) {
                timeSinceLastRecursion = 0;
                consumeItem(serverPlayer, event);

                Inventory inv = serverPlayer.getInventory();
                if (event.player.getInventory().findSlotMatchingItem(new ItemStack(standard)) > -1) {
                    //if (!(event.player.getActiveEffects().toString().contains("speed"))) {
                    event.player.addEffect(new MobEffectInstance(standardEffect, standardDuration, 1));                    //}
                    timeSinceLastConsumed += 0.05f;
                    if (!event.player.hasEffect(standardEffect)) {
                        event.player.addEffect(new MobEffectInstance(standardEffect, standardDuration, 1));
                    }
                    // Adjusted item removal logic
                    ItemStack itemToRemove = new ItemStack(standard, inv.getItem(standardItem).getCount()-1);
                    Inventory inv2 = serverPlayer.getInventory();
                    inv2.removeItem(itemToRemove);
                    // Reset the timer and update the client inventory
                    timeSinceLastConsumed = 0.0f;
                    serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, standardItem, itemToRemove));
                }
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class clientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
