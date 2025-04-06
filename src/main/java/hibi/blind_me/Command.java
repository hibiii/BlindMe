package hibi.blind_me;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import hibi.blind_me.config.ServerEffect;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public final class Command {
    private Command() {};

    public static void registerCallback(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(ClientCommandManager.literal("blindme")
            .then(ClientCommandManager.literal("off")
                .executes((src) -> Command.worldSubcommand(src, ServerEffect.OFF))
            )
            .then(ClientCommandManager.literal("blindness")
                .executes((src) -> Command.worldSubcommand(src, ServerEffect.BLINDNESS))
            )
            .then(ClientCommandManager.literal("darkness")
                .executes((src) -> Command.worldSubcommand(src, ServerEffect.DARKNESS))
            )
            .executes((src) -> Command.printSubcommand(src))
        );
    }

    private static int worldSubcommand(CommandContext<FabricClientCommandSource> cmd, ServerEffect ef) {
        String uniqueId = EffectManager.getUniqueId();
        if (uniqueId == null) {
            throw new IllegalStateException("Command called outside of a world");
        }
        if (Main.CONFIG.getEffectForServer(uniqueId) == ef) {
            cmd.getSource().sendFeedback(Text.translatable(K_EFFECT_ALREADY_SET));
            return 0;
        }
        Main.CONFIG.setEffectForServer(uniqueId, ef);
        cmd.getSource().sendFeedback(switch(ef) {
            case OFF -> Text.translatable(K_EFFECT_OFF);
            case BLINDNESS -> Text.translatable(K_EFFECT_SET, Text.translatable("effect.minecraft.blindness"));
            case DARKNESS -> Text.translatable(K_EFFECT_SET, Text.translatable("effect.minecraft.darkness"));
        });
        return 0;
    }

    private static int printSubcommand(CommandContext<FabricClientCommandSource> cmd) {
        String uniqueId = EffectManager.getUniqueId();
        cmd.getSource().sendFeedback(switch(Main.CONFIG.getEffectForServer(uniqueId)) {
            case BLINDNESS -> Text.translatable(K_PRINT_SET, Text.translatable("effect.minecraft.blindness"));
            case DARKNESS -> Text.translatable(K_PRINT_SET, Text.translatable("effect.minecraft.darkness"));
            case OFF -> Text.translatable(K_PRINT_NONE);
        });
        return 0;
    }

    private static final String
        K_EFFECT_ALREADY_SET = "blindme.command.effect_already_set",
        K_EFFECT_SET = "blindme.command.set_effect",
        K_EFFECT_OFF = "blindme.command.disable_effect",
        K_PRINT_SET = "blindme.command.current",
        K_PRINT_NONE = "blindme.command.current.none"
    ;
}
