package world_products_selling.world_products_selling.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import world_products_selling.world_products_selling.entity.Person;
import world_products_selling.world_products_selling.repository.PersonRepository;

public class PersonService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== loadUserByUsername вызван для: " + username);

        Person person = personRepository.findByEmailIgnoreCase(username);

        if (person == null) {
            System.out.println("=== Пользователь НЕ НАЙДЕН: " + username);
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }

        System.out.println("=== Пользователь найден: " + person.getEmail());
        System.out.println("=== Роль: " + person.getRole());
        System.out.println("=== Enabled: " + person.isEnabled());
        System.out.println("=== Password starts with: " + person.getPassword().substring(0, 10));

        return User.builder()
                .username(person.getEmail())
                .password(person.getPassword())
                .roles(person.getRole())
                .disabled(!person.isEnabled())
                .build();
    }
}