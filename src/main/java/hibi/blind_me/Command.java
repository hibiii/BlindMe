package hibi.blind_me;

import org.quiltmc.qsl.command.api.EnumArgumentType;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import hibi.blind_me.config.Manager;
import hibi.blind_me.config.Enums.ServerEffect;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;

public final class Command {
    private Command() {};

    public static void registerCallback(CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext ctx, RegistrationEnvironment env) {
        dispatcher.register(ClientCommandManager.literal("blindme")
            .then(ClientCommandManager.argument("effect", new EnumArgumentType("off", "blindness", "darkness", "truly_blind"))
                .executes(Command::worldSubcommand)
            )
        );
    }

    private static int worldSubcommand(CommandContext<QuiltClientCommandSource> cmd) {
        ServerEffect ef = switch(cmd.getArgument("effect", String.class)) {
            case "off" -> ServerEffect.OFF;
            case "blindness" -> ServerEffect.BLINDNESS;
            case "darkness" -> ServerEffect.DARKNESS;
            case "truly_blind" -> ServerEffect.TRULY_BLIND;
            default -> throw new IllegalStateException("Unreachable code executed");
        };

        String uniqueId = EffectManager.getUniqueId();
        if (uniqueId == null) {
            throw new IllegalStateException("Command called outside of a world");
        }
        if (Manager.CONFIG.getEffectForServer(uniqueId) == ef) {
            cmd.getSource().sendFeedback(Text.translatable(K_EFFECT_ALREADY_SET));
            return 0;
        }
        Manager.CONFIG.setEffectForServer(uniqueId, ef);
        cmd.getSource().sendFeedback(switch(ef) {
            case OFF -> Text.translatable(K_EFFECT_OFF);
            case BLINDNESS -> Text.translatable(K_EFFECT_SET, Text.translatable("effect.minecraft.blindness"));
            case DARKNESS -> Text.translatable(K_EFFECT_SET, Text.translatable("effect.minecraft.darkness"));
            case TRULY_BLIND -> Text.translatable(K_EFFECT_SET, Text.translatable("effect.blindme.truly_blind"));
        });
        return 0;
    }

    private static final String
        K_EFFECT_ALREADY_SET = "blindme.command.effect_already_set",
        K_EFFECT_SET = "blindme.command.set_effect",
        K_EFFECT_OFF = "blindme.command.disable_effect"
    ;
}
