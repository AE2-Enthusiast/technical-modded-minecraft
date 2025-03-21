/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyCollectionHelper
 * Created by HellFirePvP
 * Date: 15.01.2017 / 16:31
 */
public class SkyCollectionHelper {

    public static final int accuracy = 32;
    private final Random sharedRand = new Random();
    /*
    public static float getSkyNoiseDistribution(World world, BlockPos pos) {
        return getDistributionInternal(new Random(world.getSeed()).nextLong(), pos);
    }
    */

    public float getDistributionInternal(long seed, BlockPos pos) {
        BlockPos lowerAnchorPoint = new BlockPos(
                        (int) Math.floor((float) pos.getX() / accuracy) * accuracy,
                        0,
                        (int) Math.floor((float) pos.getZ() / accuracy) * accuracy);
        float layer0 = getNoiseDistribution(seed,
                lowerAnchorPoint,
                lowerAnchorPoint.add(accuracy, 0, 0),
                lowerAnchorPoint.add(0, 0, accuracy),
                lowerAnchorPoint.add(accuracy, 0, accuracy),
                pos);
        sharedRand.setSeed(seed);
        long nextLayerSeed = sharedRand.nextLong();
        float layer1 = getNoiseDistribution(nextLayerSeed,
                lowerAnchorPoint,
                lowerAnchorPoint.add(accuracy, 0, 0),
                lowerAnchorPoint.add(0, 0, accuracy),
                lowerAnchorPoint.add(accuracy, 0, accuracy),
                pos);
        return layer0 * layer1;
    }

    private float getNoiseDistribution(long seed, BlockPos lXlZ, BlockPos hXlZ, BlockPos lXhZ, BlockPos hXhZ, BlockPos exact) {
        float nll = getNoise(seed, lXlZ.getX(), lXlZ.getZ());
        float nhl = getNoise(seed, hXlZ.getX(), hXlZ.getZ());
        float nlh = getNoise(seed, lXhZ.getX(), lXhZ.getZ());
        float nhh = getNoise(seed, hXhZ.getX(), hXhZ.getZ());

        float xPart = Math.abs(((float) (exact.getX() - lXlZ.getX()) ) / accuracy);
        float zPart = Math.abs(((float) (exact.getZ() - lXlZ.getZ()) ) / accuracy);

        return cosInterpolate(cosInterpolate(nll, nhl, xPart), cosInterpolate(nlh, nhh, xPart), zPart);
    }

    private float cosInterpolate(float l, float h, float partial) {
        float t2 = (1F - MathHelper.cos((float) (partial * Math.PI))) / 2F;
        return(l * (1F - t2) + h * t2);
    }

    private float getNoise(long seed, int posX, int posZ) {
        sharedRand.setSeed(
                simple_hash(new int[] {
                        (int) (seed),
                        (int) (seed >> 32),
                        posX,
                        posZ
                }, 4)
        );
        sharedRand.nextLong();
        return sharedRand.nextFloat();
    }

    //General hash function with some parameters~
    //Copied from Elucent~ via D
    private int simple_hash(int[] is, int count){
        int hash = 80238287;
        for (int i = 0; i < count; i++){
            hash = (hash << 4) ^ (hash >> 28) ^ (is[i] * 5449 % 130651);
        }
        return hash % 75327403;
    }

}
