package net.thallium.patches;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class EntityPlayerMPFake extends EntityPlayerMP {
	private double lastReportedPosX;
	private double lastReportedPosY;
	private double lastReportedPosZ;
	private double setX;
	private double setY;
	private double setZ;
	private float setYaw;
	private float setPitch;

	private final Scoreboard scoreboard = new FakeScoreboard();

	private EntityPlayerMPFake(MinecraftServer server, WorldServer worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn) {
		super(server, worldIn, profile, interactionManagerIn);
	}

	public static EntityPlayerMPFake createShadow(MinecraftServer server, EntityPlayerMP player)
	{
		player.getServer().getPlayerList().playerLoggedOut(player);
		player.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.duplicate_login"));
		WorldServer worldIn = server.getWorld(player.dimension);
		PlayerInteractionManager interactionManagerIn = new PlayerInteractionManager(worldIn);
		GameProfile gameprofile = player.getGameProfile();
		gameprofile = fixSkin(gameprofile);
		EntityPlayerMPFake playerShadow = new EntityPlayerMPFake(server, worldIn, gameprofile, interactionManagerIn);
		playerShadow.setSetPosition(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		server.getPlayerList().initializeConnectionToPlayer(new NetworkManagerFake(EnumPacketDirection.CLIENTBOUND), playerShadow);

		playerShadow.setHealth(player.getHealth());
		playerShadow.connection.setPlayerLocation(player.posX, player.posY,player.posZ, player.rotationYaw, player.rotationPitch);
		interactionManagerIn.setGameType(player.interactionManager.getGameType());
		playerShadow.stepHeight = 0.6F;

		server.getPlayerList().sendPacketToAllPlayersInDimension(new SPacketEntityHeadLook(playerShadow, (byte)(player.rotationYawHead * 256 / 360) ),playerShadow.dimension);
		server.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, playerShadow));
		server.getPlayerList().serverUpdateMovingPlayer(playerShadow);
		return playerShadow;
	}

	private static GameProfile fixSkin(GameProfile gameProfile) {
		if (!gameProfile.getProperties().containsKey("texture"))
			return TileEntitySkull.updateGameProfile(gameProfile);
		else
			return gameProfile;
	}

	@Override
	public void onKillCommand() {
		logout();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.onUpdateEntity();
		this.playerMoved();
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		logout();
	}

	private void logout() {
		this.dismountRidingEntity();
		getServer().getPlayerList().playerLoggedOut(this);
	}

	private void playerMoved() {
		if (posX != lastReportedPosX || posY != lastReportedPosY || posZ != lastReportedPosZ) {
			server.getPlayerList().serverUpdateMovingPlayer(this);
			lastReportedPosX = posX;
			lastReportedPosY = posY;
			lastReportedPosZ = posZ;
		}
	}

	private void setSetPosition(double x, double y, double z, float yaw, float pitch) {
		this.setX = x;
		this.setY = y;
		this.setZ = z;
		this.setYaw = yaw;
		this.setPitch = pitch;
	}

	public void resetToSetPosition() {
		setLocationAndAngles(setX, setY, setZ, setYaw, setPitch);
	}

	@Override
	public Scoreboard getWorldScoreboard() {
		return this.scoreboard;
	}

	private class FakeScoreboard extends Scoreboard {
		@Override
		public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreCriteria criteria) {
			return Lists.newArrayList();
		}
	}
}
