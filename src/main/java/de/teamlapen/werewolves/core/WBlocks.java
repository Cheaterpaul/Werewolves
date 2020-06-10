package de.teamlapen.werewolves.core;

import de.teamlapen.werewolves.WerewolfvesMod;
import de.teamlapen.werewolves.blocks.WFlowerBlock;
import de.teamlapen.werewolves.util.REFERENCE;
import de.teamlapen.werewolves.util.WUtils;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.potion.Effects;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class WBlocks {

    public static final Block silver_ore = getNull();
    public static final Block wolfsbane = getNull();


    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(silver_ore));
        registry.register(itemBlock(wolfsbane,new Item.Properties().group(WUtils.creativeTab)));
    }


    static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(REFERENCE.MODID, "silver_ore"));
        registry.register(new WFlowerBlock(Effects.BLINDNESS,5).setRegistryName(REFERENCE.MODID,"wolfsbane"));
    }


    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block) {
        return itemBlock(block, new Item.Properties().group(WUtils.creativeTab));
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        assert block != null;
        BlockItem item = new BlockItem(block, props);
        item.setRegistryName(block.getRegistryName());
        return item;
    }
}
