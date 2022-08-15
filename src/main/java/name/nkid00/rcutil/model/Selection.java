package name.nkid00.rcutil.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class Selection {
    public boolean selected = false;
    public BlockPos lsbPos = null;
    public DimensionType lsbDimension = null;
    public BlockPos msbPos = null;
    public DimensionType msbDimension = null;

    public void selectMsb(BlockPos pos, DimensionType dimension) {
        this.msbPos = pos;
        this.msbDimension = dimension;
        if (this.lsbPos != null) {
            if (dimension != this.lsbDimension) {
                this.lsbPos = null;
                this.lsbDimension = null;
                this.selected = false;
            } else {
                this.selected = true;
            }
        }
    }

    public void selectLsb(BlockPos pos, DimensionType dimension) {
        this.lsbPos = pos;
        this.lsbDimension = dimension;
        if (this.msbPos != null) {
            if (dimension != this.msbDimension) {
                this.msbPos = null;
                this.msbDimension = null;
                this.selected = false;
            } else {
                this.selected = true;
            }
        }
    }
}
