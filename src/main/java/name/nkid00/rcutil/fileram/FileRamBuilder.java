package name.nkid00.rcutil.fileram;

import java.io.File;
import java.io.IOException;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;
import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.RCUtil;
import name.nkid00.rcutil.enumeration.FileRamFileEndianness;
import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamType;
import name.nkid00.rcutil.exception.BlockNotRedstoneWireException;
import name.nkid00.rcutil.exception.OversizedException;

public class FileRamBuilder {
    public String name = null;
    public MutableText fancyName = null;

    public FileRamType type = null;

    public DimensionType dimensionType = null;

    public BlockPos addrLsb = null;
    public BlockPos addr2Lsb = null;
    public BlockPos addrMsb = null;

    public BlockPos dataLsb = null;
    public BlockPos data2Lsb = null;
    public BlockPos dataMsb = null;

    public BlockPos clock = null;

    public FileRamEdgeTriggering clockEdgeTriggering = null;

    public String filename = null;
    public File file = null;
    public FileRamFileEndianness fileEndianness = null;

    public FileRam fileRam = null;

    public enum BuildStatus {
        Success,
        FailedNotAligned,
        WarningNotAligned
    }

    public boolean setFile(String filename) throws IOException {
        this.filename = filename;
        file = new File(RCUtil.fileRamBaseDirectory, filename);
        if (type == FileRamType.ReadOnly && !file.exists()) {
            return false;
        }
        return true;
    }

    public void buildFancyName() {
        TranslatableText typeText = type.toText(), edgeText = clockEdgeTriggering.toText(), endiannessText = fileEndianness.toText();
        fancyName = new TranslatableText("rcutil.fileram.fancyname.builder", name, typeText, edgeText, filename, endiannessText);
    }

    public BuildStatus buildAddress(ServerWorld world) throws BlockNotRedstoneWireException, OversizedException {
        if (!MathUtil.onSameLine(addrLsb, addr2Lsb, addrMsb)) {
            return BuildStatus.FailedNotAligned;
        }

        fileRam = new FileRam();
        fileRam.type = type;

        fileRam.addrBase = addrLsb;
        fileRam.addrGap = MathUtil.getOffset(addrLsb, addr2Lsb);  // including one end
        Vec3i addr = MathUtil.getOffset(addrLsb, addrMsb);
        float size = 1F;
        if (addr.getX() != fileRam.addrGap.getX()) {
            size = ((float)addr.getX()) / fileRam.addrGap.getX() + 1;
        } else if (addr.getY() != fileRam.addrGap.getY()) {
            size = ((float)addr.getY()) / fileRam.addrGap.getY() + 1;
        } else if (addr.getZ() != fileRam.addrGap.getZ()) {
            size = ((float)addr.getZ()) / fileRam.addrGap.getZ() + 1;
        }
        fileRam.addrSize = MathUtil.float2Int(size);

        if (fileRam.addrSize > 64) {
            throw new OversizedException();
        }

        // test if there is non-redstone-wire block in the bus
        BlockPos blockPos = fileRam.nAddrBlockPos(2);
        for (int i = 2; i < size; i++) {
            if (!world.getBlockState(blockPos).isOf(Blocks.REDSTONE_WIRE)) {
                throw new BlockNotRedstoneWireException();
            }
            blockPos = MathUtil.applyOffset(blockPos, fileRam.addrGap);
        }

        if (!MathUtil.isFloatIntegral(size)) {
            return BuildStatus.WarningNotAligned;
        }

        return BuildStatus.Success;
    }

    public BuildStatus buildData(ServerWorld world) throws BlockNotRedstoneWireException, OversizedException {
        if (!MathUtil.onSameLine(dataLsb, data2Lsb, dataMsb)) {
            return BuildStatus.FailedNotAligned;
        }

        fileRam.dataBase = dataLsb;
        fileRam.dataGap = MathUtil.getOffset(dataLsb, data2Lsb);  // including one end
        Vec3i data = MathUtil.getOffset(dataLsb, dataMsb);
        float size = 1F;
        if (data.getX() != fileRam.dataGap.getX()) {
            size = ((float)data.getX()) / fileRam.dataGap.getX() + 1;
        } else if (data.getY() != fileRam.dataGap.getY()) {
            size = ((float)data.getY()) / fileRam.dataGap.getY() + 1;
        } else if (data.getZ() != fileRam.dataGap.getZ()) {
            size = ((float)data.getZ()) / fileRam.dataGap.getZ() + 1;
        }
        fileRam.dataSize = MathUtil.float2Int(size);

        if (fileRam.dataSize > 64) {
            throw new OversizedException();
        }

        if (type.equals(FileRamType.WriteOnly)) {
            // test if there is non-redstone-wire block in the bus
            BlockPos blockPos = fileRam.nDataBlockPos(2);
            for (int i = 2; i < size; i++) {
                if (!world.getBlockState(blockPos).isOf(Blocks.REDSTONE_WIRE)) {
                    throw new BlockNotRedstoneWireException();
                }
                blockPos = MathUtil.applyOffset(blockPos, fileRam.dataGap);
            }
        }

        if (!MathUtil.isFloatIntegral(size)) {
            return BuildStatus.WarningNotAligned;
        }

        return BuildStatus.Success;
    }

    public FileRam build(ServerWorld world) throws BlockNotRedstoneWireException {
        fileRam.name = name;
        fileRam.dimensionType = dimensionType;
        fileRam.clock = clock;
        fileRam.clockEdgeTriggering = clockEdgeTriggering;
        fileRam.buildClock(world);
        fileRam.filename = filename;
        fileRam.file = file;
        fileRam.fileEndianness = fileEndianness;
        fileRam.setRunning(false);
        fileRam.buildFancyName();
        return fileRam;
    }
}
