package com.rocketnotfound.rnf.world.gen.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class HorizontalDistanceAwayFromOrigin extends PosRuleTest {
    public static final Codec<HorizontalDistanceAwayFromOrigin> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.fieldOf("inverse").orElse(false).forGetter((ruleTest) -> ruleTest.inverse),
            Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((ruleTest) -> ruleTest.minChance),
            Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((ruleTest) -> ruleTest.maxChance),
            Codec.INT.fieldOf("min_dist").orElse(0).forGetter((ruleTest) -> ruleTest.minDistance),
            Codec.INT.fieldOf("max_dist").orElse(0).forGetter((ruleTest) -> ruleTest.maxDistance)
    ).apply(instance, HorizontalDistanceAwayFromOrigin::new));
    public static final PosRuleTestType<HorizontalDistanceAwayFromOrigin> TYPE = () -> CODEC;

    private final boolean inverse;
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;

    public HorizontalDistanceAwayFromOrigin(boolean inverse, float minChance, float maxChance, int minDistance, int maxDistance) {
        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("Invalid range: [" + minDistance + "," + maxDistance + "]");
        } else {
            this.inverse = inverse;
            this.minChance = minChance;
            this.maxChance = maxChance;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
        }
    }

    @Override
    public boolean test(BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        Vec3d centerOfStructure = new Vec3d(
                blockPos3.getX(),
                0,
                blockPos3.getZ()
        );
        double distanceFromOrigin = MathHelper.sqrt(
            (float)
            centerOfStructure.squaredDistanceTo(
                blockPos2.getX(),
                0,
                blockPos2.getZ()
            )
        );

        return this.inverse != (double)random.nextFloat() <= MathHelper.clampedLerp(
            this.minChance,
            this.maxChance,
            MathHelper.getLerpProgress(distanceFromOrigin, this.minDistance, this.maxDistance)
        );
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return TYPE;
    }
}
