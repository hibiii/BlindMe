package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import hibi.blind_me.config.ConfigScreen;
import hibi.blind_me.config.ServerEffect;
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
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.OFF))
                )
                .then(ClientCommands.literal("blindness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.BLINDNESS))
                )
                .then(ClientCommands.literal("darkness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.DARKNESS))
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

    private static int worldSubcommand(CommandContext<FabricClientCommandSource> cmd, @Nullable ServerEffect ef) {
        String uniqueId = Networking.uniqueId;
        if (uniqueId == null) {
            throw new IllegalStateException("Command called outside of a world");
        }
        var opts = Networking.getServerOptions();
        if (opts.locked()) {
            cmd.getSource().sendFeedback(Component.translatable(K_OPTIONS_LOCKED).withStyle(ChatFormatting.RED));
            return 0;
        }
        if (opts.effect() == ef) {
            cmd.getSource().sendFeedback(Component.translatable(
                (ef == null) ? K_EFFECT_ALREADY_UNSET : K_EFFECT_ALREADY_SET
            ));
            return 0;
        }
        Main.CONFIG.setEffectForServer(uniqueId, ef);
        Component feedback;
        if (ef == null) {
            feedback = Component.translatable(K_EFFECT_UNSET);
        } else {
            if (ef == ServerEffect.OFF) {
                feedback = Component.translatable(K_EFFECT_OFF);
            } else {
                feedback = Component.translatable(K_EFFECT_SET, Component.translatable(ef.component()));
            }
        }
        cmd.getSource().sendFeedback(feedback);
        return 0;
    }

    private static int printSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        String uniqueId = Networking.uniqueId;
        ServerEffect ef = Main.CONFIG.getEffectForServer(uniqueId);
        Component feedback;
        if (ef == ServerEffect.OFF) {
            feedback = Component.translatable(K_PRINT_NONE);
        } else {
            feedback = Component.translatable(K_PRINT_SET, Component.translatable(ef.component()));
        }
        cmd.getSource().sendFeedback(feedback);
        return 0;
    }

    private static int settingsSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        var client = Minecraft.getInstance();
        client.schedule(() -> {
            client.gui.setScreen(new ConfigScreen(null));
        });
        return 0;
    }

    private static final String
        K_EFFECT_ALREADY_SET = "blindme.command.effect_already_set",
        K_EFFECT_ALREADY_UNSET = "blindme.command.effect_already_unset",
        K_EFFECT_SET = "blindme.command.set_effect",
        K_EFFECT_OFF = "blindme.command.disable_effect",
        K_EFFECT_UNSET = "blindme.command.unset_effect",
        K_OPTIONS_LOCKED = "blindme.command.options_locked",
        K_PRINT_SET = "blindme.command.current",
        K_PRINT_NONE = "blindme.command.current.none"
    ;
}
