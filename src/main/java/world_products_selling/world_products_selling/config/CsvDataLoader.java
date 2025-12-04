package world_products_selling.world_products_selling.config;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import world_products_selling.world_products_selling.entity.Product;
import world_products_selling.world_products_selling.repository.ProductRepository;
import world_products_selling.world_products_selling.service.ProductService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CsvDataLoader {
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),  // 11/20/2010
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),  // 08.12.2010
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),  // 08/12/2010
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),  // 2010-12-08
            DateTimeFormatter.ofPattern("dd-MM-yyyy")   // 08-12-2010
    );

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        dateString = dateString.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // next format
            }
        }

        return null;
    }

    @Bean
    CommandLineRunner loadData(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                System.out.println("Данные уже загружены. Пропускаем загрузку CSV.");
                return;
            }

            List<Product> products = new ArrayList<>();

            try (
                    InputStream is = ProductService.class.getResourceAsStream("/Продажа продуктов в мире.csv");
                    InputStreamReader streamReader = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(streamReader);
                    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            ) {
                List<String[]> lines = csvReader.readAll();

                for (String[] line : lines) {
                    Product product = new Product();
                    String region = line[0];
                    String country = line[1];
                    String itemType = line[2];
                    String salesChannel = line[3];
                    String orderPriority = line[4];
                    LocalDate orderDate = parseDate(line[5]);
                    Integer unitsSold = line[6] != null ? Integer.parseInt(line[6]) : null;
                    Double totalProfit = line[7] != null ? Double.parseDouble(line[7]) : null;

                    product.setRegion(region);
                    product.setCountry(country);
                    product.setItemType(itemType);
                    product.setSalesChannel(salesChannel);
                    product.setOrderPriority(orderPriority);
                    product.setOrderDate(orderDate);
                    product.setUnitsSold(unitsSold);
                    product.setTotalProfit(totalProfit);

                    products.add(product);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }

            repository.saveAll(products);
            System.out.println("Загружено " + products.size() + " продуктов из CSV");
        };
    }
}
