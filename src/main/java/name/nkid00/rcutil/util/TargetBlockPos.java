package name.nkid00.rcutil.util;

import name.nkid00.rcutil.exception.BlockNotTargetException;
import name.nkid00.rcutil.helper.TargetBlockHelper;
import name.nkid00.rcutil.helper.WorldHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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

    public void check() throws BlockNotTargetException {
        if (!valid()) {
            throw new BlockNotTargetException();
        }
    }

    public void check(String message) throws BlockNotTargetException {
        if (!valid()) {
            throw new BlockNotTargetException(message);
        }
    }

    public void check(Text message) throws BlockNotTargetException {
        if (!valid()) {
            throw new BlockNotTargetException(message);
        }
    }

    public int read() throws BlockNotTargetException {
        return TargetBlockHelper.read(world, this);
    }

    public boolean readDigital() throws BlockNotTargetException {
        return TargetBlockHelper.readDigital(world, this);
    }

    public int readOrZero() {
        return TargetBlockHelper.readOrZero(world, this);
    }

    public boolean readDigitalOrZero() {
        return TargetBlockHelper.readDigitalOrZero(world, this);
    }

    public int readUnsafe() {
        return TargetBlockHelper.readUnsafe(world, this);
    }

    public boolean readDigitalUnsafe() {
        return TargetBlockHelper.readDigitalUnsafe(world, this);
    }

    public void write(int power) throws BlockNotTargetException {
        TargetBlockHelper.write(world, this, power);
    }

    public void writeDigital(boolean power) throws BlockNotTargetException {
        TargetBlockHelper.writeDigital(world, this, power);
    }

    public void writeSuppress(int power) {
        TargetBlockHelper.writeSuppress(world, this, power);
    }

    public void writeDigitalSuppress(boolean power) {
        TargetBlockHelper.writeDigitalSuppress(world, this, power);
    }

    public void writeUnsafe(int power) {
        TargetBlockHelper.writeUnsafe(world, this, power);
    }

    public void writeDigitalUnsafe(boolean power) {
        TargetBlockHelper.writeDigitalUnsafe(world, this, power);
    }

    @Override
    public int hashCode() {
        // some random prime number
        return super.hashCode() * 31 + (world == null ? 0 : WorldHelper.toString(world).hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) && obj instanceof TargetBlockPos) {
            var other = (TargetBlockPos) obj;
            if (world == null ? other.world != null : !world.equals(other.world)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "%s, %s, %s, %s".formatted(getX(), getY(), getZ(), WorldHelper.toString(world));
    }

    public BlockPosWithWorld toBlockPosWithWorld() {
        return new BlockPosWithWorld(this, world);
    }
}
