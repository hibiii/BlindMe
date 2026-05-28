package hibi.blind_me.config;

public record ServerEffect(float start, float end, String component, String subcomponent) {
    public static final ServerEffect
    OFF = new ServerEffect(0, 0, "options.off", "OFF"), // Represents no vision-impairing effects
    BLINDNESS = new ServerEffect(1.25f, 5f, "effect.minecraft.blindness", "BLINDNESS"),
    DARKNESS = new ServerEffect(11.25f, 15f,  "effect.minecraft.darkness", "DARKNESS");
    // Additionally in some contexts, `null` means deferral to a default effect

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ServerEffect other) {
            return this == other || this.start == other.start && this.end == other.end;
        }
        return false;
    }

    public ServerEffect(float start, float end) {
        this(start, end, "effect.blindme.custom", "CUSTOM");
    }
}
