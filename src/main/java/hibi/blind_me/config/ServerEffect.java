package hibi.blind_me.config;

public record ServerEffect(float start, float end, int color, boolean enabled) {
    // In some contexts, `null` means deferral to a default effect

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ServerEffect other) {
            return this == other || this.start == other.start && this.end == other.end && this.color == other.color;
        }
        return false;
    }

    public ServerEffect(float start, float end, int color) {
        this(start, end, color, true);
    }

    public ServerEffect setEnabled(boolean enabled) {
        return new ServerEffect(this.start, this.end, this.color, enabled);
    }

    public ServerEffect setStart(float start) {
        return new ServerEffect(start, this.end, this.color, this.enabled);
    }

    public ServerEffect setEnd(float end) {
        return new ServerEffect(this.start, end, this.color, this.enabled);
    }

    public ServerEffect setColor(int color) {
        return new ServerEffect(this.start, this.end, color, this.enabled);
    }
}
