package name.nkid00.rcutil.wires;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class WiresBuilder {
    public String name = null;

    public DimensionType dimensionType = null;
    public BlockPos lsb = null;
    public BlockPos secondLsb = null;
    public BlockPos msb = null;

    public Wires build() {
        var wires = new Wires();
        wires.dimensionType = dimensionType;
        return wires;
    }
}
