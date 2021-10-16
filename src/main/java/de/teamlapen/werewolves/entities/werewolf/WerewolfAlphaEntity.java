package de.teamlapen.werewolves.entities.werewolf;

import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.werewolves.core.ModBiomes;
import de.teamlapen.werewolves.util.WerewolfForm;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;

public class WerewolfAlphaEntity extends WerewolfBaseEntity implements IWerewolfAlpha {
    public static final int MAX_LEVEL = 4;
    private static final DataParameter<Integer> LEVEL = EntityDataManager.defineId(WerewolfAlphaEntity.class, DataSerializers.INT);


    public static boolean spawnPredicateAlpha(EntityType<? extends WerewolfAlphaEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return ModBiomes.WEREWOLF_HEAVEN_KEY.getRegistryName().equals(Helper.getBiomeId(world, blockPos)) && world.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && spawnPredicateWerewolf(entityType, world, spawnReason, blockPos, random);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder() //TODO values
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_BARON_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, 5);
    }

    private int followingEntities = 0;

    public WerewolfAlphaEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("level", getLevel());
    }

    @Override
    public void decreaseFollowerCount() {
        this.followingEntities = Math.max(0, this.followingEntities-1);
    }

    @Override
    public int getFollowingCount() {
        return this.followingEntities;
    }

    @Override
    public void setLevel(int level) {
        level = MathHelper.clamp(level,0,MAX_LEVEL);
        if (level >= 0) {
            getEntityData().set(LEVEL,level);
            this.updateEntityAttributes();
            this.setCustomName(getTypeName().plainCopy().append(new TranslationTextComponent("entity.werewolves.alpha_werewolf.level", level + 1)));
        } else {
            this.setCustomName(null);
        }
    }

    @Override
    public int getLevel() {
        return getEntityData().get(LEVEL);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getMaxHeadXRot() {
        return 5;
    }

    @Override
    public int getMaxHeadYRot() {
        return 5;
    }

    @Override
    public int getMaxFollowerCount() {
        return (int)((BalanceMobProps.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER) * ((this.getLevel()+1)/(float)(this.getMaxLevel()+1)));
    }

    @Override
    public int getPortalWaitTime() {
        return 500;
    }

    @Override
    public boolean increaseFollowerCount() {
        if (this.followingEntities < getMaxFollowerCount()) {
            this.followingEntities++;
            return true;
        }
        return false;
    }

    @Override
    public void killed(ServerWorld p_241847_1_, LivingEntity p_241847_2_) {
        super.killed(p_241847_1_, p_241847_2_);
        if (p_241847_2_ instanceof WerewolfAlphaEntity) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        setLevel(nbt.getInt("level"));
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    public int suggestLevel(Difficulty d) {
        int avg = Math.round(((d.avgPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int max = Math.round(((d.maxPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * MAX_LEVEL);
        int min = Math.round(((d.minPercLevel) / 100F - 5 / 14F) / (1F - 5 / 14F) * (MAX_LEVEL));

        switch (random.nextInt(7)) {
            case 0:
                return min;
            case 1:
                return max + 1;
            case 2:
                return avg;
            case 3:
                return avg + 1;
            case 4:
            case 5:
                return random.nextInt(MAX_LEVEL + 1);
            default:
                return random.nextInt(max + 2 - min) + min;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(LEVEL, -1);
    }

    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 20 + 5 * getLevel();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, PlayerEntity.class, 6.0F, 0.6, 0.7F, input -> input != null && !isLowerLevel(input)));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 0.2));
        this.goalSelector.addGoal(9, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isLowerLevel));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, WerewolfAlphaEntity.class, true, false));
    }

    private boolean isLowerLevel(LivingEntity player) {
        if (player instanceof PlayerEntity) {
            int playerLevel = FactionPlayerHandler.getOpt((PlayerEntity) player).map(FactionPlayerHandler::getCurrentLevel).orElse(0);
            return (playerLevel - 8) / 2F - this.getLevel() <= 0;
        }
        return false;
    }

    @Nonnull
    @Override
    public WerewolfForm getForm() {
        return WerewolfForm.BEAST4L;
    }

    @Override
    public int getSkinType() {
        return 0;
    }

    @Override
    public int getEyeType() {
        return 0;
    }

    @Override
    public boolean hasGlowingEyes() {
        return false;
    }

    protected void updateEntityAttributes() { //TODO different values
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(20D);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_MOVEMENT_SPEED * Math.pow((BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL - 1) / 5 + 1, (getLevel())));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_MAX_HEALTH * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_BARON_ATTACK_DAMAGE * Math.pow(BalanceMobProps.mobProps.VAMPIRE_BARON_IMPROVEMENT_PER_LEVEL, getLevel()));
    }
}