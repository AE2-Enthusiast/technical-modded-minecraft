import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class Grapher {
    public static final int LENGTH = 256;
    public static final long SEED = new Random(1000).nextLong();
    
    public static void main(String args[]) {
        SkyCollectionHelper map = new SkyCollectionHelper();
        BlockPos maxPos = null;
        float max = Float.MIN_VALUE;
        for (int x = -LENGTH; x < LENGTH; x += SkyCollectionHelper.accuracy / 2) {
            for (int z = -LENGTH; z < LENGTH; z += SkyCollectionHelper.accuracy / 2) {
                BlockPos here = new BlockPos(x, 0, z);
                float current = map.getDistributionInternal(SEED, here);
                if (current > max) {
                    max = current;
                    maxPos = here;
                }
            }
        }

        System.out.printf("%.3f @ (%d,%d)\n", max, maxPos.getX(), maxPos.getZ());
    }
} 
