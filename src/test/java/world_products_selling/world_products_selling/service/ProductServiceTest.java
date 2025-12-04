package world_products_selling.world_products_selling.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import world_products_selling.world_products_selling.entity.Product;
import world_products_selling.world_products_selling.repository.ProductRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
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
    void getAll_ShouldReturnAllProducts() {
        // Arrange
        Product product2 = new Product();
        product2.setId(2);
        product2.setRegion("Asia");

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        Iterable<Product> result = productService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, ((List<Product>) result).size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getOne_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getOne(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Germany", result.getCountry());
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void getOne_WhenProductDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getOne(999)
        );

        assertTrue(exception.getMessage().contains("Запись не существует"));
        verify(productRepository, times(1)).existsById(999);
        verify(productRepository, never()).findById(any());
    }

    @Test
    void add_WhenProductIsNew_ShouldSaveProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setRegion("Africa");
        newProduct.setCountry("Kenya");

        when(productRepository.save(newProduct)).thenReturn(newProduct);

        // Act
        Product result = productService.add(newProduct);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).save(newProduct);
        verify(productRepository, never()).existsById(any());
    }

    @Test
    void add_WhenProductWithIdDoesNotExist_ShouldSaveProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setId(10);
        newProduct.setRegion("Africa");

        when(productRepository.existsById(10)).thenReturn(false);
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        // Act
        Product result = productService.add(newProduct);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).existsById(10);
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    void add_WhenProductAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        testProduct.setId(1);
        when(productRepository.existsById(1)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.add(testProduct)
        );

        assertTrue(exception.getMessage().contains("Запись создана ранее"));
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_WhenProductExists_ShouldUpdateProduct() {
        // Arrange
        testProduct.setTotalProfit(6000.0);
        when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // Act
        Product result = productService.update(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(6000.0, result.getTotalProfit());
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void update_WhenProductDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        testProduct.setId(999);
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.update(testProduct)
        );

        assertTrue(exception.getMessage().contains("Запись не существует"));
        verify(productRepository, times(1)).existsById(999);
        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1);

        // Act
        productService.delete(1);

        // Assert
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_WhenProductDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.delete(999)
        );

        assertTrue(exception.getMessage().contains("Запись не существует"));
        verify(productRepository, times(1)).existsById(999);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void findAverageProfitPerUnit_ShouldReturnAverageProfit() {
        // Arrange
        double expectedAverage = 50.5;
        when(productRepository.findAverageProfitPerUnit()).thenReturn(expectedAverage);

        // Act
        double result = productService.findAverageProfitPerUnit();

        // Assert
        assertEquals(expectedAverage, result);
        verify(productRepository, times(1)).findAverageProfitPerUnit();
    }

    @Test
    void findAverageProfitPerUnit_WhenNoData_ShouldReturnZero() {
        // Arrange
        when(productRepository.findAverageProfitPerUnit()).thenReturn(0.0);

        // Act
        double result = productService.findAverageProfitPerUnit();

        // Assert
        assertEquals(0.0, result);
        verify(productRepository, times(1)).findAverageProfitPerUnit();
    }
}