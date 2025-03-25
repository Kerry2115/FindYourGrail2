import java.util.Scanner;

public class Menu {
    private UserService userService;
    private Scanner scanner;

    public Menu() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        Integer userId = null;

        while (userId == null) {
            System.out.println("1. Rejestracja");
            System.out.println("2. Logowanie");
            System.out.print("Wybierz opcję: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Odczytanie znaku nowej linii

            if (choice == 1) {
                registerUser();
            } else if (choice == 2) {
                userId = loginUser();
            } else {
                System.out.println("Nieprawidłowa opcja! Spróbuj ponownie.");
            }
        }

        showUserMenu(userId);
    }

    private void showUserMenu(int userId) {
        while (true) {
            System.out.println("\n1. Szukaj butów");
            System.out.println("2. Zobacz ulubione");
            System.out.println("3. Wyloguj się");
            System.out.print("Wybierz opcję: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Odczytanie znaku nowej linii

            if (choice == 1) {
                chooseShoes(userId);
            } else if (choice == 2) {
                userService.getFavorites(userId);
            } else if (choice == 3) {
                System.out.println("Wylogowano. Do zobaczenia!");
                return;
            } else {
                System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
            }
        }
    }

    private void registerUser() {
        System.out.print("Podaj nazwę użytkownika: ");
        String username = scanner.nextLine();

        System.out.print("Podaj hasło: ");
        String password = scanner.nextLine();

        System.out.print("Podaj e-mail: ");
        String email = scanner.nextLine();

        userService.registerUser(username, password, email);
    }

    private Integer loginUser() {
        System.out.print("Podaj nazwę użytkownika: ");
        String username = scanner.nextLine();

        System.out.print("Podaj hasło: ");
        String password = scanner.nextLine();

        return userService.loginUser(username, password);
    }

    private void chooseShoes(Integer userId) {
        System.out.print("Podaj producenta (lub pozostaw puste): ");
        String manufacturer = scanner.nextLine();

        System.out.print("Podaj model (lub pozostaw puste): ");
        String model = scanner.nextLine();

        ShoeFinder shoeFinder = new ShoeFinder(manufacturer, model, null, null, null, null, null, null);
        shoeFinder.find(userId);
    }
}
