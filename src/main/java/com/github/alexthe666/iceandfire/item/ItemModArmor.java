package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemModArmor extends ItemArmor {

	public ItemModArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, String gameName, String name) {
		super(material, renderIndex, slot);
		this.setCreativeTab(IceAndFire.TAB);
		this.setTranslationKey(name);
		this.setRegistryName(IceAndFire.MODID, gameName);
	}
}
