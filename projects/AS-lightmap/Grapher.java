import net.minecraft.util.math.BlockPos;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class Grapher {
    public static final int LENGTH = 256;
    public static final long SEED = new Random(1000).nextLong();

    public static void main(String args[]) throws FileNotFoundException {
        try (PrintWriter output = new PrintWriter("starlight.dat")) {
        SkyCollectionHelper map = new SkyCollectionHelper();
        BlockPos maxPos = null;
        float max = Float.MIN_VALUE;
        for (int x = -LENGTH; x < LENGTH; x += SkyCollectionHelper.accuracy / 2) {
            for (int z = -LENGTH; z < LENGTH; z += SkyCollectionHelper.accuracy / 2) {
                BlockPos here = new BlockPos(x, 0, z);
                float current = map.getDistributionInternal(SEED, here);
                output.printf("%d %d %f", x, z, current);
                if (current > max) {
                    max = current;
                    maxPos = here;
                }
            }
        }

        System.out.printf("%.3f @ (%d,%d)\n", max, maxPos.getX(), maxPos.getZ());
        }
    }
} 
