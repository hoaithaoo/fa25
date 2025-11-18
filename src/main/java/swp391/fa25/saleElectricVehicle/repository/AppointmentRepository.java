package swp391.fa25.saleElectricVehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.fa25.saleElectricVehicle.entity.Appointment;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.AppointmentStatus;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByCustomer_CustomerId(int customerId);
    List<Appointment> findByUser_UserId(int staffId);
    List<Appointment> findByStore_StoreId(int storeId);
    List<Appointment> findByModel_ModelId(int modelId);
    List<Appointment> findByStatus(AppointmentStatus status);

    // Find appointments by store and user (for staff filtering)
    List<Appointment> findByStore_StoreIdAndUser_UserId(int storeId, int userId);

    // Find appointment by store, user and appointmentId (for staff filtering)
    Appointment findByStore_StoreIdAndUser_UserIdAndAppointmentId(int storeId, int userId, int appointmentId);

    // Find appointments by store and customer
    List<Appointment> findByStore_StoreIdAndCustomer_CustomerId(int storeId, int customerId);

    // Find appointments by store, user and customer (for staff filtering)
    List<Appointment> findByStore_StoreIdAndUser_UserIdAndCustomer_CustomerId(int storeId, int userId, int customerId);

//    // Find appointments by store and model
//    List<Appointment> findByStore_StoreIdAndModel_ModelId(int storeId, int modelId);
//
//    // Find appointments by store, user and model (for staff filtering)
//    List<Appointment> findByStore_StoreIdAndUser_UserIdAndModel_ModelId(int storeId, int userId, int modelId);
//
//    // Find appointments by store and status
//    List<Appointment> findByStore_StoreIdAndStatus(int storeId, AppointmentStatus status);
//
//    // Find appointments by store, user and status (for staff filtering)
//    List<Appointment> findByStore_StoreIdAndUser_UserIdAndStatus(int storeId, int userId, AppointmentStatus status);
}
