package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockMyrmexBiolight;
import com.github.alexthe666.iceandfire.block.BlockMyrmexConnectedResin;
import com.github.alexthe666.iceandfire.block.BlockMyrmexResin;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public abstract class EntityMyrmexBase extends EntityTameable implements IAnimatedEntity, IMerchant {

    private static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntityMyrmexBase.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> GROWTH_STAGE = EntityDataManager.<Integer>createKey(EntityMyrmexBase.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> VARIANT = EntityDataManager.<Boolean>createKey(EntityMyrmexBase.class, DataSerializers.BOOLEAN);
    private int animationTick;
    private Animation currentAnimation;
    private MyrmexHive hive;
    public boolean isEnteringHive = false;
    public boolean isBeingGuarded = false;
    protected int growthTicks = 1;
    private static final ResourceLocation TEXTURE_DESERT_LARVA = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_larva.png");
    private static final ResourceLocation TEXTURE_DESERT_PUPA = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_pupa.png");
    private static final ResourceLocation TEXTURE_JUNGLE_LARVA = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_larva.png");
    private static final ResourceLocation TEXTURE_JUNGLE_PUPA = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_pupa.png");
    public static final Animation ANIMATION_PUPA_WIGGLE = Animation.create(20);
    @Nullable
    private EntityPlayer buyingPlayer;
    @Nullable
    private MerchantRecipeList buyingList;
    private java.util.UUID lastBuyingPlayer;
    private int careerId;
    private int careerLevel;
    private boolean needsInitilization;
    private int timeUntilReset;
    private int wealth;

    public EntityMyrmexBase(World worldIn) {
        super(worldIn);
        this.stepHeight = 2;
    }

    public boolean canMove() {
        return this.getGrowthStage() > 1 && !this.isTrading();
    }

    protected void updateAITasks() {
        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    for (MerchantRecipe merchantrecipe : this.buyingList) {
                        if (merchantrecipe.isRecipeDisabled()) {
                            merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                        }
                    }

                    this.populateBuyingList();
                    this.needsInitilization = false;

                    if (this.getHive() != null && this.lastBuyingPlayer != null) {
                        this.world.setEntityState(this, (byte) 14);
                        this.getHive().setWorld(this.world);
                        this.getHive().modifyPlayerReputation(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        super.updateAITasks();
    }

    protected int getExperiencePoints(EntityPlayer player) {
        return (this.getCasteImportance() * 7) + this.world.rand.nextInt(3);
    }

    @Override
    public boolean attackEntityFrom(DamageSource dmg, float i) {
        if (dmg == DamageSource.IN_WALL && this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getGrowthStage() < 2) {
            this.setAnimation(ANIMATION_PUPA_WIGGLE);
        }
        return super.attackEntityFrom(dmg, i);
    }

    protected float getJumpUpwardsMotion() {
        return 0.52F;
    }

    protected void jump() {
        this.motionY = (double) this.getJumpUpwardsMotion();
        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
            this.motionY += (double) ((float) (this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
        }
        float f = this.rotationYaw * 0.017453292F;
        this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
        this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
        this.isAirBorne = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
    }

    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F - this.world.getLightBrightness(pos);
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateGround(this, worldIn);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CLIMBING, Byte.valueOf((byte) 0));
        this.dataManager.register(GROWTH_STAGE, Integer.valueOf(2));
        this.dataManager.register(VARIANT, Boolean.valueOf(false));
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.getGrowthStage() < 2 && this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityMyrmexBase) {
            float yaw = this.getRidingEntity().rotationYaw;
            this.rotationYaw = yaw;
            this.rotationYawHead = yaw;
            this.renderYawOffset = 0;
            this.prevRenderYawOffset = 0;
        }
        this.setScaleForAge(false);
        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally && (this.onGround || !this.collidedVertically));
        }
        if (this.getGrowthStage() < 2) {
            growthTicks++;
            if (growthTicks == IceAndFire.CONFIG.myrmexLarvaTicks) {
                this.setGrowthStage(this.getGrowthStage() + 1);
                growthTicks = 0;
            }
        }
        if (!this.world.isRemote && this.getGrowthStage() < 2 && this.getRNG().nextInt(150) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_PUPA_WIGGLE);
        }

        if (this.getAttackTarget() != null && !(this.getAttackTarget() instanceof EntityPlayer) && this.getNavigator().noPath()) {
            this.setAttackTarget(null);
        }
        if (this.getAttackTarget() != null && (haveSameHive(this, this.getAttackTarget()) || this.getAttackTarget() instanceof EntityTameable && !canAttackTamable((EntityTameable)this.getAttackTarget()) || this.getAttackTarget() instanceof EntityPlayer && this.getHive() != null && !this.getHive().isPlayerReputationTooLowToFight(this.getAttackTarget().getUniqueID()))) {
            this.setAttackTarget(null);
        }

        if(this.getHealth() < this.getMaxHealth() && this.ticksExisted % 500 == 0 && this.isOnResin()){
            this.heal(1);
            this.world.setEntityState(this, (byte) 76);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("GrowthStage", this.getGrowthStage());
        tag.setInteger("GrowthTicks", growthTicks);
        tag.setBoolean("Variant", this.isJungle());
        if (this.getHive() != null) {
            tag.setUniqueId("HiveUUID", this.getHive().hiveUUID);
        }
        tag.setInteger("Career", this.careerId);
        tag.setInteger("CareerLevel", this.careerLevel);
        tag.setInteger("Riches", this.wealth);
        if (this.buyingList != null) {
            tag.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setGrowthStage(tag.getInteger("GrowthStage"));
        this.growthTicks = tag.getInteger("GrowthTicks");
        this.setJungleVariant(tag.getBoolean("Variant"));
        this.setHive(MyrmexWorldData.get(world).getHiveFromUUID(tag.getUniqueId("HiveUUID")));
        this.careerId = tag.getInteger("Career");
        this.careerLevel = tag.getInteger("CareerLevel");
        if (tag.hasKey("Offers", 10)) {
            NBTTagCompound nbttagcompound = tag.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound);
        }
        this.wealth = tag.getInteger("Riches");
    }

    public void setCustomer(@Nullable EntityPlayer player) {
        this.buyingPlayer = player;
    }

    @Nullable
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Nullable
    public MerchantRecipeList getRecipes(EntityPlayer player) {
        if (this.buyingList == null) {
            this.populateBuyingList();
        }

        return this.buyingList;
    }

    public void verifySellingItem(ItemStack stack) {
        if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(stack.isEmpty() ? ModSounds.MYRMEX_HURT : ModSounds.MYRMEX_IDLE, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public void useRecipe(MerchantRecipe recipe) {
        recipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        if(this.getHive() != null && this.getCustomer() != null){
            this.getHive().setWorld(this.world);
            this.getHive().modifyPlayerReputation(this.getCustomer().getUniqueID(), 2);
        }
        this.playSound(ModSounds.MYRMEX_IDLE, this.getSoundVolume(), this.getSoundPitch());
        int i = 3 + this.rand.nextInt(4);

        if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            if (this.buyingPlayer != null) {
                this.lastBuyingPlayer = this.buyingPlayer.getUniqueID();
            } else {
                this.lastBuyingPlayer = null;
            }

            i += 5;
        }

        if (recipe.getItemToBuy().getItem() == ModItems.myrmex_desert_resin || recipe.getItemToBuy().getItem() == ModItems.myrmex_jungle_resin) {
            this.wealth += recipe.getItemToBuy().getCount();
        }

        if (recipe.getRewardsExp()) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, i));
        }
    }

    private void populateBuyingList() {
        if (this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            this.careerId = this.getProfessionForge().getRandomCareer(this.rand) + 1;
            this.careerLevel = 1;
        }

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        int i = this.careerId - 1;
        int j = this.careerLevel - 1;
        java.util.List<EntityVillager.ITradeList> trades = this.getProfessionForge().getCareer(i).getTrades(j);

        if (trades != null) {
            for (EntityVillager.ITradeList entityvillager$itradelist : trades) {
                entityvillager$itradelist.addMerchantRecipe(this, this.buyingList, this.rand);
            }
        }
    }

    public boolean canAttackTamable(EntityTameable tameable){
        if(tameable.getOwner() != null && this.getHive() != null){
            return !this.getHive().isPlayerReputationTooLowToFight(tameable.getOwnerId());
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {
    }

    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        String s = this.getCustomNameTag();

        if (s != null && !s.isEmpty()) {
            TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, s));
            textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
            textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
            return textcomponentstring;
        } else {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }
            TextComponentString itextcomponent = new TextComponentString(this.getName());
            itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
            itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());

            if (team != null) {
                itextcomponent.getStyle().setColor(team.getColor());
            }

            return itextcomponent;
        }
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return new BlockPos(this);
    }

    public void setGrowthStage(int stage) {
        this.dataManager.set(GROWTH_STAGE, stage);
    }

    public int getGrowthStage() {
        return this.dataManager.get(GROWTH_STAGE).intValue();
    }

    public boolean isJungle() {
        return this.dataManager.get(VARIANT).booleanValue();
    }

    public void setJungleVariant(boolean isJungle) {
        this.dataManager.set(VARIANT, isJungle);
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isBesideClimbableBlock() {
        return (((Byte) this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = ((Byte) this.dataManager.get(CLIMBING)).byteValue();

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataManager.set(CLIMBING, Byte.valueOf(b0));
    }

    public boolean isOnLadder() {
        return super.isOnLadder();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE};
    }

    public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
        if(this.getHive() == null || livingBase == null || livingBase instanceof EntityPlayer && this.getHive().isPlayerReputationTooLowToFight(livingBase.getUniqueID())) {
            super.setRevengeTarget(livingBase);
            if (this.getHive() != null && livingBase != null) {
                this.getHive().addOrRenewAgressor(livingBase, this.getImportance());
            }
        }

        if (this.getHive() != null && livingBase != null) {
            if (livingBase instanceof EntityPlayer) {
                int i = -5 * this.getCasteImportance();
                this.getHive().setWorld(this.world);
                this.getHive().modifyPlayerReputation(livingBase.getUniqueID(), i);
                if (this.isEntityAlive()) {
                    this.world.setEntityState(this, (byte) 13);
                }
            }
        }
    }

    public void onDeath(DamageSource cause) {
        if (this.getHive() != null) {
            Entity entity = cause.getTrueSource();
            if (entity != null) {
                this.getHive().setWorld(this.world);
                this.getHive().modifyPlayerReputation(entity.getUniqueID(), -15);
            }
        }
        super.onDeath(cause);
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag2 = itemstack.getItem() == ModItems.myrmex_jungle_staff || itemstack.getItem() == ModItems.myrmex_desert_staff;

        if(flag2){
            this.onStaffInteract(player, itemstack);
            player.swingArm(hand);
            return true;
        }
        boolean flag = itemstack.getItem() == Items.NAME_TAG || itemstack.getItem() == Items.LEAD;
        if (flag) {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        } else if (!this.holdingSpawnEggOfClass(itemstack, this.getClass()) && this.getGrowthStage() >= 2 && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            if (hand == EnumHand.MAIN_HAND) {
                player.addStat(StatList.TALKED_TO_VILLAGER);
            }

            if (!this.world.isRemote && !this.buyingList.isEmpty() && (this.getHive() == null || !this.getHive().isPlayerReputationTooLowToTrade(player.getUniqueID()))) {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            } else if (this.buyingList.isEmpty()) {
                return super.processInteract(player, hand);
            }

            return true;
        } else {
            return super.processInteract(player, hand);
        }
    }

    public void onStaffInteract(EntityPlayer player, ItemStack itemstack) {
        UUID staffUUID = itemstack.getTagCompound().getUniqueId("HiveUUID");
        if(world.isRemote){
            return;
        }
        if(!player.isCreative()) {
            if ((this.getHive() != null && !this.getHive().canPlayerCommandHive(player.getUniqueID()))) {
                return;
            }
        }
        if(this.getHive() == null){
            player.sendStatusMessage(new TextComponentTranslation("myrmex.message.null_hive"), true);

        }else{
            if(staffUUID != null && staffUUID.equals(this.getHive().hiveUUID)){
                player.sendStatusMessage(new TextComponentTranslation("myrmex.message.staff_already_set"), true);
            }else {
                this.getHive().setWorld(this.world);
                EntityMyrmexQueen queen = this.getHive().getQueen();
                BlockPos center = this.getHive().getCenterGround();
                if (queen.hasCustomName()) {
                    player.sendStatusMessage(new TextComponentTranslation("myrmex.message.staff_set_named", queen.getCustomNameTag(), center.getX(), center.getY(), center.getZ()), true);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("myrmex.message.staff_set_unnamed", center.getX(), center.getY(), center.getZ()), true);
                }
                itemstack.getTagCompound().setUniqueId("HiveUUID", this.getHive().hiveUUID);
            }

        }

    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setHive(MyrmexWorldData.get(world).getNearestHive(this.getPosition(), 400));
        if(this.getHive() != null){
            this.setJungleVariant(isJungleBiome(world, this.getHive().getCenter()));
        }else{
            this.setJungleVariant(rand.nextBoolean());
        }
        this.populateBuyingList();
        return livingdata;
    }

    private static boolean isJungleBiome(World world, BlockPos position) {
        Biome biome = world.getBiome(position);
        return biome.topBlock != Blocks.SAND && biome.fillerBlock != Blocks.SAND && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY);
    }

    public abstract boolean shouldLeaveHive();

    public abstract boolean shouldEnterHive();

    @Override
    public void setScaleForAge(boolean baby) {
        this.setScale(this.getGrowthStage() == 0 ? 0.5F : this.getGrowthStage() == 1 ? 0.75F : 1F);
    }

    public abstract ResourceLocation getAdultTexture();

    public abstract float getModelScale();

    public ResourceLocation getTexture() {
        if (this.getGrowthStage() == 0) {
            return isJungle() ? TEXTURE_JUNGLE_LARVA : TEXTURE_DESERT_LARVA;
        } else if (this.getGrowthStage() == 1) {
            return isJungle() ? TEXTURE_JUNGLE_PUPA : TEXTURE_DESERT_PUPA;
        } else {
            return getAdultTexture();
        }
    }

    public MyrmexHive getHive() {
        return hive;
    }

    public void setHive(MyrmexHive newHive) {
        hive = newHive;
        if(hive != null){
            hive.addMyrmex(this);
        }
    }

    public static boolean haveSameHive(EntityMyrmexBase myrmex, Entity entity) {
        if (entity instanceof EntityMyrmexBase) {
            if (myrmex.getHive() != null && ((EntityMyrmexBase) entity).getHive() != null) {
                if (myrmex.isJungle() == ((EntityMyrmexBase) entity).isJungle()) {
                    return myrmex.getHive().getCenter() == ((EntityMyrmexBase) entity).getHive().getCenter();
                }
            }

        }
        if (entity instanceof EntityMyrmexEgg) {
            return myrmex.isJungle() == ((EntityMyrmexEgg) entity).isJungle();
        }
        return false;
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!haveSameHive(this, entityIn)) {
            entityIn.applyEntityCollision(this);
        }
    }

    public boolean canSeeSky() {
        return world.canBlockSeeSky(new BlockPos(this));
    }

    public static boolean isEdibleBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        if(block instanceof BlockMyrmexBiolight){
            return false;
        }
        return blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.PLANTS || blockState.getMaterial() == Material.VINE || blockState.getMaterial() == Material.CACTUS || block instanceof BlockBush || block instanceof BlockCactus || block instanceof BlockLeaves;
    }

    public boolean isOnResin() {
        IBlockState state = world.getBlockState(new BlockPos(this).down());
        IBlockState state2 = world.getBlockState(new BlockPos(this).down(2));
        return state.getBlock() instanceof BlockMyrmexResin || state2.getBlock() instanceof BlockMyrmexResin
                || state.getBlock() instanceof BlockMyrmexConnectedResin || state2.getBlock() instanceof BlockMyrmexConnectedResin;
    }


    public boolean isInNursery() {
        if (getHive() != null && getHive().getRooms(WorldGenMyrmexHive.RoomType.NURSERY).isEmpty() && getHive().getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.getRNG(), this.getPosition()) != null) {
            return false;
        }
        if (getHive() != null) {
            BlockPos nursery = getHive().getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.getRNG(), this.getPosition());
            return this.getDistanceSqToCenter(nursery) < 45;
        }
        return false;
    }

    @Override
    public void travel(float strafe, float forward, float vertical) {
        if (!this.canMove()) {
            strafe = 0;
            forward = 0;
            vertical = 0;
            super.travel(strafe, forward, vertical);
            return;
        }
        super.travel(strafe, forward, vertical);
    }

    public int getImportance() {
        if (this.getGrowthStage() < 2) {
            return 1;
        }
        return getCasteImportance();
    }

    public abstract int getCasteImportance();

    public boolean needsGaurding() {
        return true;
    }

    public boolean shouldMoveThroughHive() {
        return true;
    }

    public boolean shouldWander() {
        return this.getHive() == null;
    }

    public VillagerRegistry.VillagerProfession getProfessionForge() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 76) {
            this.playVillagerEffect();
        }else{
            super.handleStatusUpdate(id);
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return ModSounds.MYRMEX_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MYRMEX_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return ModSounds.MYRMEX_DIE;
    }

    protected void playStepSound(BlockPos pos, Block blockIn){
        this.playSound(ModSounds.MYRMEX_WALK, 0.16F * this.getMyrmexPitch() * (this.getRNG().nextFloat() * 0.6F + 0.4F), 1.0F);
    }

    protected void playBiteSound(){
        this.playSound(ModSounds.MYRMEX_BITE, 1.0F * this.getMyrmexPitch(), 1.0F);
    }

    protected void playStingSound(){
        this.playSound(ModSounds.MYRMEX_STING, 1.0F * this.getMyrmexPitch(), 0.6F);
    }

    protected void playVillagerEffect() {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.VILLAGER_HAPPY;
        for (int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
        }
    }

    public float getMyrmexPitch() {
        return width;
    }


    public static class BasicTrade implements EntityVillager.ITradeList {
        public ItemStack first;
        public ItemStack second;
        public EntityVillager.PriceInfo firstPrice;
        public EntityVillager.PriceInfo secondPrice;

        public BasicTrade(ItemStack first, ItemStack second, EntityVillager.PriceInfo firstPrice, EntityVillager.PriceInfo secondPrice) {
            this.first = first;
            this.second = second;
            this.firstPrice = firstPrice;
            this.secondPrice = secondPrice;
        }

        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int i = firstPrice.getPrice(random);
            int j = secondPrice.getPrice(random);
            recipeList.add(new MerchantRecipe(new ItemStack(first.getItem(), i, first.getItemDamage()), new ItemStack(second.getItem(), j, second.getItemDamage())));
        }
    }

    public static int getRandomCaste(World world, Random random, boolean royal){
        float rand = random.nextFloat();
        if(royal) {
            if (rand > 0.9) {
                return 2;//royal
            } else if (rand > 0.75) {
                return 3;//sentinel
            } else if (rand > 0.5) {
                return 1;//soldier
            } else {
                return 0;//worker
            }
        }else{
            if (rand > 0.8) {
                return 3;//sentinel
            } else if (rand > 0.6) {
                return 1;//soldier
            } else {
                return 0;//worker
            }
        }
    }

}
