import java.sql.*;

public class UserService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shoe_database";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Rejestracja użytkownika
    public boolean registerUser(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);

            stmt.executeUpdate();
            System.out.println("Rejestracja zakończona sukcesem.");
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Błąd: Nazwa użytkownika lub e-mail już istnieją.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Logowanie użytkownika
    public Integer loginUser(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Logowanie udane.");
                    return rs.getInt("id");
                } else {
                    System.out.println("Nieprawidłowa nazwa użytkownika lub hasło.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Dodanie buta do ulubionych
    public void addFavorite(int userId, int shoeId) {
        String query = "INSERT INTO favorites (user_id, shoe_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, shoeId);
            stmt.executeUpdate();

            System.out.println("But został dodany do ulubionych.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // **Nowa metoda** - Pobieranie ulubionych butów użytkownika
    public void getFavorites(int userId) {
        String query = """
                SELECT s.id, s.manufacturer, s.model, s.color, s.price
                FROM favorites f
                JOIN shoes s ON f.shoe_id = s.id
                WHERE f.user_id = ?
                ORDER BY s.price ASC
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nTwoje ulubione buty:");
            boolean hasFavorites = false;

            while (rs.next()) {
                hasFavorites = true;
                System.out.printf("ID: %d | Producent: %s | Model: %s | Kolor: %s | Cena: %.2f zł\n",
                        rs.getInt("id"),
                        rs.getString("manufacturer"),
                        rs.getString("model"),
                        rs.getString("color"),
                        rs.getDouble("price"));
            }

            if (!hasFavorites) {
                System.out.println("Nie masz jeszcze żadnych butów w ulubionych.");
            }
        } catch (SQLException e) {
            System.err.println("Błąd SQL podczas pobierania ulubionych butów: " + e.getMessage());
        }
    }
}
