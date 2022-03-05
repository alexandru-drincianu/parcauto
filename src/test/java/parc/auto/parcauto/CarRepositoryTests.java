package parc.auto.parcauto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import parc.auto.parcauto.car.Car;
import parc.auto.parcauto.car.CarRepository;
import parc.auto.parcauto.user.User;
import parc.auto.parcauto.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CarRepositoryTests {
    @Autowired
    private UserRepository userrepo;

    @Autowired
    private CarRepository carrepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCar() {
        User user = new User();
        user.setEmail("test2@gmail.com");
        user.setPassword("test123");
        user.setFirstName("First2");
        user.setLastName("Last2");
        user.setPhoneNumber("000000001");
        Car car = new Car();
        car.setUser(user);
        car.setAn(2010);
        car.setCapacitateMotor("1800cm3");
        car.setCombustibil("motorina");
        car.setMarca("Audi");
        car.setModel("A3");
        car.setKm(187902);
        car.setPret(3500);

        User savedUser = userrepo.save(user);
        Car savedCar = carrepo.save(car);

        User existUser = entityManager.find(User.class,savedUser.getId());

        assertThat(existUser.getEmail()).isEqualTo(user.getEmail());
    }
}
