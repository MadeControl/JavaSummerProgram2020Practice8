package com.epam.rd.java.basic.practice8.db;

import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DBManager {

    private static final Logger LOGGER = Logger.getLogger(DBManager.class.getSimpleName());
    private static final String FILE_PROPERTIES = "app.properties";
    private static final String CONNECTION_URL_KEY_IN_FILE_PROPERTIES = "connection.url";
    private static final String CONNECTION_URL = getConnectionURLFromFileProperties(FILE_PROPERTIES);

    private static DBManager dbManager;

    private DBManager() {
    }

    public static DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    public Connection getConnection(String connectionUrl) throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    ////////// Methods for User

    public void insertUser(User user) {
        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (user.getLogin() != null) {
                sqlQuery = "INSERT INTO users (login) VALUES ('" + user.getLogin() + "');";
            }

            statement.executeUpdate(sqlQuery);

            User user1 = getUser(user.getLogin());

            user.setId(user1.getId());

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

    }

    public User getUser(String login) {

        ResultSet resultSet = null;

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (login != null) {
                sqlQuery = "SELECT * FROM users WHERE users.login='" + login + "';";
            }

            resultSet = statement.executeQuery(sqlQuery);

            User user = new User();

            while (resultSet.next()) {

                long userId = resultSet.getLong("id");
                String userLogin = resultSet.getString("login");

                user.setId(userId);
                user.setLogin(userLogin);

            }

            resultSet.close();

            return user;

        } catch (SQLException e) {

            LOGGER.warning(e.getMessage());

        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    LOGGER.warning(e1.getMessage());
                }
            }
        }

        return null;
    }

    public List<User> findAllUsers() {

        List<User> users = new ArrayList<>();
        ResultSet resultSet = null;

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "SELECT * FROM users;";

            resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {

                long userId = resultSet.getLong("id");
                String userLogin = resultSet.getString("login");

                User user = new User();

                user.setId(userId);
                user.setLogin(userLogin);

                users.add(user);

            }

            resultSet.close();

            return users;

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    LOGGER.warning(e1.getMessage());
                }
            }
        }

        return users;
    }

    ////////// Methods for Team

    public void insertTeam(Team team) {

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (team.getName() != null) {
                sqlQuery = "INSERT INTO teams (name) VALUES ('" + team.getName() + "');";
            }

            statement.executeUpdate(sqlQuery);

            Team team1 = getTeam(team.getName());

            team.setId(team1.getId());

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public Team getTeam(String name) {

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (name != null) {
                sqlQuery = "SELECT * FROM teams WHERE teams.name='" + name + "';";
            }

            return returnTeam(statement, sqlQuery);

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

        return null;
    }

    public Team getTeamById(long id) {

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (id >= 0) {
                sqlQuery = "SELECT * FROM teams WHERE teams.id='" + id + "';";
            }

            return returnTeam(statement, sqlQuery);

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

        return null;
    }

    public List<Team> findAllTeams() {

        List<Team> teams = new ArrayList<>();
        ResultSet resultSet = null;

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "SELECT * FROM teams;";

            resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {


                long teamId = resultSet.getLong("id");
                String teamName = resultSet.getString("name");

                Team team = new Team();

                team.setId(teamId);
                team.setName(teamName);

                teams.add(team);

            }

            resultSet.close();

            return teams;

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    LOGGER.warning(e1.getMessage());
                }
            }
        }

        return teams;
    }

    public List<Team> getUserTeams(User user) {

        List<Team> userTeams = new ArrayList<>();
        ResultSet resultSet = null;

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (user != null) {
                sqlQuery = "SELECT * FROM users_teams WHERE user_id='" + user.getId() + "';";
            }

            resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {

                long teamId = resultSet.getLong("team_id");
                Team team = getTeamById(teamId);

                userTeams.add(team);

            }

            resultSet.close();

            return userTeams;

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    LOGGER.warning(e1.getMessage());
                }
            }
        }

        return userTeams;
    }

    public void setTeamsForUser(User user, Team... teams) {

        Connection connection = null;
        Statement statement = null;
        Savepoint savepoint = null;

        try {

            connection = getConnection(CONNECTION_URL);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            savepoint = connection.setSavepoint("SavePointOne");

            for (Team team : teams) {

                String sqlQuery = "";

                if (user != null && team != null) {
                    sqlQuery = "INSERT INTO users_teams (user_id, team_id) " +
                            "VALUES ('" + user.getId() + "','" + team.getId() + "');";
                }

                statement.executeUpdate(sqlQuery);

            }

            connection.commit();

        } catch (SQLException e) {

            if (connection != null) {
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e2) {
                    LOGGER.warning(e2.getMessage());
                }
            }

            LOGGER.warning(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e3) {
                LOGGER.warning(e3.getMessage());
            }
        }

    }

    public void deleteTeam(Team team) {

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (team != null) {
                sqlQuery = "DELETE FROM teams WHERE teams.name='" + team.getName() + "';";
            }

            statement.executeUpdate(sqlQuery);

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void updateTeam(Team team) {

        try (Connection connection = getConnection(CONNECTION_URL);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "";

            if (team != null) {
                sqlQuery = "UPDATE teams SET teams.name='" + team.getName()
                        + "' WHERE teams.id='" + team.getId() + "';";
            }

            statement.executeUpdate(sqlQuery);

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public static String getConnectionURLFromFileProperties(String fileName) {

        Properties properties = new Properties();
        File file = new File(fileName);

        try (FileReader fileReader = new FileReader(file)) {

            properties.load(fileReader);

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }

        return properties.getProperty(CONNECTION_URL_KEY_IN_FILE_PROPERTIES);

    }

    public Team returnTeam(Statement statement, String query) {

        Team team = new Team();
        ResultSet resultSet = null;

        try {

            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {

                long teamId = resultSet.getLong("id");
                String teamName = resultSet.getString("name");

                team.setId(teamId);
                team.setName(teamName);

            }

            return team;

        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e1) {
                    LOGGER.warning(e1.getMessage());
                }
            }
        }

        return null;
    }

}
