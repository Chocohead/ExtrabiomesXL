/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.items;

import extrabiomes.blocks.BlockCustomTallGrass.TallGrassType;
import extrabiomes.utility.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemGrass extends MultiItemBlock
{
	private static final int META_LIMIT = TallGrassType.values().length;
	
    public ItemGrass(Block block)
    {
        super(block);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        int metadata = itemstack.getItemDamage() % META_LIMIT;
        itemstack = itemstack.copy();
        itemstack.setItemDamage(metadata);
        return super.getUnlocalizedName(itemstack);
    }
    
}
