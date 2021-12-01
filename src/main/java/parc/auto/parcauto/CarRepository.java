package parc.auto.parcauto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c WHERE c.id = ?1")
    Car findByID(long ID);

    @Query(value = "SELECT * FROM cars WHERE MATCH(marca,model,combustibil,descriere) AGAINST(?1)",nativeQuery = true)
    List<Car> search(String keyword);
}
