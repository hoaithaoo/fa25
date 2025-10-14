package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByCustomer_CustomerId(int customerId);
    List<Appointment> findByUser_UserId(int staffId);
    List<Appointment> findByStore_StoreId(int storeId);
    List<Appointment> findByModel_ModelId(int modelId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

}
