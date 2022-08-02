package name.nkid00.rcutil.model.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.dimension.DimensionType;

public abstract class ComponentBuilder {
    @NotNull
    public String name = "";
    public DimensionType dimensionType = null;

    @Nullable
    public abstract Component build();
}
