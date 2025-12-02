import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelManager manager = new HotelManager();
        Scanner scanner = new Scanner(System.in);

        try {
            manager.loadFromFile("hotels.txt");

            manager.displaySorted();

            System.out.print("\nВведите название города для поиска отелей: ");
            String city = scanner.nextLine();
            manager.displayByCity(city);

            System.out.print("\nВведите название отеля для поиска по городам: ");
            String hotelName = scanner.nextLine();
            manager.displayCitiesByHotel(hotelName);

        } catch (java.io.IOException e) {
            System.out.println("Ошибка при загрузке файла: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}