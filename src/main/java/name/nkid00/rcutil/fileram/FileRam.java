package name.nkid00.rcutil.fileram;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.function.BiConsumer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;

import name.nkid00.rcutil.MathUtil;
import name.nkid00.rcutil.enumeration.FileRamFileByteOrder;
import name.nkid00.rcutil.enumeration.FileRamEdgeTriggering;
import name.nkid00.rcutil.enumeration.FileRamType;
import name.nkid00.rcutil.exception.BlockNotRedstoneWireException;

public class FileRam {
    public String name = null;
    public MutableText fancyName = null;

    public FileRamType type = null;

    public DimensionType dimensionType = null;

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
    public FileRamFileByteOrder fileByteOrder = null;

    public boolean running = false;

    public boolean lastClockState = false;

    public void buildFancyName() {
        TranslatableText typeText = type.toText(), edgeText = clockEdgeTriggering.toText(), byteOrderText = fileByteOrder.toText();
        fancyName = new TranslatableText("rcutil.fileram.fancyname", name, typeText, edgeText, filename, byteOrderText, addrSize, dataSize);
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
        world.setBlockState(blockPos, power ? Blocks.REDSTONE_BLOCK.getDefaultState() : Blocks.AIR.getDefaultState());
    }

    public boolean getClockState(ServerWorld world) throws BlockNotRedstoneWireException {
        return getDigitalRedstonePower(world, clock);
    }

    public long readAddr(ServerWorld world) throws BlockNotRedstoneWireException {
        BitSet addr = new BitSet(addrSize);
        BlockPos blockPos = addrBase;
        for (int i = 0; i < addrSize; i++) {
            if (getDigitalRedstonePower(world, blockPos)) {
                addr.set(i);
            }
            blockPos = MathUtil.applyOffset(blockPos, addrGap);
        }
        if (fileByteOrder == FileRamFileByteOrder.BigEndian) {
            addr = MathUtil.reverseBitSet(addr, addrSize);
        }
        return MathUtil.bitSet2Long(addr);
    }

    public BitSet readData(ServerWorld world) throws BlockNotRedstoneWireException {
        BitSet data = new BitSet(dataSize);
        BlockPos blockPos = dataBase;
        for (int i = 0; i < dataSize; i++) {
            if (getDigitalRedstonePower(world, blockPos)) {
                data.set(i);
            }
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
        if (fileByteOrder == FileRamFileByteOrder.BigEndian) {
            data = MathUtil.reverseBitSet(data, dataSize);
        }
        return data;
    }

    public void writeData(ServerWorld world, BitSet data) throws BlockNotRedstoneWireException {
        BlockPos blockPos = dataBase;
        if (fileByteOrder == FileRamFileByteOrder.BigEndian) {
            data = MathUtil.reverseBitSet(data, dataSize);
        }
        for (int i = 0; i < dataSize; i++) {
            setDigitalRedstonePower(world, blockPos, data.get(i));
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
    }

    public void tick(ServerWorld world) throws BlockNotRedstoneWireException, EOFException, IOException {
        boolean clockState = getClockState(world);
        if (lastClockState != clockState) {
            lastClockState = clockState;
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
                        // calculate the real address of the data in the file
                        long addrBit = readAddr(world) * dataSize;
                        long addrByte = addrBit >> 3;
                        int offsetBit = (int)(addrBit & 0b111);
                        int lenBit = dataSize + offsetBit;
                        int lenByte = lenBit >> 3;
                        if ((lenBit & 0b111) != 0) {
                            lenByte++;
                        }

                        // read file into buffer
                        byte[] dataByte = new byte[lenByte];
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                        randomAccessFile.seek(addrByte);
                        randomAccessFile.readFully(dataByte);
                        randomAccessFile.close();

                        writeData(world, BitSet.valueOf(dataByte).get(offsetBit, offsetBit + dataSize));
                    }
                    break;
                case WriteOnly: {
                        // calculate real address of the data in the file
                        long addrBit = readAddr(world) * dataSize;
                        long addrByte = addrBit >> 3;
                        int offsetBit = (int)(addrBit & 0b111);
                        int lenBit = dataSize + offsetBit;
                        int lenByte = lenBit >> 3;
                        if ((lenBit & 0b111) != 0) {
                            lenByte++;
                        }
                        BitSet data = readData(world);

                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

                        // read file into buffer
                        byte[] rawDataByte = new byte[lenByte];
                        randomAccessFile.seek(addrByte);
                        BitSet rawBitSet;
                        try {
                            randomAccessFile.readFully(rawDataByte);
                            rawBitSet = BitSet.valueOf(rawDataByte);
                        } catch (EOFException e) {
                            // fill the file with 0
                            randomAccessFile.seek(0);
                            for (int i = 0; i < addrByte; i++) {
                                randomAccessFile.write(0);
                            }
                            // fill the buffer with 0
                            rawBitSet = new BitSet(lenByte << 3);
                        }

                        // write data into buffer
                        for (int i = 0; i < dataSize; i++) {
                            rawBitSet.set(i + offsetBit, data.get(i));
                        }

                        // write back buffer
                        randomAccessFile.seek(addrByte);
                        randomAccessFile.write(MathUtil.bitSet2ByteArray(rawBitSet, lenByte));
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
                fancyName.setStyle(Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.UNDERLINE));
            } else {
                fancyName.setStyle(Style.EMPTY);
            }
        }
    }

    public BlockPos nAddrBlockPos(int n) {
        return MathUtil.applyOffset(addrBase, MathUtil.scale(addrGap, n));
    }

    public void forEachEnumerateAddr(BiConsumer<? super Integer, ? super BlockPos> consumer) {
        BlockPos blockPos = addrBase;
        for (int i = 0; i < addrSize; i++) {
            consumer.accept(i, blockPos);
            blockPos = MathUtil.applyOffset(blockPos, addrGap);
        }
    }

    public BlockPos nDataBlockPos(int n) {
        return MathUtil.applyOffset(dataBase, MathUtil.scale(dataGap, n));
    }

    public void forEachEnumerateData(BiConsumer<? super Integer, ? super BlockPos> consumer) {
        BlockPos blockPos = dataBase;
        for (int i = 0; i < dataSize; i++) {
            consumer.accept(i, blockPos);
            blockPos = MathUtil.applyOffset(blockPos, dataGap);
        }
    }

    public void spawnAddrParticles(ServerWorld world) {
        forEachEnumerateAddr((i, blockPos) -> {
            float rgb[] = MathUtil.HSV2RGB(i * 120F / addrSize + 60F, 1F, 1F);
            world.spawnParticles(new DustParticleEffect(rgb[0], rgb[1], rgb[2], 1F), blockPos.getX() + 0.5, blockPos.getY() + 0.2, blockPos.getZ() + 0.5, 10, 0, 0, 0, 0);
        });
    }

    public void spawnDataParticles(ServerWorld world) {
        forEachEnumerateData((i, blockPos) -> {
            float rgb[] = MathUtil.HSV2RGB(i * 120F / dataSize + 180F, 1F, 1F);
            world.spawnParticles(new DustParticleEffect(rgb[0], rgb[1], rgb[2], 1F), blockPos.getX() + 0.5, blockPos.getY() + 0.2, blockPos.getZ() + 0.5, 10, 0, 0, 0, 0);
        });
    }

    public void spawnClockParticles(ServerWorld world) {
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, clock.getX() + 0.5, clock.getY() + 0.2, clock.getZ() + 0.5, 10, 0, 0, 0, 0);
    }
}
