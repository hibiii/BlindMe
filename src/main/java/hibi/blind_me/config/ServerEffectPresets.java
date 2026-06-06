package hibi.blind_me.config;

public enum ServerEffectPresets {
    OFF(1.25f, 5f, "options.off", "blindme.effect_description.off", false),
    BLINDNESS(1.25f, 5f, "effect.minecraft.blindness", "blindme.effect_description.blindness", true),
    DARKNESS(11.25f, 15f,  "effect.minecraft.darkness", "blindme.effect_description.darkness", true),
    ;

    public final String nameKey;
    public final String descriptionKey;
    public final float start;
    public final float end;
    public final boolean enabled;

    ServerEffectPresets(float start, float end, String nameKey, String descriptionKey, boolean enabled) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.start = start;
        this.end = end;
        this.enabled = enabled;
    }

    public static String matchNameKey(ServerEffect effect) {
        if (!effect.enabled()) {
            return OFF.nameKey;
        }
        for (ServerEffectPresets preset : ServerEffectPresets.values()) {
            if(effect.start() == preset.start && effect.end() == preset.end) {
                return preset.nameKey;
            }
        }
        return "effect.blindme.custom";
    }

    public boolean equals(ServerEffect effect) {
        return effect != null && effect.enabled() == this.enabled && effect.start() == this.start && effect.end() == this.end;
    }

    public static boolean equals(ServerEffectPresets preset, ServerEffect effect) {
        return preset != null && preset.equals(effect);
    }

    public ServerEffect toEffect() {
        return new ServerEffect(this.start, this.end, this.enabled);
    }
}
