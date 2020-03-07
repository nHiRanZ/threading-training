import domain.Inventory;
import domain.Product;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EchoProtocol {
    public String processInput(String input) {
        return input.toUpperCase();
    }

    public static List<Inventory> processInventory(String request) {
        return Arrays.stream(request.split("\n")).
                map(s -> s.split(","))
                .map(o -> new Inventory(o[0], o[1]))
                .collect(Collectors.toList());
    }

    public static List<Product> processProducts(String request) {
        return Arrays.stream(request.split("\n")).
                map(s -> s.split(","))
                .map(o -> new Product(o[0], o[1], o[2]))
                .collect(Collectors.toList());
    }
}
