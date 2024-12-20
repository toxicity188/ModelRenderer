package kr.toxicity.model.api.tracker;

import kr.toxicity.model.api.ModelRenderer;
import kr.toxicity.model.api.data.renderer.RenderInstance;
import kr.toxicity.model.api.entity.TrackerMovement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public final class VoidTracker extends Tracker {
    private Location location;
    private final UUID uuid;
    public VoidTracker(@NotNull UUID uuid, @NotNull RenderInstance instance, @NotNull Location location) {
        super(() -> new TrackerMovement(new Vector3f(), new Vector3f(1), new Vector3f()), instance);
        this.uuid = uuid;
        this.location = location;
    }
    public void setLocation(Location location) {
        this.location = Objects.requireNonNull(location, "location");
        instance.teleport(location);
    }
    @Override
    public @NotNull Location location() {
        return location;
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    public void spawn(@NotNull Player player) {
        var bundler = ModelRenderer.inst().nms().createBundler();
        spawn(player, bundler);
        bundler.send(player);
    }
}
