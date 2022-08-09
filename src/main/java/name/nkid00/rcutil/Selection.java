package name.nkid00.rcutil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class Selection {
    public String name;
    public boolean selected = false;
    public BlockPos LsbPos = null;
    public DimensionType LsbDimension = null;
    public BlockPos MsbPos = null;
    public DimensionType MsbDimension = null;

    public void selectMsb(BlockPos pos, DimensionType dimension) {
        this.MsbPos = pos;
        this.MsbDimension = dimension;
        if (this.LsbPos != null) {
            if (dimension != this.LsbDimension) {
                this.LsbPos = null;
                this.LsbDimension = null;
                this.selected = false;
            } else {
                this.selected = true;
            }
        }
    }

    public void selectLsb(BlockPos pos, DimensionType dimension) {
        // this.LsbPos = pos;
        // this.LsbDimension = dimension;
        // if (this.MsbPos != null) {
        //     if (dimension != this.MsbDimension) {
        //         this.MsbPos = null;
        //         this.MsbDimension = null;
        //         this.selected = false;
        //     } else {
        //         this.selected = true;
        //     }
        // }
    }
}
