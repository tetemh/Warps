package fr.tetemh.warps.customclass;

import fr.tetemh.warps.Warps;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CWarp {
    private String name;
    private String slug;
    private String world_name;
    private Location spawn;
    private double price;
    private boolean active = true;

    public CWarp(String name, Location spawn){
        this.name = name;
        this.slug = name.toLowerCase().replace(" ", "_");
        this.world_name = spawn.getWorld().getName();
        this.spawn = spawn;
    }
    public CWarp(String name, Location spawn, double price){
        this.name = name;
        this.slug = name.toLowerCase().replace(" ", "_");
        this.world_name = spawn.getWorld().getName();
        this.spawn = spawn;
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public String getSlug() {
        return slug;
    }
    public double getPrice() {
        return price;
    }
    public Location getSpawn() {
        return spawn;
    }
    public boolean isActive() {
        return active;
    }

    public CWarp setPrice(double price) {
        this.price = price;
        return this;
    }
    public CWarp setSpawn(Location spawn) {
        this.spawn = spawn;
        return this;
    }
    public CWarp setActive(boolean active) {
        this.active = active;
        return this;
    }
    public CWarp teleport(Player player){
        player.teleport(this.spawn);
        return this;
    }

    public void save(){
        try {
            Connection connection = Warps.getLoader().getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO warps (slug, name, price, world, x, y, z, yaw, pitch, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE price = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?, active = ?");
            preparedStatement.setString(1, this.slug);
            preparedStatement.setString(2, this.name);
            preparedStatement.setDouble(3, this.price);
            preparedStatement.setString(4, this.world_name);
            preparedStatement.setDouble(5, this.spawn.getX());
            preparedStatement.setDouble(6, this.spawn.getY());
            preparedStatement.setDouble(7, this.spawn.getZ());
            preparedStatement.setDouble(8, this.spawn.getYaw());
            preparedStatement.setDouble(9, this.spawn.getPitch());
            preparedStatement.setBoolean(10, this.active);


            preparedStatement.setDouble(11, this.price);
            preparedStatement.setString(12, this.world_name);
            preparedStatement.setDouble(13, this.spawn.getX());
            preparedStatement.setDouble(14, this.spawn.getY());
            preparedStatement.setDouble(15, this.spawn.getZ());
            preparedStatement.setDouble(16, this.spawn.getYaw());
            preparedStatement.setDouble(17, this.spawn.getPitch());
            preparedStatement.setBoolean(18, this.active);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
