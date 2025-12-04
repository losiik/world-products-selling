package world_products_selling.world_products_selling.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import world_products_selling.world_products_selling.entity.Person;

public interface PersonRepository extends CrudRepository<Person, String> {

    Person findByEmailIgnoreCase(@Param("email") String email);
}
