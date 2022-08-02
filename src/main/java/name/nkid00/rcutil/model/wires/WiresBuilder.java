package name.nkid00.rcutil.model.wires;

import org.jetbrains.annotations.Nullable;

import name.nkid00.rcutil.model.component.ComponentBuilder;
import name.nkid00.rcutil.helper.BlockPosHelper;
import net.minecraft.util.math.BlockPos;

public class WiresBuilder extends ComponentBuilder {
    public BlockPos lsb = null;
    public BlockPos secondLsb = null;
    public BlockPos msb = null;

    @Override
    @Nullable
    public Wires build() {
        var it = BlockPosHelper.resolveBlockPos(lsb, secondLsb, msb);
        if (it == null) {
            return null;
        } else {
            var wires = new Wires();
            wires.name = name;
            wires.dimensionType = dimensionType;
            var iter = it.iterator();
            wires.base = iter.getPos();
            wires.gap = iter.getGap();
            wires.size = iter.getSize();
            wires.start();
            return wires;
        }
    }
}
