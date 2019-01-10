package com.github.alexthe666.iceandfire;

import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexAddRoom;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexStaff;
import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.alexthe666.iceandfire.client.model.*;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonAnimations;
import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.client.particle.*;
import com.github.alexthe666.iceandfire.client.render.entity.*;
import com.github.alexthe666.iceandfire.client.render.tile.*;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.tile.*;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.event.EventClient;
import com.github.alexthe666.iceandfire.event.EventNewMenu;
import com.github.alexthe666.iceandfire.item.ICustomRendered;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

	private static final ModelFireDragonArmor FIRE_DRAGON_SCALE_ARMOR_MODEL = new ModelFireDragonArmor(0.5F);
	private static final ModelFireDragonArmor FIRE_DRAGON_SCALE_ARMOR_MODEL_LEGS = new ModelFireDragonArmor(0.2F);
	private static final ModelIceDragonArmor ICE_DRAGON_SCALE_ARMOR_MODEL = new ModelIceDragonArmor(0.5F);
	private static final ModelIceDragonArmor ICE_DRAGON_SCALE_ARMOR_MODEL_LEGS = new ModelIceDragonArmor(0.2F);
	private static final ModelDeathWormArmor DEATHWORM_ARMOR_MODEL = new ModelDeathWormArmor(0.5F);
	private static final ModelDeathWormArmor DEATHWORM_ARMOR_MODEL_LEGS = new ModelDeathWormArmor(0.2F);
	private static final ModelTrollArmor TROLL_ARMOR_MODEL = new ModelTrollArmor(0.75F);
	private static final ModelTrollArmor TROLL_ARMOR_MODEL_LEGS = new ModelTrollArmor(0.35F);
	private FontRenderer bestiaryFontRenderer;
	@SideOnly(Side.CLIENT)
	private static final IceAndFireTEISR TEISR = new IceAndFireTEISR();
	private int thirdPersonViewDragon = 0;
	private static MyrmexHive referedClientHive = null;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.podium), new ResourceLocation("iceandfire:podium_oak"), new ResourceLocation("iceandfire:podium_spruce"), new ResourceLocation("iceandfire:podium_birch"), new ResourceLocation("iceandfire:podium_jungle"), new ResourceLocation("iceandfire:podium_acacia"), new ResourceLocation("iceandfire:podium_dark_oak"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 0, new ModelResourceLocation("iceandfire:podium_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 1, new ModelResourceLocation("iceandfire:podium_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 2, new ModelResourceLocation("iceandfire:podium_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 3, new ModelResourceLocation("iceandfire:podium_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 4, new ModelResourceLocation("iceandfire:podium_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.podium), 5, new ModelResourceLocation("iceandfire:podium_dark_oak", "inventory"));
		ModelBakery.registerItemVariants(ModItems.dragonbone_bow, new ResourceLocation("iceandfire:dragonbone_bow"), new ResourceLocation("iceandfire:dragonbone_bow_pulling_0"), new ResourceLocation("iceandfire:dragonbone_bow_pulling_1"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragonbone_bow, 0, new ModelResourceLocation("iceandfire:dragonbone_bow", "inventory"));
		ModelBakery.registerItemVariants(ModItems.dragon_skull, new ResourceLocation("iceandfire:dragon_skull_fire"), new ResourceLocation("iceandfire:dragon_skull_ice"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_skull, 0, new ModelResourceLocation("iceandfire:dragon_skull_fire", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_skull, 1, new ModelResourceLocation("iceandfire:dragon_skull_ice", "inventory"));
		ModelBakery.registerItemVariants(ModItems.dragon_armor_iron, new ResourceLocation("iceandfire:dragonarmor_iron_head"), new ResourceLocation("iceandfire:dragonarmor_iron_neck"), new ResourceLocation("iceandfire:dragonarmor_iron_body"), new ResourceLocation("iceandfire:dragonarmor_iron_tail"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_iron, 0, new ModelResourceLocation("iceandfire:dragonarmor_iron_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_iron, 1, new ModelResourceLocation("iceandfire:dragonarmor_iron_neck", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_iron, 2, new ModelResourceLocation("iceandfire:dragonarmor_iron_body", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_iron, 3, new ModelResourceLocation("iceandfire:dragonarmor_iron_tail", "inventory"));
		ModelBakery.registerItemVariants(ModItems.dragon_armor_gold, new ResourceLocation("iceandfire:dragonarmor_gold_head"), new ResourceLocation("iceandfire:dragonarmor_gold_neck"), new ResourceLocation("iceandfire:dragonarmor_gold_body"), new ResourceLocation("iceandfire:dragonarmor_gold_tail"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_gold, 0, new ModelResourceLocation("iceandfire:dragonarmor_gold_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_gold, 1, new ModelResourceLocation("iceandfire:dragonarmor_gold_neck", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_gold, 2, new ModelResourceLocation("iceandfire:dragonarmor_gold_body", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_gold, 3, new ModelResourceLocation("iceandfire:dragonarmor_gold_tail", "inventory"));
		ModelBakery.registerItemVariants(ModItems.dragon_armor_diamond, new ResourceLocation("iceandfire:dragonarmor_diamond_head"), new ResourceLocation("iceandfire:dragonarmor_diamond_neck"), new ResourceLocation("iceandfire:dragonarmor_diamond_body"), new ResourceLocation("iceandfire:dragonarmor_diamond_tail"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_diamond, 0, new ModelResourceLocation("iceandfire:dragonarmor_diamond_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_diamond, 1, new ModelResourceLocation("iceandfire:dragonarmor_diamond_neck", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_diamond, 2, new ModelResourceLocation("iceandfire:dragonarmor_diamond_body", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.dragon_armor_diamond, 3, new ModelResourceLocation("iceandfire:dragonarmor_diamond_tail", "inventory"));
		for(int i = 0; i < EnumHippogryphTypes.values().length; i++){
			ModelLoader.setCustomModelResourceLocation(ModItems.hippogryph_egg, i, new ModelResourceLocation("iceandfire:hippogryph_egg", "inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(ModItems.gorgon_head, 0, new ModelResourceLocation("iceandfire:gorgon_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.gorgon_head, 1, new ModelResourceLocation("iceandfire:gorgon_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 0, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 1, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 2, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 3, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 4, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.pixieHouse), 5, new ModelResourceLocation("iceandfire:pixie_house", "inventory"));
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.jar_pixie), new ResourceLocation("iceandfire:jar_0"), new ResourceLocation("iceandfire:jar_1"), new ResourceLocation("iceandfire:jar_2"), new ResourceLocation("iceandfire:jar_3"), new ResourceLocation("iceandfire:jar_4"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_empty), 0, new ModelResourceLocation("iceandfire:jar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_pixie), 0, new ModelResourceLocation("iceandfire:jar_0", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_pixie), 1, new ModelResourceLocation("iceandfire:jar_1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_pixie), 2, new ModelResourceLocation("iceandfire:jar_2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_pixie), 3, new ModelResourceLocation("iceandfire:jar_3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.jar_pixie), 4, new ModelResourceLocation("iceandfire:jar_4", "inventory"));
		ModelBakery.registerItemVariants(ModItems.deathworm_chitin, new ResourceLocation("iceandfire:deathworm_chitin_yellow"), new ResourceLocation("iceandfire:deathworm_chitin_white"), new ResourceLocation("iceandfire:deathworm_chitin_red"));
		ModelLoader.setCustomModelResourceLocation(ModItems.deathworm_chitin, 0, new ModelResourceLocation("iceandfire:deathworm_chitin_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.deathworm_chitin, 1, new ModelResourceLocation("iceandfire:deathworm_chitin_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.deathworm_chitin, 2, new ModelResourceLocation("iceandfire:deathworm_chitin_red", "inventory"));
		ModelBakery.registerItemVariants(ModItems.deathworm_egg, new ResourceLocation("iceandfire:deathworm_egg"), new ResourceLocation("iceandfire:deathworm_egg_giant"));
		ModelLoader.setCustomModelResourceLocation(ModItems.deathworm_egg, 0, new ModelResourceLocation("iceandfire:deathworm_egg", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.deathworm_egg, 1, new ModelResourceLocation("iceandfire:deathworm_egg_giant", "inventory"));
		for (EnumDragonArmor armor : EnumDragonArmor.values()) {
			renderArmors(armor);
		}
		for(EnumTroll.Weapon weapon : EnumTroll.Weapon.values()){
			ModelLoader.setCustomModelResourceLocation(weapon.item, 0, new ModelResourceLocation("iceandfire:troll_weapon", "inventory"));
		}
		for (EnumTroll troll : EnumTroll.values()) {
			ModelLoader.setCustomModelResourceLocation(troll.leather, 0, new ModelResourceLocation("iceandfire:troll_leather_" + troll.name().toLowerCase(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(troll.helmet, 0, new ModelResourceLocation("iceandfire:"  + troll.name().toLowerCase() + "_troll_leather_helmet", "inventory"));
			ModelLoader.setCustomModelResourceLocation(troll.chestplate, 0, new ModelResourceLocation("iceandfire:"  + troll.name().toLowerCase() + "_troll_leather_chestplate", "inventory"));
			ModelLoader.setCustomModelResourceLocation(troll.leggings, 0, new ModelResourceLocation("iceandfire:"  + troll.name().toLowerCase() + "_troll_leather_leggings", "inventory"));
			ModelLoader.setCustomModelResourceLocation(troll.boots, 0, new ModelResourceLocation("iceandfire:"  + troll.name().toLowerCase() + "_troll_leather_boots", "inventory"));
		}
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.myrmex_resin), new ResourceLocation("iceandfire:desert_myrmex_resin"), new ResourceLocation("iceandfire:jungle_myrmex_resin"));
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.myrmex_resin_sticky), new ResourceLocation("iceandfire:desert_myrmex_resin_sticky"), new ResourceLocation("iceandfire:jungle_myrmex_resin_sticky"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.myrmex_resin), 0, new ModelResourceLocation("iceandfire:desert_myrmex_resin", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.myrmex_resin), 1, new ModelResourceLocation("iceandfire:jungle_myrmex_resin", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.myrmex_resin_sticky), 0, new ModelResourceLocation("iceandfire:desert_myrmex_resin_sticky", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.myrmex_resin_sticky), 1, new ModelResourceLocation("iceandfire:jungle_myrmex_resin_sticky", "inventory"));
		for(int i = 0; i < 5; i++){
			ModelLoader.setCustomModelResourceLocation(ModItems.myrmex_desert_egg, i, new ModelResourceLocation("iceandfire:myrmex_desert_egg", "inventory"));
			ModelLoader.setCustomModelResourceLocation(ModItems.myrmex_jungle_egg, i, new ModelResourceLocation("iceandfire:myrmex_jungle_egg", "inventory"));
		}
		try {
			for (Field f : ModItems.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Item && !(obj instanceof ICustomRendered)) {
					ModelLoader.setCustomModelResourceLocation((Item)obj, 0, new ModelResourceLocation("iceandfire:" + ((Item)obj).getRegistryName().getPath(), "inventory"));
				} else if (obj instanceof Item[]) {
					for (Item item : (Item[]) obj) {
						if(!(item instanceof ICustomRendered)){
							ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("iceandfire:" + item.getRegistryName().getPath(), "inventory"));

						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		try {
			for (Field f : ModBlocks.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Block && !(obj instanceof ICustomRendered)) {
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)obj), 0, new ModelResourceLocation("iceandfire:" + ((Block)obj).getRegistryName().getPath(), "inventory"));
				} else if (obj instanceof Block[]) {
					for (Block block : (Block[]) obj) {
						if(!(block instanceof ICustomRendered)){
							ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("iceandfire:" + block.getRegistryName().getPath(), "inventory"));

						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	@SideOnly(Side.CLIENT)
	public static void renderArmors(EnumDragonArmor armor) {
		ModelLoader.setCustomModelResourceLocation(armor.helmet, 0, new ModelResourceLocation("iceandfire:" + armor.name() + "_helmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(armor.chestplate, 0, new ModelResourceLocation("iceandfire:" + armor.name() + "_chestplate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(armor.leggings, 0, new ModelResourceLocation("iceandfire:" + armor.name() + "_leggings", "inventory"));
		ModelLoader.setCustomModelResourceLocation(armor.boots, 0, new ModelResourceLocation("iceandfire:" + armor.name() + "_boots", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("deprecation")
	public void render() {
		this.bestiaryFontRenderer = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("iceandfire:textures/font/bestiary.png"), Minecraft.getMinecraft().renderEngine, false);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this.bestiaryFontRenderer);
		ModKeys.init();
		MinecraftForge.EVENT_BUS.register(new RenderModCapes());
		MinecraftForge.EVENT_BUS.register(new EventClient());
		MinecraftForge.EVENT_BUS.register(new EventNewMenu());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDummyGorgonHead.class, new RenderGorgonHead(false));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDummyGorgonHeadActive.class, new RenderGorgonHead(true));
		ForgeHooksClient.registerTESRItemStack(ModItems.gorgon_head, 0, TileEntityDummyGorgonHead.class);
		ForgeHooksClient.registerTESRItemStack(ModItems.gorgon_head, 1, TileEntityDummyGorgonHeadActive.class);
		renderEntities();


	}

	@SideOnly(Side.CLIENT)
	public void postRender() {
		EventClient.initializeStoneLayer();
		for(EnumTroll.Weapon weapon : EnumTroll.Weapon.values()) {
			weapon.item.setTileEntityItemStackRenderer(TEISR);
		}
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	private void renderEntities() {
		EnumDragonAnimations.initializeDragonModels();
		ModelBase firedragon_model = null;
		ModelBase icedragon_model = null;

		try {
			firedragon_model = new IceAndFireTabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/firedragon/dragonFireGround"), new FireDragonTabulaModelAnimator());
			icedragon_model = new IceAndFireTabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/icedragon/dragonIceGround"), new IceDragonTabulaModelAnimator());
		} catch (IOException e) {
			e.printStackTrace();
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityFireDragon.class, new RenderDragonBase(Minecraft.getMinecraft().getRenderManager(), firedragon_model, true));
		RenderingRegistry.registerEntityRenderingHandler(EntityIceDragon.class, new RenderDragonBase(Minecraft.getMinecraft().getRenderManager(), icedragon_model, false));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonEgg.class, new RenderDragonEgg(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonArrow.class, new RenderDragonArrow(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonSkull.class, new RenderDragonSkull(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonFire.class, new RenderNothing(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonFireCharge.class, new RenderDragonFireCharge(Minecraft.getMinecraft().getRenderManager(), true));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonIceProjectile.class, new RenderNothing(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonIceCharge.class, new RenderDragonFireCharge(Minecraft.getMinecraft().getRenderManager(), false));
		RenderingRegistry.registerEntityRenderingHandler(EntitySnowVillager.class, new RenderSnowVillager(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityHippogryphEgg.class, new RenderSnowball(Minecraft.getMinecraft().getRenderManager(), ModItems.hippogryph_egg, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityHippogryph.class, new RenderHippogryph(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityStoneStatue.class, new RenderStoneStatue(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityGorgon.class, new RenderGorgon(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityPixie.class, new RenderPixie(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCyclops.class, new RenderCyclops(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySiren.class, new RenderSiren(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityHippocampus.class, new RenderHippocampus(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDeathWorm.class, new RenderDeathWorm(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDeathWormEgg.class, new RenderSnowball(Minecraft.getMinecraft().getRenderManager(), ModItems.deathworm_egg, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCockatrice.class, new RenderCockatrice(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCockatriceEgg.class, new RenderSnowball(Minecraft.getMinecraft().getRenderManager(), ModItems.rotten_egg, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityStymphalianBird.class, new RenderStymphalianBird(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityStymphalianFeather.class, new RenderStymphalianFeather(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityStymphalianArrow.class, new RenderStymphalianArrow(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTroll.class, new RenderTroll(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexWorker.class, new RenderMyrmexBase(Minecraft.getMinecraft().getRenderManager(), new ModelMyrmexWorker(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexSoldier.class, new RenderMyrmexBase(Minecraft.getMinecraft().getRenderManager(), new ModelMyrmexSoldier(), 0.75F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexQueen.class, new RenderMyrmexBase(Minecraft.getMinecraft().getRenderManager(), new ModelMyrmexQueen(), 1.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexEgg.class, new RenderMyrmexEgg(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexSentinel.class, new RenderMyrmexBase(Minecraft.getMinecraft().getRenderManager(), new ModelMyrmexSentinel(), 0.85F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMyrmexRoyal.class, new RenderMyrmexBase(Minecraft.getMinecraft().getRenderManager(), new ModelMyrmexRoyal(), 0.75F));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPodium.class, new RenderPodium());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLectern.class, new RenderLectern());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEggInIce.class, new RenderEggInIce());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPixieHouse.class, new RenderPixieHouse());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJar.class, new RenderJar());
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 0, TileEntityPixieHouse.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 1, TileEntityPixieHouse.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 2, TileEntityPixieHouse.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 3, TileEntityPixieHouse.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 4, TileEntityPixieHouse.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.pixieHouse), 5, TileEntityPixieHouse.class);

	}

	@SideOnly(Side.CLIENT)
	public void spawnParticle(String name, World world, double x, double y, double z, double motX, double motY, double motZ) {
		net.minecraft.client.particle.Particle particle = null;
		if (name.equals("dragonfire")) {
			particle = new ParticleDragonFire(world, x, y, z, motX, motY, motZ);
		}
		if (name.equals("dragonice")) {
			particle = new ParticleDragonIce(world, x, y, z, motX, motY, motZ);
		}
		if (name.equals("blood")) {
			particle = new ParticleBlood(world, x, y, z);
		}
		if (name.equals("if_pixie")) {
			particle = new ParticlePixieDust(world, x, y, z, (float) motX, (float) motY, (float) motZ);
		}
		if (name.equals("siren_appearance")) {
			particle = new ParticleSirenAppearance(world, x, y, z);
		}
		if (name.equals("siren_music")) {
			particle = new ParticleSirenMusic(world, x, y, z, motX, motY, motZ);
		}
		if (particle != null) {
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void openBestiaryGui(ItemStack book) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiBestiary(book));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void openMyrmexStaffGui(ItemStack staff) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiMyrmexStaff(staff));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void openMyrmexAddRoomGui(ItemStack staff, BlockPos pos, EnumFacing facing) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiMyrmexAddRoom(staff, pos, facing));
	}


	@SideOnly(Side.CLIENT)
	public Object getArmorModel(int armorId) {
		switch (armorId) {
			case 0:
				return FIRE_DRAGON_SCALE_ARMOR_MODEL;
			case 1:
				return FIRE_DRAGON_SCALE_ARMOR_MODEL_LEGS;
			case 2:
				return ICE_DRAGON_SCALE_ARMOR_MODEL;
			case 3:
				return ICE_DRAGON_SCALE_ARMOR_MODEL_LEGS;
			case 4:
				return DEATHWORM_ARMOR_MODEL;
			case 5:
				return DEATHWORM_ARMOR_MODEL_LEGS;
			case 6:
				return TROLL_ARMOR_MODEL;
			case 7:
				return TROLL_ARMOR_MODEL_LEGS;
		}
		return null;
	}

	public Object getFontRenderer() {
		return this.bestiaryFontRenderer;
	}

	public int getDragon3rdPersonView() {
		return thirdPersonViewDragon;
	}

	public void setDragon3rdPersonView(int view) {
		thirdPersonViewDragon = view;
	}

	public static MyrmexHive getReferedClientHive(){
		return referedClientHive;
	}

	public static void setReferedClientHive(MyrmexHive hive){
		referedClientHive = hive;
	}
}
