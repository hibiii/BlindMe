package hibi.blind_me.config;

public record ServerEffect(float start, float end, boolean enabled) {
    // In some contexts, `null` means deferral to a default effect

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ServerEffect other) {
            return this == other || this.start == other.start && this.end == other.end;
        }
        return false;
    }

    public ServerEffect(float start, float end) {
        this(start, end, true);
    }

    public ServerEffect setEnabled(boolean enabled) {
        return new ServerEffect(this.start, this.end, enabled);
    }

    public ServerEffect setStart(Float start) {
        return new ServerEffect(start, this.end, this.enabled);
    }

    public ServerEffect setEnd(Float end) {
        return new ServerEffect(this.start, end, this.enabled);
    }
}
