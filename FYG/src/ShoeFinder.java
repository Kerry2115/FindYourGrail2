import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Finder {
    protected String buildBaseQuery(String tableName) {
        return "SELECT id, manufacturer, model, color, collaboration, designer, height, price FROM " + tableName;
    }

    abstract void find(int userId);
}

class ShoeFinder extends Finder {
    private String manufacturer;
    private String model;
    private String color;
    private Boolean collaboration;
    private Boolean designer;
    private String height;
    private Double minPrice;
    private Double maxPrice;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/shoe_database";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public ShoeFinder(String manufacturer, String model, String color, Boolean collaboration, Boolean designer,
                      String height, Double minPrice, Double maxPrice) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.color = color;
        this.collaboration = collaboration;
        this.designer = designer;
        this.height = height;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public void find(int userId) {
        String query = buildQuery();
        List<Object> params = getQueryParameters();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Integer> shoeIds = new ArrayList<>();

            System.out.println("🔎 Znalezione buty:");
            while (rs.next()) {
                int shoeId = rs.getInt("id");
                shoeIds.add(shoeId);

                System.out.printf("ID: %d, Producent: %s, Model: %s, Kolor: %s, Kolaboracja: %b, Designer: %b, Wysokość: %s, Cena: %.2f\n",
                        shoeId,
                        rs.getString("manufacturer"),
                        rs.getString("model"),
                        rs.getString("color"),
                        rs.getBoolean("collaboration"),
                        rs.getBoolean("designer"),
                        rs.getString("height"),
                        rs.getDouble("price"));
            }

            if (!shoeIds.isEmpty()) {
                addFavorites(userId, shoeIds);
            } else {
                System.out.println(" Brak wyników dla podanych kryteriów.");
            }

        } catch (SQLException e) {
            System.err.println("Błąd SQL podczas wyszukiwania butów: " + e.getMessage());
        }
    }

    private String buildQuery() {
        StringBuilder query = new StringBuilder(buildBaseQuery("shoes"));
        List<String> conditions = new ArrayList<>();

        if (manufacturer != null && !manufacturer.isEmpty()) conditions.add("manufacturer = ?");
        if (model != null && !model.isEmpty()) conditions.add("model = ?");
        if (color != null && !color.isEmpty()) conditions.add("color = ?");
        if (collaboration != null) conditions.add("collaboration = ?");
        if (designer != null) conditions.add("designer = ?");
        if (height != null && !height.isEmpty()) conditions.add("height = ?");
        if (minPrice != null) conditions.add("price >= ?");
        if (maxPrice != null) conditions.add("price <= ?");

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        query.append(" ORDER BY price ASC LIMIT 50"); //  Ograniczamy wyniki do 50

        return query.toString();
    }

    private List<Object> getQueryParameters() {
        List<Object> params = new ArrayList<>();

        if (manufacturer != null && !manufacturer.isEmpty()) params.add(manufacturer);
        if (model != null && !model.isEmpty()) params.add(model);
        if (color != null && !color.isEmpty()) params.add(color);
        if (collaboration != null) params.add(collaboration);
        if (designer != null) params.add(designer);
        if (height != null && !height.isEmpty()) params.add(height);
        if (minPrice != null) params.add(minPrice);
        if (maxPrice != null) params.add(maxPrice);

        return params;
    }

    private void addFavorites(int userId, List<Integer> shoeIds) {
        try (Scanner scanner = new Scanner(System.in)) {
            for (int shoeId : shoeIds) {
                System.out.println("Czy chcesz dodać but o ID " + shoeId + " do ulubionych? (tak/nie): ");
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("tak")) {
                    new UserService().addFavorite(userId, shoeId);
                }
            }
        }
    }
}
