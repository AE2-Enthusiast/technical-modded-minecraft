import net.minecraft.util.math.BlockPos;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class Grapher {
    public static final int LENGTH = (1024 / SkyCollectionHelper.accuracy) * SkyCollectionHelper.accuracy;
    
    public static final long SEED = new Random(2023794103891237230l).nextLong();
    //public static final long SEED = new Random(5506212711182897848l).nextLong();

    public static void main(String args[]) throws FileNotFoundException {
        try (PrintWriter output = new PrintWriter("starlight.dat")) {
        SkyCollectionHelper map = new SkyCollectionHelper();
        BlockPos maxPos = null;
        float max = Float.MIN_VALUE;
        int maxDistance = Integer.MIN_VALUE;
        output.println("#X Z Strength");
        for (int x = -LENGTH; x < LENGTH; x += SkyCollectionHelper.accuracy) {
            for (int z = -LENGTH; z < LENGTH; z += SkyCollectionHelper.accuracy) {
                BlockPos here = new BlockPos(x, 0, z);
                float current = map.getDistributionInternal(SEED, here);
                output.printf("%f ", current);
                if (current >= .90) {
                    System.out.printf("%f @ (%d,%d) (%f m)\n", current, x, z, Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)));
                }
                if (current > max) {
                    max = current;
                    maxPos = here;
                    maxDistance = x + z;
                } else if (current == max && maxDistance > (x + z)) {
                    maxDistance = x + z;
                    maxPos = here;
                }
            }
            output.println();
        }

        System.out.printf("Max: %.3f @ (%d,%d)\n", max, maxPos.getX(), maxPos.getZ());
        }
    }
} 
