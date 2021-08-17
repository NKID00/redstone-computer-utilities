package name.nkid00.rcutil.fileram;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.function.BiConsumer;

import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.enumeration.FileRamFileEndianness;
import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamType;
import name.nkid00.rcutil.exception.BlockNotRedstoneWireException;

public class FileRam {
    public String name = null;
    public MutableText fancyName = null;

    public FileRamType type = null;

    public BlockPos addrBase = null;
    public Vec3i addrGap = null;  // including one end
    public int addrSize = 0;

    public BlockPos dataBase = null;
    public Vec3i dataGap = null;  // including one end
    public int dataSize = 0;

    public BlockPos clock = null;
    public FileRamEdgeTriggering clockEdgeTriggering = null;

    public String filename = null;
    public File file = null;
    public FileRamFileEndianness fileEndianness = null;

    public boolean running = false;

    public boolean lastClockState = false;

    public void buildFancyName() {
        TranslatableText typeText = type.toText(), edgeText = clockEdgeTriggering.toText(), endiannessText = fileEndianness.toText();
        fancyName = new TranslatableText("rcutil.fileram.fancyname", name, typeText, edgeText, filename, endiannessText, addrSize, dataSize);
    }

    public void buildClock(ServerWorld world) throws BlockNotRedstoneWireException {
        lastClockState = getClockState(world);
    }

    public static int getRedstonePower(ServerWorld world, BlockPos blockPos) throws BlockNotRedstoneWireException {
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isOf(Blocks.REDSTONE_WIRE)) {
            throw new BlockNotRedstoneWireException();
        }
        return blockState.get(RedstoneWireBlock.POWER);
    }

    public static boolean getDigitalRedstonePower(ServerWorld world, BlockPos blockPos) throws BlockNotRedstoneWireException {
        return getRedstonePower(world, blockPos) > 0;
    }

    public static void setDigitalRedstonePower(ServerWorld world, BlockPos blockPos, boolean power) {
        world.setBlockState(blockPos, power ? RedstoneBlock.getStateFromRawId(0) : AirBlock.getStateFromRawId(0));
    }

    public boolean getClockState(ServerWorld world) throws BlockNotRedstoneWireException {
        return getDigitalRedstonePower(world, clock);
    }

    public long readAddr(ServerWorld world) throws BlockNotRedstoneWireException {
        BitSet addr = new BitSet(addrSize);
        BlockPos blockPos = addrBase;
        for (int i = 0; ; i++) {
            if (getDigitalRedstonePower(world, blockPos)) {
                addr.set(i);
            }
            if (i >= addrSize) {
                break;
            }
            blockPos = MathUtil.applyOffset(blockPos, addrGap);
        }
        return addr.toLongArray()[0];
    }

    public BitSet readData(ServerWorld world) throws BlockNotRedstoneWireException {
        BitSet data = new BitSet(dataSize);
        BlockPos blockPos = dataBase;
        for (int i = 0; ; i++) {
            if (getDigitalRedstonePower(world, blockPos)) {
                data.set(i);
            }
            if (i >= dataSize) {
                break;
            }
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
        return data;
    }

    public void writeData(ServerWorld world, BitSet data) throws BlockNotRedstoneWireException {
        BlockPos blockPos = dataBase;
        for (int i = 0; ; i++) {
            setDigitalRedstonePower(world, blockPos, data.get(i));
            if (i >= dataSize) {
                break;
            }
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
    }

    public void tick(ServerWorld world) throws BlockNotRedstoneWireException, EOFException, IOException {
        boolean clockState = getClockState(world);
        if (lastClockState != clockState) {
            switch (clockEdgeTriggering) {
                case Positive:
                    if (!clockState) {
                        return;
                    }
                    break;
                case Negative:
                    if (clockState) {
                        return;
                    }
                    break;
                case Dual:
                    break;
            }
            switch (type) {
                case ReadOnly: {
                        long addrBit = readAddr(world) * dataSize;
                        long addrByte = addrBit >> 3;
                        int offsetBit = (int)(addrBit & 0b111);
                        int lenBit = dataSize + offsetBit;
                        int lenByte = lenBit >> 3;
                        if ((lenBit & 0b111) != 0) {
                            lenByte++;
                        }
                        byte[] dataByte = new byte[lenByte];
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                        randomAccessFile.seek(addrByte);
                        randomAccessFile.readFully(dataByte);
                        randomAccessFile.close();
                        writeData(world, BitSet.valueOf(dataByte).get(offsetBit, offsetBit + dataSize));
                    }
                    break;
                case WriteOnly: {
                        long addrBit = readAddr(world) * dataSize;
                        long addrByte = addrBit >> 3;
                        int offsetBit = (int)(addrBit & 0b111);
                        int lenBit = dataSize + offsetBit;
                        int lenByte = lenBit >> 3;
                        if ((lenBit & 0b111) != 0) {
                            lenByte++;
                        }
                        BitSet data = readData(world);
                        byte[] rawDataByte = new byte[lenByte];
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                        randomAccessFile.seek(addrByte);
                        randomAccessFile.readFully(rawDataByte);
                        BitSet rawBitSet = BitSet.valueOf(rawDataByte);
                        for (int i = 0; i < dataSize; i++) {
                            rawBitSet.set(i + offsetBit, data.get(i));
                        }
                        for (byte v : rawBitSet.toByteArray()) {
                            randomAccessFile.writeByte(v);
                        }
                        randomAccessFile.close();
                    }
                    break;

            }
        }
    }

    public void setRunning(boolean v) {
        if (running != v) {
            running = v;
            if (v) {
                fancyName.setStyle(Style.EMPTY.withBold(true).withUnderline(true));
            } else {
                fancyName.setStyle(Style.EMPTY);
            }
        }
    }

    public BlockPos nAddressBlockPos(int n) {
        return MathUtil.applyOffset(addrBase, MathUtil.scale(addrGap, n));
    }

    public void forEachEnumerateAddress(BiConsumer<? super Integer, ? super BlockPos> consumer) {
        BlockPos blockPos = addrBase;
        for (int i = 0; ; i++) {
            consumer.accept(i, blockPos);
            if (i >= addrSize) {
                break;
            }
            blockPos = MathUtil.applyOffset(blockPos, addrGap);
        }
    }

    public BlockPos nDataBlockPos(int n) {
        return MathUtil.applyOffset(dataBase, MathUtil.scale(dataGap, n));
    }

    public void forEachEnumerateData(BiConsumer<? super Integer, ? super BlockPos> consumer) {
        BlockPos blockPos = dataBase;
        for (int i = 0; ; i++) {
            consumer.accept(i, blockPos);
            if (i >= dataSize) {
                break;
            }
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
    }
}
