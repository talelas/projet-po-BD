package db;

public final class DbConfig {
    private DbConfig() {}

    public static final String HOST = "HOST";
    public static final String PORT = "8888";
    public static final String DATABASE = "DATABASE";
    public static final String USER = "USER";
    public static final String PASSWORD = "PASSWORD";

    public static String jdbcUrl() {
        // Unicode + timezone settings avoid common MySQL JDBC issues.
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&useUnicode=true&characterEncoding=UTF-8"
                + "&serverTimezone=UTC";
    }
}
