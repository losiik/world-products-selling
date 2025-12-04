package world_products_selling.world_products_selling.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private LocalDate birthday;

    private String password;

    private boolean enabled = true;

    private String role = "USER";

    @Column(insertable = true, updatable = false)
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime updated;

    // Конструкторы
    public Person() {
    }

    public Person(String email, String name, LocalDate birthday, String password, boolean enabled, String role) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public Person(String email, String name, LocalDate birthday, String password) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.password = password;
    }

    // Lifecycle callbacks
    @PrePersist
    void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updated = LocalDateTime.now();
    }

    // Геттеры
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    // Сеттеры
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    // toString
    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", enabled=" + enabled +
                ", role='" + role + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    // equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return id != null ? id.equals(person.id) : person.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}