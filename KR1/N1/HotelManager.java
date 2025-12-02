import java.io.*;
import java.util.*;

class Hotel {
    private String city;
    private String name;
    private int stars;

    public Hotel(String city, String name, int stars) {
        this.city = city;
        this.name = name;

        if (stars >= 2 && stars <= 4) {
            this.stars = stars;
        } else {
            throw new IllegalArgumentException("Количество звезд должно быть 2, 3 или 4");
        }
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public int getStars() {
        return stars;
    }

    public String toString() {
        return city + " " + name + " " + stars;
    }
}

public class HotelManager {
    private List<Hotel> hotels;

    public HotelManager() {
        hotels = new ArrayList<>();
    }

    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 3);
                if (parts.length < 3) continue;

                String city = parts[0];
                String name = parts[1];
                int stars = Integer.parseInt(parts[2]);

                if (stars >= 2 && stars <= 4) {
                    hotels.add(new Hotel(city, name, stars));
                }
            }
        }
    }

    public void displaySorted() {
        Map<String, List<Hotel>> grouped = new TreeMap<>();
        for (Hotel h : hotels) {
            grouped.computeIfAbsent(h.getCity(), k -> new ArrayList<>()).add(h);
        }

        for (List<Hotel> cityHotels : grouped.values()) {
            cityHotels.sort((h1, h2) -> Integer.compare(h2.getStars(), h1.getStars()));
        }

        System.out.println("Отели, отсортированные по городам (алфавитно) и звездам (убывание):");
        grouped.forEach((city, cityHotels) -> {
            for (Hotel h : cityHotels) {
                System.out.println(h);
            }
        });
    }

    public void displayByCity(String city) {
        System.out.println("\nОтели в городе '" + city + "':");
        boolean found = false;
        for (Hotel h : hotels) {
            if (h.getCity().equalsIgnoreCase(city)) {
                System.out.println(h);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Данного города нет в списке");
        }
    }

    public void displayCitiesByHotel(String hotelName) {
        System.out.println("\nГорода с отелем '" + hotelName + "':");
        Set<String> cities = new TreeSet<>();
        for (Hotel h : hotels) {
            if (h.getName().equalsIgnoreCase(hotelName)) {
                cities.add(h.getCity());
            }
        }
        if (cities.isEmpty()) {
            System.out.println("Отели с указанным названием не найдены");
        } else {
            for (String city : cities) {
                System.out.println(city);
            }
        }
    }
}