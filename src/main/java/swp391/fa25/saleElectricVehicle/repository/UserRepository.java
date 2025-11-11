package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Role;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findUsersByFullNameContaining(String name);
    User findByEmail(String email);
    
//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.store LEFT JOIN FETCH u.role WHERE u.email = :email")
//    User findByEmailWithStoreAndRole(@Param("email") String email);

//    User findUserByStoreAndRole(Store store, Role role);
}
