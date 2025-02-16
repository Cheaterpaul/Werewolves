package de.teamlapen.werewolves.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.world.loot.functions.AddBookNbtFunction;
import de.teamlapen.vampirism.world.loot.functions.RefinementSetFunction;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.core.ModBlocks;
import de.teamlapen.werewolves.core.ModEntities;
import de.teamlapen.werewolves.core.ModItems;
import de.teamlapen.werewolves.core.ModLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTablesGenerator extends LootTableProvider {

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};


    public LootTablesGenerator(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(
                Pair.of(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                Pair.of(ModEntityLootTables::new, LootContextParamSets.ENTITY),
                Pair.of(InjectLootTables::new, LootContextParamSets.ENTITY),
                Pair.of(ChestLootTables::new, LootContextParamSets.CHEST)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationContext validationtracker) {
        map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationtracker, resourceLocation, lootTable));
    }

    private static class ModBlockLootTables extends BlockLoot {
        @Override
        protected void addTables() {
            this.add(ModBlocks.TOTEM_TOP_WEREWOLVES_WEREWOLF.get(), LootTable.lootTable());
            this.add(ModBlocks.TOTEM_TOP_WEREWOLVES_WEREWOLF_CRAFTED.get(), createSingleItemTable(de.teamlapen.vampirism.core.ModBlocks.TOTEM_TOP.get()));
            this.dropSelf(ModBlocks.JACARANDA_SAPLING.get());
            this.dropSelf(ModBlocks.MAGIC_SAPLING.get());
            this.dropSelf(ModBlocks.WOLFSBANE.get());
            this.dropSelf(ModBlocks.SILVER_BLOCK.get());
            this.dropSelf(ModBlocks.SILVER_ORE.get());
            this.dropSelf(ModBlocks.RAW_SILVER_BLOCK.get());
            this.dropPottedContents(ModBlocks.POTTED_WOLFSBANE.get());
            this.dropSelf(ModBlocks.JACARANDA_LOG.get());
            this.dropSelf(ModBlocks.MAGIC_LOG.get());
            this.dropSelf(ModBlocks.STONE_ALTAR.get());
            this.dropSelf(ModBlocks.MAGIC_PLANKS.get());
            this.add(ModBlocks.JACARANDA_LEAVES.get(), (block -> createLeavesDrops(block, ModBlocks.JACARANDA_SAPLING.get(), DEFAULT_SAPLING_DROP_RATES)));
            this.add(ModBlocks.MAGIC_LEAVES.get(), (block -> createLeavesDrops(block, ModBlocks.MAGIC_SAPLING.get(), DEFAULT_SAPLING_DROP_RATES)));
            this.dropSelf(ModBlocks.STONE_ALTAR_FIRE_BOWL.get());
            this.add(ModBlocks.SILVER_ORE.get(), (block) -> {
                return createOreDrop(block, ModItems.RAW_SILVER.get());
            });
            this.add(ModBlocks.DEEPSLATE_SILVER_ORE.get(), (block) -> {
                return createOreDrop(block, ModItems.RAW_SILVER.get());
            });
        }

        @Nonnull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.getAllBlocks();
        }
    }

    private static class ModEntityLootTables extends EntityLoot {
        private ModEntityLootTables() {
        }

        @Override
        protected void addTables() {
            this.add(ModEntities.TASK_MASTER_WEREWOLF.get(), LootTable.lootTable());
            this.add(ModEntities.WEREWOLF_MINION.get(), LootTable.lootTable());
            LootTable.Builder werewolf = LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .name("general")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.LIVER.get())))
                    .withPool(LootPool.lootPool()
                            .name("general2")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.CRACKED_BONE.get()).setWeight(40)))
                    .withPool(LootPool.lootPool()
                            .name("accessories")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceCondition.randomChance(0.05f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.BONE_NECKLACE.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.CHARM_BRACELET.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION)))
                            .add(LootItem.lootTableItem(ModItems.DREAM_CATCHER.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION))));
            this.add(ModEntities.WEREWOLF_SURVIVALIST.get(), werewolf);
            this.add(ModEntities.WEREWOLF_BEAST.get(), werewolf);
            this.add(ModEntities.HUMAN_WEREWOLF.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .name("general")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.LIVER.get())))
                    .withPool(LootPool.lootPool()
                            .name("general2")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.33f, 0.05f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.CRACKED_BONE.get()).setWeight(6)))
                    .withPool(LootPool.lootPool()
                            .name("hunter")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.1f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.V.VAMPIRE_BOOK.get()).setWeight(1)))
            );
            this.add(ModEntities.WOLF.get(), LootTable.lootTable());
            this.add(ModEntities.ALPHA_WEREWOLF.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .name("general")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .setRolls(UniformGenerator.between(1, 2))
                            .add(LootItem.lootTableItem(ModItems.WEREWOLF_TOOTH.get())))
                    .withPool(LootPool.lootPool()
                            .name("vampire_book")
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.1f))
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.V.VAMPIRE_BOOK.get()).apply(AddBookNbtFunction.builder()).setWeight(1))
                    )
            );
        }

        @Nonnull
        @Override
        protected Iterable<EntityType<?>> getKnownEntities() {
            return ModEntities.getAllEntities();
        }
    }

    private static class InjectLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(ModLootTables.villager, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("liver").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.LIVER.get()).setWeight(1).when(LootItemRandomChanceCondition.randomChance(0.5f)))));
            consumer.accept(ModLootTables.skeleton, LootTable.lootTable()
                    .withPool(LootPool.lootPool().name("bones").setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.CRACKED_BONE.get()).setWeight(1).when(LootItemRandomChanceCondition.randomChance(0.1f)))));
        }
    }

    private static class ChestLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            LootPool.Builder accessories = LootPool.lootPool()
                    .name("accessories")
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.BONE_NECKLACE.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION)))
                    .add(LootItem.lootTableItem(ModItems.CHARM_BRACELET.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION)))
                    .add(LootItem.lootTableItem(ModItems.DREAM_CATCHER.get()).setWeight(1).apply(RefinementSetFunction.builder(WReference.WEREWOLF_FACTION)));
            consumer.accept(ModLootTables.abandoned_mineshaft, LootTable.lootTable()
                    .withPool(accessories)
                    .withPool(LootPool.lootPool()
                            .name("main")
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.CRACKED_BONE.get()).setWeight(5))
                            .add(EmptyLootItem.emptyItem().setWeight(10)))
            );
            consumer.accept(ModLootTables.desert_pyramid, LootTable.lootTable()
                    .withPool(accessories)
                    .withPool(LootPool.lootPool()
                            .name("main")
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.LIVER.get()).setWeight(5))
                            .add(EmptyLootItem.emptyItem().setWeight(10)))
            );
            consumer.accept(ModLootTables.jungle_temple, LootTable.lootTable()
                    .withPool(accessories)
                    .withPool(LootPool.lootPool()
                            .name("main")
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.CRACKED_BONE.get()).setWeight(5))
                            .add(EmptyLootItem.emptyItem().setWeight(10)))
            );
            consumer.accept(ModLootTables.stronghold_corridor, LootTable.lootTable()
                    .withPool(accessories)
                    .withPool(LootPool.lootPool()
                            .name("main")
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ModItems.WEREWOLF_TOOTH.get()).setWeight(5))
                            .add(EmptyLootItem.emptyItem().setWeight(10)))
            );
            consumer.accept(ModLootTables.stronghold_library, LootTable.lootTable()
                    .withPool(accessories)
            );
        }
    }

}
