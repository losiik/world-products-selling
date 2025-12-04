package world_products_selling.world_products_selling.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import world_products_selling.world_products_selling.entity.Product;
import world_products_selling.world_products_selling.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public Iterable<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getOne(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует");
        }
        return productRepository.findById(id).orElse(null);
    }

    public Product add(Product product) {
        if (product.getId() != null)
            if (productRepository.existsById(product.getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Запись создана ранее");
        return productRepository.save(product);
    }

    public Product update(Product product) {
        if (!productRepository.existsById(product.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует");
        return productRepository.save(product);
    }

    public void delete(Integer id) {
        if (!productRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не существует");
        productRepository.deleteById(id);
    }

    public double findAverageProfitPerUnit(){
        return productRepository.findAverageProfitPerUnit();
    }
}
