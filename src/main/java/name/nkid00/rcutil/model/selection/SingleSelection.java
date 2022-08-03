package name.nkid00.rcutil.model.selection;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SingleSelection extends Selection {
    @Override
    public void selectMsb(BlockPos pos, DimensionType dimension) {
        this.MsbPos = pos;
        this.MsbDimension = dimension;
        this.LsbPos = pos;
        this.LsbDimension = dimension;
        this.selected = true;
    }

    @Override
    public void selectLsb(BlockPos pos, DimensionType dimension) {
        this.selectMsb(pos, dimension);
    }
}
