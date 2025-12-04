package world_products_selling.world_products_selling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import world_products_selling.world_products_selling.entity.Product;
import world_products_selling.world_products_selling.service.ProductService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setRegion("Europe");
        testProduct.setCountry("Germany");
        testProduct.setItemType("Office Supplies");
        testProduct.setSalesChannel("Online");
        testProduct.setOrderPriority("H");
        testProduct.setOrderDate(LocalDate.of(2024, 1, 15));
        testProduct.setUnitsSold(100);
        testProduct.setTotalProfit(5000.0);
    }

    @Test
    void getAll_ShouldReturnAllProducts() throws Exception {
        // Arrange
        Product product2 = new Product();
        product2.setId(2);
        product2.setRegion("Asia");
        product2.setCountry("Japan");

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productService.getAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].country", is("Germany")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].country", is("Japan")));

        verify(productService, times(1)).getAll();
    }

    @Test
    void getAll_WhenNoProducts_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(productService.getAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAll();
    }

    @Test
    void getOne_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getOne(1)).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.region", is("Europe")))
                .andExpect(jsonPath("$.country", is("Germany")))
                .andExpect(jsonPath("$.itemType", is("Office Supplies")))
                .andExpect(jsonPath("$.salesChannel", is("Online")))
                .andExpect(jsonPath("$.orderPriority", is("H")))
                .andExpect(jsonPath("$.unitsSold", is(100)))
                .andExpect(jsonPath("$.totalProfit", is(5000.0)));

        verify(productService, times(1)).getOne(1);
    }

    @Test
    void getOne_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        // Arrange
        when(productService.getOne(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует"));

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getOne(999);
    }

    @Test
    void add_WhenValidProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        Product newProduct = new Product();
        newProduct.setRegion("Africa");
        newProduct.setCountry("Kenya");
        newProduct.setItemType("Electronics");
        newProduct.setSalesChannel("Offline");
        newProduct.setOrderPriority("M");
        newProduct.setOrderDate(LocalDate.of(2024, 2, 1));
        newProduct.setUnitsSold(50);
        newProduct.setTotalProfit(2500.0);

        Product savedProduct = new Product();
        savedProduct.setId(10);
        savedProduct.setRegion(newProduct.getRegion());
        savedProduct.setCountry(newProduct.getCountry());
        savedProduct.setItemType(newProduct.getItemType());
        savedProduct.setSalesChannel(newProduct.getSalesChannel());
        savedProduct.setOrderPriority(newProduct.getOrderPriority());
        savedProduct.setOrderDate(newProduct.getOrderDate());
        savedProduct.setUnitsSold(newProduct.getUnitsSold());
        savedProduct.setTotalProfit(newProduct.getTotalProfit());

        when(productService.add(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.country", is("Kenya")))
                .andExpect(jsonPath("$.itemType", is("Electronics")));

        verify(productService, times(1)).add(any(Product.class));
    }

    @Test
    void add_WhenProductAlreadyExists_ShouldReturn409() throws Exception {
        // Arrange
        when(productService.add(any(Product.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Запись создана ранее"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isConflict());

        verify(productService, times(1)).add(any(Product.class));
    }

    @Test
    void update_WhenProductExists_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        testProduct.setTotalProfit(6000.0);
        when(productService.update(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.totalProfit", is(6000.0)));

        verify(productService, times(1)).update(any(Product.class));
    }

    @Test
    void update_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        // Arrange
        when(productService.update(any(Product.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует"));

        // Act & Assert
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).update(any(Product.class));
    }

    @Test
    void delete_WhenProductExists_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(productService).delete(1);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(1);
    }

    @Test
    void delete_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует"))
                .when(productService).delete(999);

        // Act & Assert
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).delete(999);
    }

    @Test
    void averageProfitPerUnit_ShouldReturnAverageValue() throws Exception {
        // Arrange
        double expectedAverage = 50.75;
        when(productService.findAverageProfitPerUnit()).thenReturn(expectedAverage);

        // Act & Assert
        mockMvc.perform(get("/api/products/avg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(50.75)));

        verify(productService, times(1)).findAverageProfitPerUnit();
    }

    @Test
    void averageProfitPerUnit_WhenNoData_ShouldReturnZero() throws Exception {
        // Arrange
        when(productService.findAverageProfitPerUnit()).thenReturn(0.0);

        // Act & Assert
        mockMvc.perform(get("/api/products/avg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(0.0)));

        verify(productService, times(1)).findAverageProfitPerUnit();
    }
}