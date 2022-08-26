package name.nkid00.rcutil.util;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.helper.WorldHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class TargetBlockPos extends BlockPos {
    private final ServerWorld world;

    public TargetBlockPos(ServerWorld world, BlockPos pos) {
        this(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public TargetBlockPos(ServerWorld world, Vec3i vec3i) {
        this(world, vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public TargetBlockPos(ServerWorld world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    public TargetBlockPos copy() {
        return new TargetBlockPos(this.world(), this);
    }

    public ServerWorld world() {
        return world;
    }

    public boolean valid() {
        return TargetBlockHelper.is(world, this);
    }

    public int read() throws BlockNotTargetException {
        return TargetBlockHelper.read(world, this);
    }

    public int readOrZero() {
        try {
            return read();
        } catch (BlockNotTargetException e) {
            return 0;
        }
    }

    public boolean readDigital() throws BlockNotTargetException {
        return TargetBlockHelper.readDigital(world, this);
    }

    public boolean readDigitalOrZero() {
        try {
            return readDigital();
        } catch (BlockNotTargetException e) {
            return false;
        }
    }

    public void write(int power) throws BlockNotTargetException {
        TargetBlockHelper.write(world, this, power);
    }

    public void writeSuppress(int power) {
        try {
            write(power);
        } catch (BlockNotTargetException e) {
        }
    }

    public void writeDigital(boolean power) throws BlockNotTargetException {
        TargetBlockHelper.writeDigital(world, this, power);
    }

    public void writeDigitalSuppress(boolean power) {
        try {
            writeDigital(power);
        } catch (BlockNotTargetException e) {
        }
    }

    public String toString() {
        return "%s, %s, %s, %s".formatted(getX(), getY(), getZ(), WorldHelper.toString(world));
    }
}
