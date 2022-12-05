package fr.tetemh.warps.managers;

import fr.tetemh.warps.Warps;
import fr.tetemh.warps.customclass.CWarp;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CWarpsManager {

    private static CWarpsManager instance;
    public CWarpsManager(){
        instance = this;
    }
    public static CWarpsManager getInstance() {
        return instance;
    }

    private Map<String, CWarp> warps = new HashMap<>();

    public Map<String, CWarp> getAllWarps(){
        return this.warps;
    }
    public Map<String, CWarp> getWarps() {
        return warps.values().stream().filter(CWarp::isActive).collect(Collectors.toMap(CWarp::getSlug, cWarp -> cWarp));
    }

    public CWarpsManager addWarp(CWarp cWarp){
        getAllWarps().put(cWarp.getSlug(), cWarp);
        return this;
    }
    public CWarpsManager removeWarp(String slug){
        getAllWarps().remove(slug);
        return this;
    }

    public void init(){
        try {
            Connection connection = Warps.getLoader().getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM warps");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                addWarp(new CWarp(resultSet.getString("name"), new Location(Warps.getInstance().getServer().getWorld(resultSet.getString("world")), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), Float.parseFloat(String.valueOf(resultSet.getDouble("yaw"))), Float.parseFloat(String.valueOf(resultSet.getDouble("pitch"))))).setPrice(resultSet.getDouble("price")));
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void save(){
        getAllWarps().values().forEach(CWarp::save);
    }
}
