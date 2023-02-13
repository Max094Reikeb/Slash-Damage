package net.reikeb.slash_damage.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.reikeb.slash_damage.DamageSourceManager;

import java.util.Collection;

public class DamageCommand {

    private static final DynamicCommandExceptionType ERROR_PLAYER = new DynamicCommandExceptionType((error) -> Component.translatable("command.slash_damage.null_player", error));

    public static final SuggestionProvider<CommandSourceStack> AVAILABLE_DAMAGE_SOURCES = SuggestionProviders.register(
            new ResourceLocation("available_damage_sources"), (context, builder) ->
                    SharedSuggestionProvider.suggest(DamageSourceManager.DAMAGES_SOURCES, builder)
    );

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("damage").requires((commandSource)
                        -> commandSource.hasPermission(2))
                .then(Commands.argument("target", GameProfileArgument.gameProfile())
                        .then(Commands.argument("damage_source", ResourceLocationArgument.id()).suggests(AVAILABLE_DAMAGE_SOURCES)
                                .then(Commands.argument("damage", IntegerArgumentType.integer())
                                        .then(Commands.argument("bypassArmor", BoolArgumentType.bool()).executes((command)
                                                -> damagePlayer(command.getSource(),
                                                GameProfileArgument.getGameProfiles(command, "target"),
                                                ResourceLocationArgument.getId(command, "damage_source"),
                                                IntegerArgumentType.getInteger(command, "damage"),
                                                BoolArgumentType.getBool(command, "bypassArmor"))
                                        ))))
                ));
    }

    private static int damagePlayer(CommandSourceStack source, Collection<GameProfile> target, ResourceLocation damageSource, int damage, boolean bypassArmor) throws CommandSyntaxException {
        for (GameProfile gameProfile : target) {
            ServerPlayer serverPlayer = source.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer == null) throw ERROR_PLAYER.create(gameProfile);

            DamageSource newDamageSource = (bypassArmor ? new DamageSource(damageSource.getPath()).bypassArmor() : new DamageSource(damageSource.getPath()));

            serverPlayer.hurt(newDamageSource, damage);
            source.sendSuccess(Component.translatable("command.slash_damage.damaged", serverPlayer.getDisplayName().getString()), true);
        }
        return source.hashCode();
    }
}
