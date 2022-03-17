package de.teamlapen.werewolves.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.gui.MinionStatsScreen;
import de.teamlapen.werewolves.entities.minion.WerewolfMinionEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;

public class WerewolfMinionStatsScreen extends MinionStatsScreen<WerewolfMinionEntity.WerewolfMinionData, WerewolfMinionEntity> {

    private final TranslatableComponent inventoryLevel = new TranslatableComponent("text.vampirism.minion.stats.inventory_level");
    private final TranslatableComponent healthLevel = new TranslatableComponent(Attributes.MAX_HEALTH.getDescriptionId());
    private final TranslatableComponent strengthLevel = new TranslatableComponent(Attributes.ATTACK_DAMAGE.getDescriptionId());
    private final TranslatableComponent resourceLevel = new TranslatableComponent("text.vampirism.minion.stats.resource_level");

    public WerewolfMinionStatsScreen(WerewolfMinionEntity entity, @Nullable Screen backScreen) {
        super(entity, 4, backScreen);
    }

    @Override
    protected boolean areButtonsVisible(WerewolfMinionEntity.WerewolfMinionData data) {
        return data.getRemainingStatPoints() > 0 || data.getLevel() < WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL;
    }

    @Override
    protected int getRemainingStatPoints(WerewolfMinionEntity.WerewolfMinionData data) {
        return data.getRemainingStatPoints();
    }

    @Override
    protected boolean isActive(WerewolfMinionEntity.WerewolfMinionData data, int i) {
        switch (i) {
            case 0:
                return data.getRemainingStatPoints() > 0 && data.getInventoryLevel() < WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_INVENTORY;
            case 1:
                return data.getRemainingStatPoints() > 0 && data.getHealthLevel() < WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_HEALTH;
            case 2:
                return data.getRemainingStatPoints() > 0 && data.getStrengthLevel() < WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_STRENGTH;
            case 3:
                return data.getRemainingStatPoints() > 0 && data.getResourceEfficiencyLevel() < WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_RESOURCES;
            default:
                return false;
        }
    }

    @Override
    protected void renderStats(PoseStack mStack, WerewolfMinionEntity.WerewolfMinionData data) {
        renderLevelRow(mStack, data.getLevel() + 1, WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL + 1);
        renderStatRow(mStack, 0, this.inventoryLevel, new TextComponent("" + data.getInventorySize()), data.getInventoryLevel() + 1, WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_INVENTORY + 1);
        renderStatRow(mStack, 1, this.healthLevel, new TextComponent("" + entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue()), data.getHealthLevel() + 1, WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_HEALTH + 1);
        renderStatRow(mStack, 2, this.strengthLevel, new TextComponent("" + entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()), data.getStrengthLevel() + 1, WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_STRENGTH + 1);
        renderStatRow(mStack, 3, this.resourceLevel, new TextComponent("" + (int) (Math.ceil((float) (data.getResourceEfficiencyLevel() + 1) / (WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_RESOURCES + 1) * 100)) + "%"), data.getResourceEfficiencyLevel() + 1, WerewolfMinionEntity.WerewolfMinionData.MAX_LEVEL_RESOURCES + 1);

    }
}
