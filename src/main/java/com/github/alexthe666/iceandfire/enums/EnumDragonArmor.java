package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.item.ItemScaleArmor;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public enum EnumDragonArmor {

	armor_red(12, EnumDragonEgg.RED),
	armor_bronze(13, EnumDragonEgg.BRONZE),
	armor_green(14, EnumDragonEgg.GREEN),
	armor_gray(15, EnumDragonEgg.GRAY),
	armor_blue(12, EnumDragonEgg.BLUE),
	armor_white(13, EnumDragonEgg.WHITE),
	armor_sapphire(14, EnumDragonEgg.SAPPHIRE),
	armor_silver(15, EnumDragonEgg.SILVER);

	public ArmorMaterial material;
	public int armorId;
	public EnumDragonEgg eggType;
	@GameRegistry.ObjectHolder(IceAndFire.MODID + ":armor_dragon_helmet")
	public Item helmet;
	@GameRegistry.ObjectHolder(IceAndFire.MODID + ":armor_dragon_chestplate")
	public Item chestplate;
	@GameRegistry.ObjectHolder(IceAndFire.MODID + ":armor_dragon_leggings")
	public Item leggings;
	@GameRegistry.ObjectHolder(IceAndFire.MODID + ":armor_dragon_boots")
	public Item boots;
	public ArmorMaterial armorMaterial;
	private EnumDragonArmor(int armorId, EnumDragonEgg eggType) {
		this.armorId = armorId;
		this.eggType = eggType;
	}

	public static void initArmors() {
		for (int i = 0; i < EnumDragonArmor.values().length; i++) {
			EnumDragonArmor.values()[i].armorMaterial  = EnumHelper.addArmorMaterial("DragonScales" + (i + 1), "iceandfire:armor_dragon_scales" + (i + 1), 36, new int[]{4, 7, 9, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 2);
			EnumDragonArmor.values()[i].helmet = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, 0, EntityEquipmentSlot.HEAD).setTranslationKey("iceandfire.dragonHelmet");
			EnumDragonArmor.values()[i].chestplate = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, 1, EntityEquipmentSlot.CHEST).setTranslationKey("iceandfire.dragonChestplate");
			EnumDragonArmor.values()[i].leggings = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, 2, EntityEquipmentSlot.LEGS).setTranslationKey("iceandfire.dragonLeggings");
			EnumDragonArmor.values()[i].boots = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, 3, EntityEquipmentSlot.FEET).setTranslationKey("iceandfire.dragonBoots");
			EnumDragonArmor.values()[i].helmet.setRegistryName(EnumDragonArmor.values()[i].name() + "_helmet");
			//GameRegistry.register(EnumDragonArmor.values()[i].helmet);
			EnumDragonArmor.values()[i].chestplate.setRegistryName(EnumDragonArmor.values()[i].name() + "_chestplate");
			//GameRegistry.register(EnumDragonArmor.values()[i].chestplate);
			EnumDragonArmor.values()[i].leggings.setRegistryName(EnumDragonArmor.values()[i].name() + "_leggings");
			//GameRegistry.register(EnumDragonArmor.values()[i].leggings);
			EnumDragonArmor.values()[i].boots.setRegistryName(EnumDragonArmor.values()[i].name() + "_boots");
			//GameRegistry.register(EnumDragonArmor.values()[i].boots);
		}
	}

	public static Item getScaleItem(EnumDragonArmor armor) {
		switch (armor) {
			case armor_red:
				return ModItems.dragonscales_red;
			case armor_bronze:
				return ModItems.dragonscales_bronze;
			case armor_green:
				return ModItems.dragonscales_green;
			case armor_gray:
				return ModItems.dragonscales_gray;
			case armor_blue:
				return ModItems.dragonscales_blue;
			case armor_white:
				return ModItems.dragonscales_white;
			case armor_sapphire:
				return ModItems.dragonscales_sapphire;
			case armor_silver:
				return ModItems.dragonscales_silver;
			default:
				return ModItems.dragonscales_red;
		}
	}

	public static Item getEggItem(EnumDragonArmor armor) {
		switch (armor) {
			case armor_red:
				return ModItems.dragonegg_red;
			case armor_bronze:
				return ModItems.dragonegg_bronze;
			case armor_green:
				return ModItems.dragonegg_green;
			case armor_gray:
				return ModItems.dragonegg_gray;
			default:
				return ModItems.dragonegg_red;
		}
	}
}
