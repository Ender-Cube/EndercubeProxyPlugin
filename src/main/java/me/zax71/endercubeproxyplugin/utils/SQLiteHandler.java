package me.zax71.endercubeproxyplugin.utils;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Properties;


public class SQLiteHandler {
    private Connection CONNECTION;

    /**
     * Initializes a database with the given name
     */
    public SQLiteHandler() {
        createDatabase();
        createTable();
    }

    /**
     * Adds a time to the database
     *
     * @param player The player the time belongs to
     * @param time   The time in milliseconds
     */
    public void addTime(Player player, String course, Long time) {
        String sql = "INSERT INTO playerTimes(player,course,time) VALUES(?,?,?)";

        try {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            preparedStatement.setString(2, course);
            preparedStatement.setLong(3, time);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the specified players best times ordered by an index
     *
     * @param player player to retrieve data from
     * @param index  time to get, 1 for best
     * @return the nth best time of that player
     */
    @Nullable
    public Long getTimePlayer(Player player, String course, int index) {
        String sql = "SELECT * FROM playerTimes WHERE player = ? AND course = ? ORDER BY time ASC LIMIT 1 OFFSET ?;";

        try {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            preparedStatement.setString(2, course);
            preparedStatement.setInt(3, index - 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getLong("time");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Gets the specified players best times ordered by an index
     *
     * @param UUID  player's UUID to retrieve data from
     * @param index time to get, 1 for best
     * @return the nth best time of that player
     */
    @Nullable
    public Long getTimeUUID(String UUID, String course, int index) {
        String sql = "SELECT * FROM playerTimes WHERE player = ? AND course = ? ORDER BY time ASC LIMIT 1 OFFSET ?;";

        try {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, UUID);
            preparedStatement.setString(2, course);
            preparedStatement.setInt(3, index - 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getLong("time");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Retrieves the overall nth best time for a course
     *
     * @param course The course to get data for
     * @param index  The nth time you want
     * @return The time
     */
    @Nullable
    public Long getTimeOverall(String course, int index) {
        String sql = "SELECT * FROM playerTimes WHERE course = ? ORDER BY time ASC LIMIT 1 OFFSET ?;";

        try {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, course);
            preparedStatement.setInt(2, index - 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getLong("time");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Retrieves the player with the overall nth best time
     *
     * @param course the course to get data for
     * @param index  The nth time you want
     * @return the player's name
     */
    @Nullable
    public String getPlayerOverall(String course, int index) {
        String sql = "SELECT * FROM playerTimes WHERE course = ? ORDER BY time ASC LIMIT 1 OFFSET ?;";

        try {
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, course);
            preparedStatement.setInt(2, index - 1);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            JsonObject playerNameObject = MojangUtils.fromUuid(resultSet.getString("player"));

            if (playerNameObject == null) {
                return null;
            }

            return playerNameObject
                    .get("name")
                    .getAsString();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes all but the top ten times per player for all players
     */
    public void pruneDatabase() {
        // Thanks, ChatGPT <3
        String sql = """
                DELETE FROM playerTimes
                  WHERE (player, course, time) NOT IN (
                    SELECT player, course, time
                    FROM (
                      SELECT player, course, time,
                             ROW_NUMBER() OVER (PARTITION BY player, course ORDER BY time ASC) AS row_num
                      FROM playerTimes
                    ) AS subQuery
                    WHERE row_num <= 10
                  );        
                """;

        try {
            CONNECTION.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the table - only called in init
     */
    private void createTable() {
        String createTable = """
                CREATE TABLE IF NOT EXISTS playerTimes (
                    player text NOT NULL,
                    course text NOT NULL,
                    time bigint NOT NULL
                    
                );
                """;

        try {
            Statement statement = CONNECTION.createStatement();
            statement.execute(createTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the database file
     */
    private void createDatabase() {
        String url = "jdbc:mariadb://mariadb:3306/endercube?createDatabaseIfNotExist=true";

        Properties props = new Properties();
        props.setProperty("user", System.getenv("MARIADB_USER"));
        props.setProperty("password", System.getenv("MARIADB_PASSWORD"));

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            CONNECTION = DriverManager.getConnection(url, props);
            if (CONNECTION != null) {
                DatabaseMetaData meta = CONNECTION.getMetaData();
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}