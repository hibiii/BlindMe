package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import hibi.blind_me.config.ConfigScreen;
import hibi.blind_me.config.ServerEffect;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public final class Command {
    private Command() {};

    public static void registerCallback(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext access) {
        dispatcher.register(ClientCommandManager.literal("blindme")
            .then(ClientCommandManager.literal("set")
                .then(ClientCommandManager.literal("off")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.OFF))
                )
                .then(ClientCommandManager.literal("blindness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.BLINDNESS))
                )
                .then(ClientCommandManager.literal("darkness")
                    .executes((src) -> Command.worldSubcommand(src, ServerEffect.DARKNESS))
                )
                .then(ClientCommandManager.literal("default")
                    .executes((src) -> Command.worldSubcommand(src, null))
                )
            )
            .then(ClientCommandManager.literal("query")
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
        if (Networking.serverEnforced && !Networking.isOpForBypass) {
            cmd.getSource().sendFeedback(Component.translatable(K_ENFORCED_BY_SERVER).withStyle(ChatFormatting.RED));
            return 0;
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
        cmd.getSource().sendFeedback(switch(ef) {
            case OFF -> Component.translatable(K_EFFECT_OFF);
            case BLINDNESS -> Component.translatable(K_EFFECT_SET, Component.translatable("effect.minecraft.blindness"));
            case DARKNESS -> Component.translatable(K_EFFECT_SET, Component.translatable("effect.minecraft.darkness"));
            case null -> Component.translatable(K_EFFECT_UNSET);
        });
        return 0;
    }

    private static int printSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        String uniqueId = Networking.uniqueId;
        cmd.getSource().sendFeedback(switch(Main.CONFIG.getEffectForServer(uniqueId)) {
            case BLINDNESS -> Component.translatable(K_PRINT_SET, Component.translatable("effect.minecraft.blindness"));
            case DARKNESS -> Component.translatable(K_PRINT_SET, Component.translatable("effect.minecraft.darkness"));
            case OFF -> Component.translatable(K_PRINT_NONE);
        });
        return 0;
    }

    private static int settingsSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        var client = Minecraft.getInstance();
        client.schedule(() -> {
            client.setScreen(new ConfigScreen(null));
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
        K_ENFORCED_BY_SERVER = "blindme.command.enforced_by_server",
        K_PRINT_SET = "blindme.command.current",
        K_PRINT_NONE = "blindme.command.current.none"
    ;
}
