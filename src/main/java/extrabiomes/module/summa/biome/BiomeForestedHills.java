/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.module.summa.biome;

import extrabiomes.lib.BiomeSettings;
import extrabiomes.lib.DecorationSettings;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Biomes;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeForestedHills extends ExtraBiome
{

	@Override
	public DecorationSettings getDecorationSettings() {
		return DecorationSettings.FORESTEDHILLS;
	}
	
	private static BiomeProperties getBiomeProperties() {
		final BiomeProperties props = new BiomeProperties("Forested Hills");
		props.setBaseHeight(1.0F);
		props.setHeightVariation(0.8F);
		props.setTemperature(Biomes.FOREST.getTemperature() - 0.1F);
		props.setRainfall(Biomes.FOREST.getRainfall());
		return props;
	}

    public BiomeForestedHills()
    {
		super(getBiomeProperties(), BiomeSettings.FORESTEDHILLS, Type.FOREST, Type.HILLS);
        
        spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int x, int y, int z)
    {
        return ColorizerFoliage.getFoliageColor(0.8F, 1.0F);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int x, int y, int z)
    {
        return ColorizerGrass.getGrassColor(0.8F, 1.0F);
    }
    
}
