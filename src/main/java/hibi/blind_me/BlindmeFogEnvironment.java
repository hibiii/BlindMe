package hibi.blind_me;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class BlindmeFogEnvironment extends FogEnvironment {

    private boolean enabled = false;
    private int color = 0xFF000000;
    private float envEnd = 5;
    private float envStart = 4;

    @Override
    public void setupFog(FogData fog, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker) {
        fog.environmentalEnd = this.envEnd;
        fog.environmentalStart = this.envStart;
        fog.skyEnd = this.envEnd;
        fog.cloudEnd = this.envEnd;
    }

    @Override
    public int getBaseColor(ClientLevel level, Camera camera, int renderDistance, float partialTicks) {
        return this.color;
    }

    @Override
    public boolean isApplicable(@Nullable FogType fogType, Entity entity) {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProperties(float start, float end, int color) {
        this.envEnd = end;
        this.envStart = start;
        this.color = color;
    }
    
    private static final BlindmeFogEnvironment instance = new BlindmeFogEnvironment();

    public static BlindmeFogEnvironment getInstance() {
        return instance;
    }
}
