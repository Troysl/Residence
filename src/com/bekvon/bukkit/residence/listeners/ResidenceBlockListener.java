/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.residence.listeners;

import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

/**
 *
 * @author Administrator
 */
public class ResidenceBlockListener extends BlockListener {

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled())
            return;
        Player player = event.getPlayer();
        if(Residence.getPermissionManager().isResidenceAdmin(player))
            return;
        ClaimedResidence res;
        if(Residence.getConfig().enabledRentSystem())
        {
            String resname = Residence.getResidenceManger().getNameByLoc(event.getBlock().getLocation());
            if(Residence.getRentManager().isRented(resname))
            {
                player.sendMessage("§cCannot modify a rented residence!");
                event.setCancelled(true);
                return;
            }
            res = Residence.getResidenceManger().getByName(resname);
        }
        else
        {
            res = Residence.getResidenceManger().getByLoc(event.getBlock().getLocation());
        }
        String pname = player.getName();
        if (res != null) {
            ResidencePermissions perms = res.getPermissions();
            boolean hasbuild = perms.playerHas(pname, "build", true);
            boolean hasdestroy = perms.playerHas(pname, "destroy", hasbuild);
            if ((!hasbuild && !hasdestroy) || !hasdestroy) {
                event.setCancelled(true);
                player.sendMessage("§cYou dont have permission to destroy here.");
            }
        } else {
            FlagPermissions perms = Residence.getWorldFlags().getPerms(player);
            boolean hasbuild = perms.has("build", true);
            boolean hasdestroy = perms.has("destroy", hasbuild);
            if((!hasbuild && !hasdestroy) || !hasdestroy)
            {
                event.setCancelled(true);
                player.sendMessage("§cWorld destroy is disabled.");
            }
        }
        super.onBlockBreak(event);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled())
            return;
        Player player = event.getPlayer();
        if(Residence.getPermissionManager().isResidenceAdmin(player))
            return;
        ClaimedResidence res;
        if(Residence.getConfig().enabledRentSystem())
        {
            String resname = Residence.getResidenceManger().getNameByLoc(event.getBlock().getLocation());
            if(Residence.getRentManager().isRented(resname))
            {
                player.sendMessage("§cCannot modify a rented residence!");
                event.setCancelled(true);
                return;
            }
            res = Residence.getResidenceManger().getByName(resname);
        }
        else
        {
            res = Residence.getResidenceManger().getByLoc(event.getBlock().getLocation());
        }
        String pname = player.getName();
        if (res != null) {
            ResidencePermissions perms = res.getPermissions();
            boolean hasbuild = perms.playerHas(pname, "build", true);
            boolean hasplace = perms.playerHas(pname, "place", hasbuild);
            if ((!hasbuild && !hasplace) || !hasplace) {
                event.setCancelled(true);
                player.sendMessage("§cYou dont have permission to build here.");
            }
        } else {
            FlagPermissions perms = Residence.getWorldFlags().getPerms(player);
            boolean hasbuild = perms.has("build", true);
            boolean hasplace = perms.has("place", hasbuild);
            if ((!hasbuild && !hasplace) || !hasplace) {
                event.setCancelled(true);
                player.sendMessage("§cWorld build is disabled.");
            }
        }
        //super.onBlockPlace(event);
    }

    @Override
    public void onBlockFromTo(BlockFromToEvent event) {
        if(event.isCancelled())
            return;
        ClaimedResidence res = Residence.getResidenceManger().getByLoc(event.getToBlock().getLocation());
        if (res != null) {
            if (!res.getPermissions().has("flow", true)) {
                event.setCancelled(true);
            }
        }
        //super.onBlockFromTo(event);
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        if(event.isCancelled())
            return;
        ClaimedResidence res = Residence.getResidenceManger().getByLoc(event.getBlock().getLocation());
        if(res!=null)
        {
            if(!res.getPermissions().has("firespread", true))
            {
                event.setCancelled(true);
            }
        }
        else
        {
            if (!Residence.getWorldFlags().getPerms(event.getBlock().getWorld().getName()).has("firespread", true)) {
                event.setCancelled(true);
            }
        }
        //super.onBlockBurn(event);
    }


    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        if(event.isCancelled())
            return;
        ClaimedResidence res = Residence.getResidenceManger().getByLoc(event.getBlock().getLocation());
        IgniteCause cause = event.getCause();
        if (res != null) {
            if(cause == IgniteCause.SPREAD)
            {
                if(!res.getPermissions().has("firespread", true))
                {
                    event.setCancelled(true);
                }
            }
            else if(cause == IgniteCause.FLINT_AND_STEEL) {
                Player player = event.getPlayer();
                if (!res.getPermissions().playerHas(player.getName(), "ignite", true) && !Residence.getPermissionManager().isResidenceAdmin(player)) {
                    event.setCancelled(true);
                    player.sendMessage("§cYou dont have permission to ignite here.");
                }
            }
            else
            {
                if(!res.getPermissions().has("ignite", true))
                {
                    event.setCancelled(true);
                }
            }
        } else {
            if(cause == IgniteCause.SPREAD)
            {
                if (!Residence.getWorldFlags().getPerms(event.getBlock().getWorld().getName()).has("firespread", true)) {
                    event.setCancelled(true);
                }
            }
            else
            {
                if(!Residence.getWorldFlags().getPerms(event.getBlock().getWorld().getName()).has("ignite", true))
                {
                    event.setCancelled(true);
                }
            }
        }
        //super.onBlockIgnite(event);
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.isCancelled())
            return;
        ClaimedResidence res = Residence.getResidenceManger().getByLoc(event.getBlock().getLocation());
        if (res != null) {
            if (!res.getPermissions().has("flow", true)) {
                event.setCancelled(true);
            }
        }
        super.onBlockPhysics(event);
    }
}
