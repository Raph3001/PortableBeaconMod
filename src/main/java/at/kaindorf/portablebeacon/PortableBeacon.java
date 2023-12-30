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

import java.util.*;

import static at.kaindorf.portablebeacon.items.PortableBeacon.BEACON_PORTABLE;

@Mod(PortableBeacon.MODID)
public class PortableBeacon {

    public static final String MODID = "portablebeacon";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOcKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> EXAMPLE_BLOcK = BLOcKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOcK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public PortableBeacon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(modEventBus); //Items aus ItemInit einladen
        BlockInit.BLOCKS.register(modEventBus);

        BLOcKS.register(modEventBus);
        ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM cOMMON SETUP");
        LOGGER.info("DIRT BLOcK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

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
    private final Map<Item, Integer> cooldowns = new HashMap<>();

    public void consumeItem(ServerPlayer serverPlayer, TickEvent.PlayerTickEvent event) {
        Inventory inv = serverPlayer.getInventory();
        int iron = inv.findSlotMatchingItem(new ItemStack(Items.IRON_INGOT));
        int gold = inv.findSlotMatchingItem(new ItemStack(Items.GOLD_INGOT));
        int emerald = inv.findSlotMatchingItem(new ItemStack(Items.EMERALD));
        int diamond = inv.findSlotMatchingItem(new ItemStack(Items.DIAMOND));
        int netherite = inv.findSlotMatchingItem(new ItemStack(Items.NETHERITE_INGOT));
        List<Item> itemsInInv = new ArrayList<>();

        if (inv.findSlotMatchingItem(new ItemStack(Items.IRON_INGOT)) > -1) {
            itemsInInv.add(Items.IRON_INGOT);
        }
        if (inv.findSlotMatchingItem(new ItemStack(Items.GOLD_INGOT)) > -1) {
            itemsInInv.add(Items.GOLD_INGOT);
        }
        if (inv.findSlotMatchingItem(new ItemStack(Items.DIAMOND)) > -1) {
            itemsInInv.add(Items.DIAMOND);
        }
        if (inv.findSlotMatchingItem(new ItemStack(Items.NETHERITE_INGOT)) > -1) {
            itemsInInv.add(Items.NETHERITE_INGOT);
        }
        if (inv.findSlotMatchingItem(new ItemStack(Items.EMERALD)) > -1) {
            itemsInInv.add(Items.EMERALD);
        }

        for (Item item: itemsInInv) {
            int slot = inv.findSlotMatchingItem(new ItemStack(item));

                if (cooldowns.getOrDefault(item, 0) <= 0 || !(serverPlayer.hasEffect(Objects.requireNonNull(getEffectForItem(item))))) {
                    int duration = getDurationForItem(item);
                    MobEffect effect = getEffectForItem(item);

                    doStuff(event, serverPlayer, slot, effect, item, duration);

                    cooldowns.put(item, getCooldownForItem(item));
                } else {
                    cooldowns.put(item, cooldowns.get(item) - 1);
                }
        }

        System.out.println(standard);

        if (event.player.getInventory().findSlotMatchingItem(new ItemStack(standard)) == -1) ticksUntilRecursion = 10;
        else ticksUntilRecursion = standardDuration - 100;
        if (standardItem == -1) {
            standardDuration = 0;
            if (iron != -1 || gold != -1 || emerald != -1 || diamond != -1 || netherite != -1) {
                standardDuration = 0;
                standard = null;
                consumeItem(serverPlayer, event);
            }
        }
    }

    private int getDurationForItem(Item item) {
        if (item == Items.IRON_INGOT) {
            return 1200;
        } else if (item == Items.GOLD_INGOT) {
            return 1800;
        }
        else if (item == Items.EMERALD) {
            return 900;
        }
        else if (item == Items.DIAMOND) {
            return 6000;
        }
        else if (item == Items.NETHERITE_INGOT) {
            return 12000;
        }
        return 0;
    }

    private MobEffect getEffectForItem(Item item) {
        if (item == Items.IRON_INGOT) {
            return MobEffects.MOVEMENT_SPEED;
        } else if (item == Items.GOLD_INGOT) {
            return MobEffects.DIG_SPEED;
        }
        else if (item == Items.EMERALD) {
            return MobEffects.DAMAGE_BOOST;
        }
        else if (item == Items.DIAMOND) {
            return MobEffects.REGENERATION;
        }
        else if (item == Items.NETHERITE_INGOT) {
            return MobEffects.DAMAGE_RESISTANCE;
        }
        return null;
    }

    private int getCooldownForItem(Item item) {
        if (item == Items.IRON_INGOT) {
            return 1150;
        } else if (item == Items.GOLD_INGOT) {
            return 1750;
        }
        else if (item == Items.EMERALD) {
            return 850;
        }
        else if (item == Items.DIAMOND) {
            return 5950;
        }
        else if (item == Items.NETHERITE_INGOT) {
            return 11950;
        }
        return 0;
    }


    private void doStuff(TickEvent.PlayerTickEvent event, ServerPlayer serverPlayer, int slot, MobEffect effect, Item itemKind, int duration) {

        if (effect != null) {
            if (itemKind != Items.DIAMOND) serverPlayer.addEffect(new MobEffectInstance(effect, duration, 1));
            else serverPlayer.addEffect(new MobEffectInstance(effect, duration));
            ItemStack itemToRemove = new ItemStack(itemKind, serverPlayer.getInventory().getItem(slot).getCount() - 1);
            Inventory inv2 = serverPlayer.getInventory();
            inv2.removeItem(itemToRemove);
            cooldowns.put(itemKind, duration);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, slot, itemToRemove));
        }
    }

    private int timeSinceLastRecursion = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isHolding(BEACON_PORTABLE.get()) && event.phase.equals(TickEvent.Phase.END)) {
            timeSinceLastRecursion++;
            System.out.println("Time since last run: " + timeSinceLastRecursion + " \nTime until recursion: " + ticksUntilRecursion);
            if (event.player instanceof ServerPlayer serverPlayer) {
                timeSinceLastRecursion = 0;
                consumeItem(serverPlayer, event);
            }
        }
    }

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
