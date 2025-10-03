package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findCustomerByPhone(String phone);
    boolean existsCustomerByEmail(String email);
}
