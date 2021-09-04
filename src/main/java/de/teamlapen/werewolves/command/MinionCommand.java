package de.teamlapen.werewolves.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import de.teamlapen.werewolves.core.ModEntities;
import de.teamlapen.werewolves.entities.minion.WerewolfMinionEntity;
import de.teamlapen.werewolves.util.WReference;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;

public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType fail = new DynamicCommandExceptionType((msg) -> new StringTextComponent("Failed: " + msg));

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("spawnNew")
                        .then(Commands.literal("werewolf").executes(context -> spawnNewWerewolfMinion(context.getSource(), "Minion", -1, false))
                )
                .then(Commands.literal("recall").executes(context -> recall(context.getSource())))
                .then(Commands.literal("respawnAll").executes(context -> respawn(context.getSource())))
                .then(Commands.literal("purge").executes(context -> purge(context.getSource())))
                .executes(context -> 0));
    }


    private static int spawnNewWerewolfMinion(CommandSource ctx, String name, int type, boolean useLordSkin) throws CommandSyntaxException {
        WerewolfMinionEntity.WerewolfMinionData data = new WerewolfMinionEntity.WerewolfMinionData(name);
        return spawnNewMinion(ctx, WReference.WEREWOLF_FACTION, data, ModEntities.werewolf_minion);
    }

    private static <T extends MinionData> int spawnNewMinion(CommandSource ctx, IPlayableFaction<?> faction, T data, EntityType<? extends MinionEntity<T>> type) throws CommandSyntaxException {
        PlayerEntity p = ctx.getPlayerOrException();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            if (controller.hasFreeMinionSlot()) {

                if (fph.getCurrentFaction() == faction) {
                    int id = controller.createNewMinionSlot(data, type);
                    if (id < 0) {
                        throw fail.create("Failed to get new minion slot");
                    }
                    controller.createMinionEntityAtPlayer(id, p);
                } else {
                    throw fail.create("Wrong faction");
                }


            } else {
                throw fail.create("No free slot");
            }

        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }

    private static int recall(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.getPlayerOrException();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            Collection<Integer> ids = controller.recallMinions(true);
            for (Integer id : ids) {
                controller.createMinionEntityAtPlayer(id, p);
            }
        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }


    private static int respawn(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.getPlayerOrException();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            Collection<Integer> ids = controller.getUnclaimedMinions();
            for (Integer id : ids) {
                controller.createMinionEntityAtPlayer(id, p);
            }

        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }

    private static int purge(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.getPlayerOrException();
        MinionWorldData.getData(ctx.getServer()).purgeController(p.getUUID());
        p.displayClientMessage(new StringTextComponent("Reload world"), false);
        return 0;
    }
}
