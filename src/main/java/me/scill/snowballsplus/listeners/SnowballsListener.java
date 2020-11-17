package me.scill.snowballsplus.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.scill.snowballsplus.SnowballsPlus;

public class SnowballsListener implements Listener {

	private final SnowballsPlus plugin;
	private boolean isSnowBreakable = true;

	public SnowballsListener(final SnowballsPlus plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSnowballMine(final BlockBreakEvent e) {
		if (!isSnowBreakable || e.getBlock().getType() != Material.SNOW)
			return;

		// If player can't build
		if (!canPlayerBuild(e.getPlayer(), e.getBlock()))
			return;

		// If shovel is required, and player does not have a shovel
		if (!e.getPlayer().getInventory().getItemInMainHand().getType().name().contains("SHOVEL")
				&& plugin.getConfig().getBoolean("snow-shovel-required"))
			return;

		e.setDropItems(false);
		e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(),
				new ItemStack(Material.SNOWBALL, ((Snow) e.getBlock().getBlockData()).getLayers()));
	}

	@EventHandler
	public void onSnowballHit(final ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball))
			return;

		// If the snowball hit a block.
		if (event.getHitBlock() != null) {
			final Block block = event.getHitBlock();

			// If player can't build.
			if (!(event.getEntity().getShooter() instanceof Player)
					|| !canPlayerBuild((Player) event.getEntity().getShooter(), block))
				return;

			if (plugin.getConfig().getBoolean("toggles.add-snow-layer") && block.getType() == Material.SNOW
					&& ((Snow) block.getBlockData()).getLayers() < 8) {
				final Snow snow = (Snow) block.getBlockData();
				snow.setLayers(snow.getLayers() + 1);
				block.setBlockData(snow);
			}

			else if (plugin.getConfig().getBoolean("toggles.break-glass") && block.getType().name().contains("GLASS"))
				block.breakNaturally();

			else if (plugin.getConfig().getBoolean("toggles.break-nonsolid-blocks") && !block.getType().isSolid()
					&& block.getType() != Material.SNOW)
				block.breakNaturally();

			else {
				final Block shiftedBlock = shiftBlock(block.getLocation(), event.getHitBlockFace());
				if (plugin.getConfig().getBoolean("toggles.form-snow") && (shiftedBlock.getType().name().contains("AIR")
						&& !shiftedBlock.getLocation().add(0, -1, 0).getBlock().getType().name().contains("AIR"))
						&& !shiftedBlock.getLocation().add(0, -1, 0).getBlock().isLiquid()
						&& (shiftedBlock.getLocation().add(0, -1, 0).getBlock().getType().isSolid()
								|| shiftedBlock.getLocation().add(0, -1, 0).getBlock().getType() == Material.SNOW))
					shiftedBlock.setType(Material.SNOW);
				else if (plugin.getConfig().getBoolean("toggles.form-ice") && shiftedBlock.getType() == Material.WATER)
					shiftedBlock.setType(Material.ICE);
				else if (plugin.getConfig().getBoolean("toggles.extinguish-fire")
						&& shiftedBlock.getType() == Material.FIRE)
					shiftedBlock.breakNaturally();
			}
		}

		// If snowball hit a mob.
		else if (event.getHitEntity() != null && event.getHitEntity() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) event.getHitEntity();
			if (plugin.getConfig().getDouble("snowball-damage") != 0)
				entity.damage(plugin.getConfig().getDouble("snowball-damage"));
			if (plugin.getConfig().getBoolean("toggles.extinguish-entity-burn") && entity.getFireTicks() != 0)
				entity.setFireTicks(0);
			if (plugin.getConfig().getBoolean("entity-slowness.toggle")
					&& plugin.getConfig().getDouble("entity-slowness.chance") > new Random().nextInt(100)) {
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
						(int) (20 * plugin.getConfig().getDouble("entity-slowness.time")),
						plugin.getConfig().getInt("entity-slowness.power") - 1));
			}
		}
	}

	private boolean canPlayerBuild(final Player player, final Block block) {
		final BlockBreakEvent event = new BlockBreakEvent(block, player);
		isSnowBreakable = false;
		Bukkit.getServer().getPluginManager().callEvent(event);
		isSnowBreakable = true;
		if (event.isCancelled())
			return false;
		return true;
	}

	private Block shiftBlock(final Location location, final BlockFace blockFace) {
		if (blockFace == BlockFace.UP)
			location.add(0, 1, 0);
		else if (blockFace == BlockFace.DOWN)
			location.add(0, -1, 0);
		else if (blockFace == BlockFace.NORTH)
			location.add(0, 0, -1);
		else if (blockFace == BlockFace.EAST)
			location.add(1, 0, 0);
		else if (blockFace == BlockFace.SOUTH)
			location.add(0, 0, 1);
		else if (blockFace == BlockFace.WEST)
			location.add(-1, 0, 0);
		return location.getBlock();
	}
}