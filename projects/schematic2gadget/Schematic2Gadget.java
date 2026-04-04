import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import java.awt.datatransfer.StringSelection;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;


import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

public class Schematic2Gadget {
    private static boolean isDone = false;
    private static final int BLOCKS_PER_SEGMENT = 10923;
    public static final int SECTION_SIZE = 128;
    public static final int MAP_SIZE = 128;
    
    public static void main(String[] args) throws IOException {
        InputStream input = Files.newInputStream(Path.of(args[0]));
        int posX = ((Integer.valueOf(args[1]) + MAP_SIZE / 2) / MAP_SIZE) * MAP_SIZE;
        int posZ = ((Integer.valueOf(args[2]) + MAP_SIZE / 2) / MAP_SIZE) * MAP_SIZE;
        NBTTagCompound inputTag = CompressedStreamTools.readCompressed(input);
        NBTTagList size = inputTag.getTagList("size", 3);
        int maxX = size.getIntAt(0) / SECTION_SIZE + 1;
        int maxY = size.getIntAt(1) / SECTION_SIZE + 1;
        int maxZ = size.getIntAt(2) / (SECTION_SIZE + 1) + 1;

        Scanner scanner = new Scanner(System.in);

        System.out.printf("Loaded Schematic %d by %d Sections Big\n", maxX, maxZ);
        for (int x = 0; x < maxX; x++) {
            for (int z = 0; z < maxZ; z++) {
                System.out.printf("Entering Section (%d, %d) @ (%d, 127, %d)\n", x, z, posX + (x * SECTION_SIZE), posZ + (z * SECTION_SIZE));
                NBTTagCompound schematic = new NBTTagCompound();
                int i = 0;
                while (toGchematic(inputTag, schematic, x, z, i) > 0) {
                    System.out.printf("Converting Segment %d\n...", i + 1);
                    System.out.printf("Converted Segment %d\n", i + 1);
                    System.out.println("Copying to Clipboard...");
                    try {
                        Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(
                                         new StringSelection(schematic.toString()),
                                         null
                                         );
                    } catch (HeadlessException e) {
                        e.printStackTrace();
                    }
                    System.out.print("Copied to Clipboard, Press Enter when Ready for Next Segment");
                    System.in.read();
                    i++;
                    schematic = new NBTTagCompound();
                }
            }
        }

        System.out.println("Finished Converting Schematic to Gchematic!");
    }

    public static int toGchematic(NBTTagCompound inputTag, NBTTagCompound newTag, final int sectionX, final int sectionZ, int segment) {
        NBTTagList blocks = inputTag.getTagList("blocks", 10);
        
        ArrayList<Integer> newPoses = new ArrayList<>(blocks.tagCount());
        ArrayList<Integer> newStates = new ArrayList<>(blocks.tagCount());
        short i = 0;
        int added = 0;
        for (NBTBase temp : blocks) {
            NBTTagCompound block = (NBTTagCompound) temp;
            NBTTagList pos = block.getTagList("pos", 3);
            int x = pos.getIntAt(0);
            int y = pos.getIntAt(1);
            int z = pos.getIntAt(2);
            //System.out.printf("Testing Block %d @ (%d, %d, %d)", i, x, y, z);
            if ((x >= (sectionX * SECTION_SIZE) && x < (sectionX + 1) * SECTION_SIZE) && (z >= (sectionZ * (SECTION_SIZE + 1)) && z < (sectionZ + 1) * (SECTION_SIZE + 1))) {
                x = x - sectionX * 128;
                z = z - sectionZ * 128;
                if (!((i < segment * BLOCKS_PER_SEGMENT) || (i >= (segment + 1) * BLOCKS_PER_SEGMENT))) {
                    added++;
                    newPoses.add((((x - SECTION_SIZE / 2) & 0xff) << 16) + ((((y - SECTION_SIZE / 2) & 0xff) << 8) + (((z - SECTION_SIZE / 2) & 0xff))));
                    newStates.add(block.getInteger("state"));
                }
                i++;
            }
        }

        NBTTagList palette = inputTag.getTagList("palette", 10);

        NBTTagList mapIntState = new NBTTagList();
        i = 0;
        for (NBTBase temp : palette) {
            NBTTagCompound state = (NBTTagCompound) temp;
            NBTTagCompound newState = new NBTTagCompound();
            newState.setShort("mapSlot", i++);
            if (state.getString("Name").equals("minecraft:water")) {
                state.setString("Name", "enderio:block_omni_reservoir");
            }
            newState.setTag("mapState", state);
            mapIntState.appendTag(newState);
        }

        NBTTagList size = inputTag.getTagList("size", 3);

        NBTTagCompound startPos = new NBTTagCompound();
        startPos.setInteger("X", 0);
        startPos.setInteger("Y", 0);
        startPos.setInteger("Z", 0);
        
        NBTTagCompound endPos = new NBTTagCompound();
        endPos.setInteger("X", size.getIntAt(0));
        endPos.setInteger("Y", size.getIntAt(1));
        endPos.setInteger("Z", size.getIntAt(2));
        
        newTag.setIntArray("stateIntArray", newStates.stream().mapToInt(j -> j).toArray());
        newTag.setInteger("dim", 0);
        newTag.setIntArray("posIntArray", newPoses.stream().mapToInt(j -> j).toArray());
        newTag.setTag("startPos", startPos);
        newTag.setTag("mapIntState", mapIntState);
        newTag.setTag("endPos", endPos);

        return added;
    }
}
