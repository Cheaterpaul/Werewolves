package de.teamlapen.werewolves.entities.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.werewolves.entities.IEntityFollower;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;
import java.util.Optional;

public class DefendLeaderGoal<T extends MobEntity & IEntityFollower> extends TargetGoal {

    private final T entity;
    private LivingEntity attacker;
    private int timestamp;

    public DefendLeaderGoal(T entity) {
        super(entity, false);
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Optional<IEntityLeader> leader = this.entity.getLeader();
        if (!leader.isPresent()) {
            return false;
        } else {
            this.attacker = leader.get().getRepresentingEntity().getLastHurtByMob();
            int i = leader.get().getRepresentingEntity().getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, EntityPredicate.DEFAULT);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        this.entity.getLeader().ifPresent(leader -> {
            this.timestamp = leader.getRepresentingEntity().getLastHurtByMobTimestamp();
        });
        super.start();
    }
}