package com.example.personas;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private static Connection conn;
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static String env(String key, String def) {
        String v = dotenv.get(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

    public static Connection get() throws SQLException {
        if (conn != null && !conn.isClosed()) return conn;

        String host = env("DB_HOST", "127.0.0.1");
        String port = env("DB_PORT", "5432");
        String db   = env("DB_NAME", "personas");
        String user = env("DB_USER", "postgres");
        String pass = env("DB_PASS", "armand99");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        conn = DriverManager.getConnection(url, user, pass);
        return conn;
    }

}