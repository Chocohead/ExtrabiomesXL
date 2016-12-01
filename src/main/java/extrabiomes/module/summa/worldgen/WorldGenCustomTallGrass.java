/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.module.summa.worldgen;

import java.util.Random;

import extrabiomes.blocks.BlockCustomTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCustomTallGrass extends WorldGenerator {
	private final IBlockState tallGrassState;

	public WorldGenCustomTallGrass(IBlockState state) {
		assert state.getBlock() instanceof BlockCustomTallGrass;
		this.tallGrassState = state;
	}

	public boolean generate(World world, Random rand, BlockPos position) {
		for (IBlockState state = world.getBlockState(position); (state.getBlock().isAir(state, world, position)
				|| state.getBlock().isLeaves(state, world, position))
				&& position.getY() > 0; state = world.getBlockState(position)) {
			position = position.down();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

			if (world.isAirBlock(pos) && ((BlockCustomTallGrass) tallGrassState.getBlock()).canBlockStay(world, pos, tallGrassState)) {
				world.setBlockState(pos, tallGrassState, 2);
			}
		}

		return true;
	}
}