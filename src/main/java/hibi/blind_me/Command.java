package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import hibi.blind_me.config.ConfigScreenFactory;
import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.config.ServerEffectPresets;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public final class Command {
    private Command() {};

    public static void registerCallback(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext access) {
        dispatcher.register(ClientCommands.literal("blindme")
            .then(ClientCommands.literal("set")
                .then(ClientCommands.literal("off")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffectPresets.OFF))
                )
                .then(ClientCommands.literal("blindness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffectPresets.BLINDNESS))
                )
                .then(ClientCommands.literal("darkness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffectPresets.DARKNESS))
                )
                .then(ClientCommands.literal("truly_blind")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffectPresets.TRULY_BLIND))
                )
                .then(ClientCommands.literal("silent_hill")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffectPresets.SILENT_HILL))
                )
                .then(ClientCommands.literal("default")
                    .executes((src) -> Command.worldSubcommand(src, null))
                )
            )
            .then(ClientCommands.literal("query")
                .executes(Command::printSubcommand)
            )
            .executes(Command::settingsSubcommand)
        );
    }

    private static int worldSubcommand(CommandContext<FabricClientCommandSource> cmd, @Nullable ServerEffectPresets newPre) {
        String uniqueId = Networking.uniqueId;
        if (uniqueId == null) {
            throw new IllegalStateException("Command called outside of a world");
        }
        var opts = Networking.getServerOptions();
        if (opts.locked()) {
            cmd.getSource().sendFeedback(Component.translatable(K_OPTIONS_LOCKED).withStyle(ChatFormatting.RED));
            return 0;
        }
        Component feedback;
        var oldEf = opts.effect();
        var newEf = oldEf;
        if (newPre == null) {
            if (oldEf == null) {
                feedback = Component.translatable(K_EFFECT_ALREADY_UNSET);
            } else {
                newEf = null;
                feedback = Component.translatable(K_EFFECT_UNSET);
            }
        } else if (newPre == ServerEffectPresets.OFF) {
            if (oldEf != null && !oldEf.enabled()) {
                feedback = Component.translatable(K_EFFECT_ALREADY_OFF);
            } else {
                newEf = ServerEffectPresets.OFF.toEffect();
                feedback = Component.translatable(K_EFFECT_OFF);
            }
        } else {
            if (newPre.equals(oldEf)) {
                feedback = Component.translatable(K_EFFECT_ALREADY_SET);
            } else {
                newEf = newPre.toEffect();
                feedback = Component.translatable(K_EFFECT_SET, Component.translatable(newPre.nameKey));
            }
        }
        Main.CONFIG.setEffectForServer(uniqueId, newEf);
        cmd.getSource().sendFeedback(feedback);
        return 0;
    }

    private static int printSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        String uniqueId = Networking.uniqueId;
        ServerEffect ef = Main.CONFIG.getServerOptions(uniqueId).effect();
        Component feedback;
        if (ef == null) {
            feedback = Component.translatable(K_PRINT_SET, Component.translatable("effect.blindme.default"));
        } else if (!ef.enabled()) {
            feedback = Component.translatable(K_PRINT_NONE);
        } else {
            feedback = Component.translatable(K_PRINT_SET, Component.translatable(ServerEffectPresets.matchNameKey(ef)));
        }
        cmd.getSource().sendFeedback(feedback);
        return 0;
    }

    private static int settingsSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        var client = Minecraft.getInstance();
        client.schedule(() -> {
            client.gui.setScreen(ConfigScreenFactory.create(null, true, Networking.uniqueId));
        });
        return 0;
    }

    private static final String
        K_EFFECT_ALREADY_SET = "blindme.command.effect_already_set",
        K_EFFECT_ALREADY_OFF = "blindme.command.effect_already_disabled",
        K_EFFECT_ALREADY_UNSET = "blindme.command.effect_already_unset",
        K_EFFECT_SET = "blindme.command.set_effect",
        K_EFFECT_OFF = "blindme.command.disable_effect",
        K_EFFECT_UNSET = "blindme.command.unset_effect",
        K_OPTIONS_LOCKED = "blindme.command.options_locked",
        K_PRINT_SET = "blindme.command.current",
        K_PRINT_NONE = "blindme.command.current.none"
    ;
}
