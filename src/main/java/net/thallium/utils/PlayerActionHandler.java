package net.thallium.utils;

import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.thallium.mixins.ICPacketPlayerDigging;

import java.util.List;

public class PlayerActionHandler {
    public static CPacketPlayerDigging createPlayerDiggingPacket(CPacketPlayerDigging.Action action, EnumFacing facing, BlockPos pos) {
        CPacketPlayerDigging packet = new CPacketPlayerDigging();
        ((ICPacketPlayerDigging) packet).setAction(action);
        ((ICPacketPlayerDigging) packet).setFacing(facing);
        ((ICPacketPlayerDigging) packet).setPosition(pos);

        return packet;
    }

    public EntityPlayerMP player;

    private boolean doesAttack;
    private int attackInterval;
    private int attackCooldown;

    private boolean doesUse;
    private int useInterval;
    private int useCooldown;

    private BlockPos currentBlock = new BlockPos(-1,-1,-1);
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    public PlayerActionHandler(EntityPlayerMP playerIn)
    {
        player = playerIn;
        stop();
    }

    public String toString() {
        return (doesAttack ? "t" : "f") + ":" +
                attackInterval + ":" +
                attackCooldown + ":" +
                (doesUse ? "t" : "f") + ":" +
                useInterval + ":" +
                useCooldown;
    }

    public PlayerActionHandler setAttack(int interval, int offset)
    {
        this.doesAttack = true;
        this.attackInterval = interval;
        this.attackCooldown = interval+offset;
        return this;
    }
    public PlayerActionHandler setUse(int interval, int offset)
    {
        this.doesUse = true;
        this.useInterval = interval;
        this.useCooldown = interval+offset;
        return this;
    }
    public PlayerActionHandler setUseForever()
    {
        this.doesUse = true;
        this.useInterval = 1;
        this.useCooldown = 1;
        return this;
    }
    public PlayerActionHandler setAttackForever()
    {
        this.doesAttack = true;
        this.attackInterval = 1;
        this.attackCooldown = 1;
        return this;
    }

    public PlayerActionHandler stop() {
        this.doesUse = false;
        this.doesAttack = false;
        resetBlockRemoving();
        return this;
    }

    public void onUpdate() {
        boolean used = false;

        if (doesUse && (--useCooldown)==0)
        {
            useCooldown = useInterval;
            used  = useOnce();
        }
        if (doesAttack)
        {
            if ((--attackCooldown) == 0)
            {
                attackCooldown = attackInterval;
                if (!(used)) attackOnce();
            }
            else
            {
                resetBlockRemoving();
            }
        }
    }

    public void attackOnce() {
        RayTraceResult raytraceresult = mouseOver();
        if(raytraceresult == null) return;

        switch (raytraceresult.typeOfHit)
        {
            case ENTITY:
                player.attackTargetEntityWithCurrentItem(raytraceresult.entityHit);
                this.player.swingArm(EnumHand.MAIN_HAND);
                break;
            case MISS:
                break;
            case BLOCK:
                BlockPos blockpos = raytraceresult.getBlockPos();
                if (player.getEntityWorld().getBlockState(blockpos).getMaterial() != Material.AIR)
                {
                    onPlayerDamageBlock(blockpos,raytraceresult.sideHit.getOpposite());
                    this.player.swingArm(EnumHand.MAIN_HAND);
                    break;
                }
        }
    }

    public boolean useOnce() {
        System.out.println(player.getName());
        RayTraceResult raytraceresult = mouseOver();
        for (EnumHand enumhand : EnumHand.values())
        {
            ItemStack itemstack = this.player.getHeldItem(enumhand);
            if (raytraceresult != null)
            {
                switch (raytraceresult.typeOfHit)
                {
                    case ENTITY:
                        Entity target = raytraceresult.entityHit;
                        Vec3d vec3d = new Vec3d(raytraceresult.hitVec.x - target.posX, raytraceresult.hitVec.y - target.posY, raytraceresult.hitVec.z - target.posZ);

                        boolean flag = player.canEntityBeSeen(target);
                        double d0 = 36.0D;

                        if (!flag)
                        {
                            d0 = 9.0D;
                        }

                        if (player.getDistanceSq(target) < d0)
                        {
                            EnumActionResult res = player.interactOn(target,enumhand);
                            if (res == EnumActionResult.SUCCESS)
                            {
                                return true;
                            }
                            res = target.applyPlayerInteraction(player, vec3d, enumhand);
                            if (res == EnumActionResult.SUCCESS)
                            {
                                return true;
                            }
                        }
                        break;
                    case MISS:
                        break;
                    case BLOCK:
                        BlockPos blockpos = raytraceresult.getBlockPos();

                        if (player.getEntityWorld().getBlockState(blockpos).getMaterial() != Material.AIR)
                        {
                            if(itemstack.isEmpty())
                                continue;
                            float x = (float) raytraceresult.hitVec.x;
                            float y = (float) raytraceresult.hitVec.y;
                            float z = (float) raytraceresult.hitVec.z;

                            EnumActionResult res = player.interactionManager.processRightClickBlock(player, player.getEntityWorld(), itemstack, enumhand, blockpos, raytraceresult.sideHit, x, y, z);
                            if (res == EnumActionResult.SUCCESS)
                            {
                                this.player.swingArm(enumhand);
                                return true;
                            }
                        }
                }
            }
            EnumActionResult res = player.interactionManager.processRightClick(player,player.getEntityWorld(),itemstack,enumhand);
            if (res == EnumActionResult.SUCCESS)
            {
                return true;
            }
        }
        return false;
    }

    private RayTraceResult rayTraceBlocks(double blockReachDistance) {
        Vec3d eyeVec = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLook(1.0F);
        Vec3d pointVec = eyeVec.add(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance);
        return player.getEntityWorld().rayTraceBlocks(eyeVec, pointVec, false, false, true);
    }

    public RayTraceResult mouseOver() {
        World world = player.getEntityWorld();
        if (world == null)
            return null;
        RayTraceResult result = null;

        Entity pointedEntity = null;
        double reach = player.isCreative() ? 5.0D : 4.5D;
        result = rayTraceBlocks(reach);
        Vec3d eyeVec = player.getPositionEyes(1.0F);
        boolean flag = !player.isCreative();
        if (player.isCreative()) reach = 6.0D;
        double extendedReach = reach;

        if (result != null)
        {
            extendedReach = result.hitVec.distanceTo(eyeVec);
            if (world.getBlockState(result.getBlockPos()).getMaterial() == Material.AIR)
                result = null;
        }

        Vec3d lookVec = player.getLook(1.0F);
        Vec3d pointVec = eyeVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        Vec3d hitVec = null;
        List<Entity> list = world.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox().expand(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach).grow(1.0D, 1.0D, 1.0D),
                Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity != null && entity.canBeCollidedWith())
        );
        double d2 = extendedReach;

        for (int j = 0; j < list.size(); ++j)
        {
            Entity entity1 = list.get(j);
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyeVec, pointVec);

            if (axisalignedbb.contains(eyeVec))
            {
                if (d2 >= 0.0D)
                {
                    pointedEntity = entity1;
                    hitVec = raytraceresult == null ? eyeVec : raytraceresult.hitVec;
                    d2 = 0.0D;
                }
            }
            else if (raytraceresult != null)
            {
                double d3 = eyeVec.distanceTo(raytraceresult.hitVec);

                if (d3 < d2 || d2 == 0.0D)
                {
                    if (entity1.getLowestRidingEntity() == player.getLowestRidingEntity())
                    {
                        if (d2 == 0.0D)
                        {
                            pointedEntity = entity1;
                            hitVec = raytraceresult.hitVec;
                        }
                    }
                    else
                    {
                        pointedEntity = entity1;
                        hitVec = raytraceresult.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null && flag && eyeVec.distanceTo(hitVec) > 3.0D)
        {
            pointedEntity = null;
            result = new RayTraceResult(RayTraceResult.Type.MISS, hitVec, (EnumFacing) null, new BlockPos(hitVec));
        }

        if (pointedEntity != null && (d2 < extendedReach || result == null))
        {
            result = new RayTraceResult(pointedEntity, hitVec);
        }

        return result;
    }

    public boolean clickBlock(BlockPos loc, EnumFacing face) {
        World world = player.getEntityWorld();
        if (player.interactionManager.getGameType()!= GameType.ADVENTURE)
        {
            if (player.interactionManager.getGameType() == GameType.SPECTATOR)
            {
                return false;
            }

            if (!player.capabilities.allowEdit)
            {
                ItemStack itemstack = player.getHeldItemMainhand();

                if (itemstack.isEmpty())
                {
                    return false;
                }

                if (!itemstack.canDestroy(world.getBlockState(loc).getBlock()))
                {
                    return false;
                }
            }
        }

        if (!world.getWorldBorder().contains(loc))
        {
            return false;
        }
        else
        {
            if (player.interactionManager.getGameType()==GameType.CREATIVE)
            {
                player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, face, loc));
                clickBlockCreative(world, loc, face);
                this.blockHitDelay = 5;
            }
            else if (!this.isHittingBlock || !(currentBlock.equals(loc)))
            {
                if (this.isHittingBlock)
                {
                    player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, face, this.currentBlock));
                }

                IBlockState iblockstate = world.getBlockState(loc);
                player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, face, loc));
                boolean flag = iblockstate.getMaterial() != Material.AIR;

                if (flag && this.curBlockDamageMP == 0.0F)
                {
                    iblockstate.getBlock().onBlockClicked(world, loc, player);
                }

                if (flag && iblockstate.getPlayerRelativeBlockHardness(player, world, loc) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(loc);
                }
                else
                {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.curBlockDamageMP = 0.0F;
                    world.sendBlockBreakProgress(player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    private void clickBlockCreative(World world, BlockPos pos, EnumFacing facing) {
        if (!world.extinguishFire(player, pos, facing))
        {
            onPlayerDestroyBlock(pos);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
            return true;
        }
        World world = player.getEntityWorld();
        if (player.interactionManager.getGameType()==GameType.CREATIVE && world.getWorldBorder().contains(posBlock))
        {
            this.blockHitDelay = 5;
            player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, directionFacing, posBlock));
            clickBlockCreative(world, posBlock, directionFacing);
            return true;
        }
        else if (posBlock.equals(currentBlock))
        {
            IBlockState iblockstate = world.getBlockState(posBlock);

            if (iblockstate.getMaterial() == Material.AIR)
            {
                this.isHittingBlock = false;
                return false;
            }
            else
            {
                this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(player, world, posBlock);

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, directionFacing, posBlock));
                    this.onPlayerDestroyBlock(posBlock);
                    this.curBlockDamageMP = 0.0F;
                    this.blockHitDelay = 5;
                }
                //player.getEntityId()
                //send to all, even the breaker
                world.sendBlockBreakProgress(-1, this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        }
        else
        {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    private boolean onPlayerDestroyBlock(BlockPos pos) {
        World world = player.getEntityWorld();
        if (player.interactionManager.getGameType()!=GameType.ADVENTURE)
        {
            if (player.interactionManager.getGameType() == GameType.SPECTATOR)
            {
                return false;
            }

            if (player.capabilities.allowEdit)
            {
                ItemStack itemstack = player.getHeldItemMainhand();

                if (itemstack.isEmpty())
                {
                    return false;
                }

                if (!itemstack.canDestroy(world.getBlockState(pos).getBlock()))
                {
                    return false;
                }
            }
        }

        if (player.interactionManager.getGameType()==GameType.CREATIVE && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !player.canUseCommandBlock())
            {
                return false;
            }
            else if (iblockstate.getMaterial() == Material.AIR)
            {
                return false;
            }
            else
            {
                world.playEvent(2001, pos, Block.getStateId(iblockstate));
                block.onBlockHarvested(world, pos, iblockstate, player);
                boolean flag = world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);

                if (flag)
                {
                    block.onPlayerDestroy(world, pos, iblockstate);
                }

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!(player.interactionManager.getGameType()==GameType.CREATIVE))
                {
                    ItemStack itemstack1 = player.getHeldItemMainhand();

                    if (!itemstack1.isEmpty())
                    {
                        itemstack1.onBlockDestroyed(world, iblockstate, pos, player);

                        if (itemstack1.isEmpty())
                        {
                            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                }

                return flag;
            }
        }
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock)
        {
            player.connection.processPlayerDigging(createPlayerDiggingPacket(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, EnumFacing.DOWN, this.currentBlock));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            player.getEntityWorld().sendBlockBreakProgress(player.getEntityId(), this.currentBlock, -1);
            player.resetCooldown();
            this.currentBlock = new BlockPos(-1,-1,-1);
        }
    }
}