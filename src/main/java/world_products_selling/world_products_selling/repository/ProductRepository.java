package world_products_selling.world_products_selling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import world_products_selling.world_products_selling.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT AVG(p.totalProfit / p.unitsSold) FROM Product p")
    Double findAverageProfitPerUnit();
}