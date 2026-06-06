package hibi.blind_me.config;

public enum ServerEffectPresets {
    OFF(1.25f, 5f, 0xff000000, "options.off", "blindme.effect_description.off", false),
    BLINDNESS(1.25f, 5f, 0xff000000, "effect.minecraft.blindness", "blindme.effect_description.blindness", true),
    DARKNESS(11.25f, 15f, 0xff000000, "effect.minecraft.darkness", "blindme.effect_description.darkness", true),
    TRULY_BLIND(0.5f, 1.9f, 0xff000000, "effect.blindme.truly_blind", "blindme.effect_description.truly_blind", true),
    SILENT_HILL(2f, 7f, 0xff5a7075, "effect.blindme.silent_hill", "blindme.effect_description.silent_hill", true),
    ;

    public final String nameKey;
    public final String descriptionKey;
    public final float start;
    public final float end;
    public final int color;
    public final boolean enabled;

    ServerEffectPresets(float start, float end, int color, String nameKey, String descriptionKey, boolean enabled) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.start = start;
        this.end = end;
        this.color = color;
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
        return new ServerEffect(this.start, this.end, this.color, this.enabled);
    }
}
