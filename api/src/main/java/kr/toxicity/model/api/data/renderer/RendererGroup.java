package kr.toxicity.model.api.data.renderer;

import kr.toxicity.model.api.ModelRenderer;
import kr.toxicity.model.api.data.blueprint.BlueprintChildren;
import kr.toxicity.model.api.entity.EntityMovement;
import kr.toxicity.model.api.entity.RenderedEntity;
import kr.toxicity.model.api.nms.ModelDisplay;
import kr.toxicity.model.api.util.MathUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class RendererGroup {

    @Getter
    private final String name;
    @Getter
    private final BlueprintChildren.BlueprintGroup parent;
    @Getter
    private final float scale;
    private final Vector3f position;
    private final Vector3f rotation;
    private final ItemStack itemStack;
    private final Map<String, RendererGroup> children;
    private final Function<Location, ModelDisplay> displayFunction;

    public RendererGroup(
            @NotNull String name,
            float scale,
            @Nullable ItemStack itemStack,
            @NotNull BlueprintChildren.BlueprintGroup group,
            @NotNull Map<String, RendererGroup> children
    ) {
        this.name = name;
        this.scale = scale;
        this.parent = group;
        this.children = children;
        this.itemStack = itemStack;
        position = MathUtil.blockBenchToDisplay(group.origin().toVector()
                .div(16));
        rotation = group.rotation().toVector();
        if (itemStack != null) {
            displayFunction = l -> {
                var display = ModelRenderer.inst().nms().create(l);
                display.item(parent.visibility() ? itemStack : new ItemStack(Material.AIR));
                return display;
            };
        } else {
            displayFunction = l -> null;
        }
    }

    public @NotNull RenderedEntity create(@NotNull Location location) {
        return create(null, location);
    }
    private @NotNull RenderedEntity create(@Nullable RenderedEntity entityParent, @NotNull Location location) {
        var entity = new RenderedEntity(
                this,
                entityParent,
                displayFunction,
                location,
                new EntityMovement(
                        entityParent != null ? new Vector3f(position).sub(entityParent.getGroup().position) : position,
                        new Vector3f(1),
                        MathUtil.toQuaternion(MathUtil.blockBenchToDisplay(rotation)),
                        rotation
                )
        );
        entity.setChildren(children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().create(entity, location))));
        return entity;
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack != null ? itemStack.clone() : new ItemStack(Material.AIR);
    }
}
