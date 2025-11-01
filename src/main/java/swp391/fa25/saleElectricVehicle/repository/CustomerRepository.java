package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Customer;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findCustomerByPhone(String phone);
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerByIdentificationNumber(String identificationNumber);

    @Query("SELECT c FROM Customer c JOIN c.orders o JOIN o.user u WHERE u.userId = :staffId GROUP BY c")
    List<Customer> findCustomersByStaffId(int staffId);

    @Query("SELECT c FROM Customer c JOIN c.orders o JOIN o.user u JOIN u.store s WHERE s.storeId = :storeId")
    List<Customer> findCustomersByStore(int storeId);
}
