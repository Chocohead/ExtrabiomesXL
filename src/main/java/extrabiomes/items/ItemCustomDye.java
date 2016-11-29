package extrabiomes.items;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import extrabiomes.helpers.LogHelper;
import extrabiomes.lib.Element;
import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.utility.ModelUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCustomDye extends Item implements ITextureRegisterer {

	public enum Color
	{
		BLACK("black", 0, 0x1E1B1B, EnumDyeColor.BLACK.getDyeDamage()),
		BLUE("blue",   1, 0x253192, EnumDyeColor.BLUE.getDyeDamage()),
		BROWN("brown", 2, 0x51301A, EnumDyeColor.BROWN.getDyeDamage()),
		WHITE("white", 3, 0xF0F0F0, EnumDyeColor.WHITE.getDyeDamage());
		
		public final String	name;
		public final int	meta;
		public final int	hex;
		public final int	mcDamage;

		private Color(String name, int meta, int hex, int mcDamage)
		{
			this.name = name;
			this.meta = meta;
			this.hex = hex;
			this.mcDamage = mcDamage;
		}
		
		public static final Color[] VALUES = values();
	}
	
	private static Element[] elements = {Element.DYE_BLACK, Element.DYE_BLUE, Element.DYE_BROWN, Element.DYE_WHITE};
	
	public ItemCustomDye() {
		super();
        this.setHasSubtypes(true);
        this.setMaxDamage(0);

		if (Color.VALUES.length != elements.length) {
			LogHelper.severe("Dye color vs elements count mismatch!");
		}
	}

	public void init() {
		for( int idx = 0; idx < elements.length; ++idx ) {
			final Color color = Color.VALUES[idx];
			final Element element = elements[idx];
			
			LogHelper.info(color + " = " + element);

			element.set(new ItemStack(this, 1, color.meta));
			OreDictionary.registerOre("dye"+StringUtils.capitalize(color.name), element.get());
		}
		/*
		 * // make sure wool recipes are good
		 * OreDictionary.initVanillaEntries();
		 */
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerTexture() {
		for (int meta = 0; meta < Color.VALUES.length; meta++) {
			ModelUtil.registerTexture(this, meta, "dye_" + Color.VALUES[meta].name);
		}
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on
	 * sheep.
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (target instanceof EntitySheep) {
			final EntitySheep sheep = (EntitySheep) target;
			final int damage = stack.getItemDamage();
			final Color color = Color.VALUES[damage % Color.VALUES.length];
			final EnumDyeColor dye = EnumDyeColor.byDyeDamage(color.mcDamage);

			LogHelper.fine("Dying sheep " + damage + '/' + color + " = " + dye);

			if (!sheep.getSheared() && sheep.getFleeceColor() != dye) {
				sheep.setFleeceColor(dye);
				--stack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an
	 * ItemStack so different stacks can have different names based on their
	 * damage or NBT.
	 */
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = MathHelper.clamp_int(itemStack.getItemDamage(), 0, Color.VALUES.length);
		return super.getUnlocalizedName() + '.' + Color.VALUES[i].name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * returns a list of items with the same ID, but different meta (eg: vanilla dye returns 16 items)
	 */
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (Element element : elements) {
			list.add(element.get());
		}
	}
}
