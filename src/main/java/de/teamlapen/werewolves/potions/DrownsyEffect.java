package de.teamlapen.werewolves.potions;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.config.WerewolvesConfig;
import de.teamlapen.werewolves.core.WEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

import javax.annotation.Nonnull;

public class DrownsyEffect extends WerewolvesEffect{

    private static final String REGNAME = "drownsy";

    public DrownsyEffect() {
        super(REGNAME, EffectType.HARMFUL, 0xe012ef);
    }

    public static void addDrownsyPotion(PlayerEntity playerEntity) {
        if(FactionPlayerHandler.getOpt(playerEntity).map(player -> player.canJoin(WReference.WEREWOLF_FACTION)).orElse(false)) {
            playerEntity.addPotionEffect(new EffectInstance(WEffects.drownsy, WerewolvesConfig.BALANCE.DROWNSYTIME.get() * 1200));
        }
    }

    @Override
    public void performEffect(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if(entityLivingBaseIn instanceof PlayerEntity) {
            //TODO become werewolf
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration == 1;
    }
}
