package swp391.fa25.saleElectricVehicle.service;

import swp391.fa25.saleElectricVehicle.entity.Appointment;
import swp391.fa25.saleElectricVehicle.payload.dto.AppointmentDto;

import java.util.List;

public interface AppointmentService {
    AppointmentDto createAppointment(AppointmentDto appointmentDto);
    AppointmentDto updateAppointment(int id, AppointmentDto appointmentDto);
    AppointmentDto getAppointmentById(int id);
    List<AppointmentDto> getAllAppointments();
    List<AppointmentDto> getAppointmentsByCustomerId(int customerId);
    List<AppointmentDto> getAppointmentsByStaffId(int staffId);
    List<AppointmentDto> getAppointmentsByStoreId(int storeId);
    List<AppointmentDto> getAppointmentsByModelId(int modelId);
    List<AppointmentDto> getAppointmentsByStatus(Appointment.AppointmentStatus status);
    void deleteAppointmentById(int id);
    AppointmentDto updateAppointmentStatus(int id, Appointment.AppointmentStatus status);
}
