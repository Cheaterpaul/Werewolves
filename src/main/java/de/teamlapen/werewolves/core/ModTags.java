package de.teamlapen.werewolves.core;

import de.teamlapen.werewolves.util.REFERENCE;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("SameParameterValue")
public class ModTags {
    public static class Blocks extends de.teamlapen.vampirism.core.ModTags.Blocks {
        public static final TagKey<Block> SILVER_ORE = forge("ores/silver");
        public static final TagKey<Block> STORAGE_BLOCKS_SILVER = forge("storage_blocks/silver");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_SILVER = forge("storage_blocks/raw_silver");
        public static final TagKey<Block> MAGIC_LOGS = forge("magic_logs");
        public static final TagKey<Block> JACARANDA_LOGS = forge("jacaranda_logs");


        private static TagKey<Block> mc(ResourceLocation id) {
            return BlockTags.create(id);
        }

        private static TagKey<Block> werewolves(String id) {
            return BlockTags.create(new ResourceLocation(REFERENCE.MODID, id));
        }

        private static TagKey<Block> forge(String id) {
            return BlockTags.create(new ResourceLocation("forge", id));
        }
    }

    public static class Items extends de.teamlapen.vampirism.core.ModTags.Items {
        public static final TagKey<Item> SILVER_ORE = forge("ores/silver");
        public static final TagKey<Item> SILVER_INGOT = forge("ingots/silver");
        public static final TagKey<Item> SILVER_NUGGET = forge("nuggets/silver");
        public static final TagKey<Item> RAWMEATS = forge("rawmeats");
        public static final TagKey<Item> COOKEDMEATS = forge("cookedmeats");
        public static final TagKey<Item> SILVER_TOOL = werewolves("tools/silver");
        public static final TagKey<Item> RAW_MATERIALS_SILVER = forge("raw_materials/iron");
        public static final TagKey<Item> STORAGE_BLOCKS_SILVER = forge("storage_blocks/silver");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_SILVER = forge("storage_blocks/raw_silver");
        public static final TagKey<Item> MAGIC_LOGS = forge("magic_logs");
        public static final TagKey<Item> JACARANDA_LOGS = forge("jacaranda_logs");

        private static TagKey<Item> mc(ResourceLocation id) {
            return ItemTags.create(id);
        }

        private static TagKey<Item> werewolves(String id) {
            return ItemTags.create(new ResourceLocation(REFERENCE.MODID, id));
        }

        private static TagKey<Item> forge(String id) {
            return ItemTags.create(new ResourceLocation("forge", id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_WEREWOLF_BIOME = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "is_werewolf_biome"));
    }
}
