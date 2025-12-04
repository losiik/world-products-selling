package world_products_selling.world_products_selling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import world_products_selling.world_products_selling.entity.Product;
import world_products_selling.world_products_selling.service.ProductService;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public Iterable<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Integer id) {
        return productService.getOne(id);
    }

    @PostMapping
    public ResponseEntity<Product> add(
            @RequestBody Product product
    ) {
        Product p = productService.add(product);
        return new ResponseEntity<>(p, HttpStatus.CREATED);
    }

    @PutMapping
    public Product update(
            @RequestBody Product product
    ) {
        return productService.update(product);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Integer id
    ) {
        productService.delete(id);
    }

    @GetMapping("/avg")
    public Double AverageProfitPerUnit() {
        return productService.findAverageProfitPerUnit();
    }

}
