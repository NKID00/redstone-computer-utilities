package name.nkid00.rcutil.model;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class Selection {
    public boolean selected = false;
    public BlockPos lsb = null;
    public BlockPos msb = null;
    public ServerWorld world = null;

    public void selectMsb(BlockPos pos, ServerWorld world) {
        this.msb = pos;
        if (this.lsb != null) {
            if (world != this.world) {
                this.lsb = null;
                this.world = world;
                this.selected = false;
            } else {
                this.selected = true;
            }
        }
    }

    public void selectLsb(BlockPos pos, ServerWorld world) {
        this.lsb = pos;
        if (this.msb != null) {
            if (world != this.world) {
                this.msb = null;
                this.world = world;
                this.selected = false;
            } else {
                this.selected = true;
            }
        }
    }
}
