package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Appointment;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.AppointmentDto;
import swp391.fa25.saleElectricVehicle.repository.AppointmentRepository;
import swp391.fa25.saleElectricVehicle.repository.CustomerRepository;
import swp391.fa25.saleElectricVehicle.repository.ModelRepository;
import swp391.fa25.saleElectricVehicle.repository.StoreRepository;
import swp391.fa25.saleElectricVehicle.repository.UserRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ModelService modelService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        // Validate startTime and endTime
        if (appointmentDto.getStartTime() == null || appointmentDto.getEndTime() == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        if (appointmentDto.getStartTime().isAfter(appointmentDto.getEndTime())) {
            throw new AppException(ErrorCode.START_TIME_AFTER_END_TIME);
        }

        if (appointmentDto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
        }

        // Validate Model exists
        Model model = modelService.getModelEntityById(appointmentDto.getModelId());

        // Validate Customer exists
        Customer customer = customerService.getCustomerEntityById(appointmentDto.getCustomerId());

        // Validate Staff (User) exists
        User staff = userService.getUserEntityById(appointmentDto.getStaffId());

        // Validate Store exists
        Store store = storeService.getStoreEntityById(appointmentDto.getStoreId());

        // Set default status if not provided
        Appointment.AppointmentStatus status;
        if (appointmentDto.getStatus() != null) {
            status = appointmentDto.getStatus();
        } else {
            status = Appointment.AppointmentStatus.CONFIRMED;
        }

        Appointment newAppointment = Appointment.builder()
                .startTime(appointmentDto.getStartTime())
                .endTime(appointmentDto.getEndTime())
                .status(status)
                .createdAt(LocalDateTime.now())
                .model(model)
                .customer(customer)
                .user(staff)
                .store(store)
                .build();

        appointmentRepository.save(newAppointment);
        return mapToDto(newAppointment);
    }

    @Override
    public AppointmentDto getAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        return mapToDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(this::mapToDto).toList();
    }

        @Override
        public List<AppointmentDto> getAppointmentsByCustomerId(int customerId) {
            // Validate customer exists
                customerService.getCustomerEntityById(customerId);

            List<Appointment> appointments = appointmentRepository.findByCustomer_CustomerId(customerId);
            return appointments.stream().map(this::mapToDto).toList();
        }

    @Override
    public List<AppointmentDto> getAppointmentsByStaffId(int staffId) {
        // Validate staff exists
        userService.getUserEntityById(staffId);
        List<Appointment> appointments = appointmentRepository.findByUser_UserId(staffId);
        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStoreId(int storeId) {
        // Validate store exists
        storeService.getStoreEntityById(storeId);
        List<Appointment> appointments = appointmentRepository.findByStore_StoreId(storeId);
        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByModelId(int modelId) {
        // Validate model exists
        modelService.getModelEntityById(modelId);
        List<Appointment> appointments = appointmentRepository.findByModel_ModelId(modelId);
        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByStatus(status);
        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public void deleteAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        appointmentRepository.delete(appointment);
    }

    @Override
    public AppointmentDto updateAppointment(int id, AppointmentDto appointmentDto) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        // Update startTime if provided
        if (appointmentDto.getStartTime() != null) {
            if (appointmentDto.getStartTime().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
            }
            existingAppointment.setStartTime(appointmentDto.getStartTime());
        }

        // Update endTime if provided
        if (appointmentDto.getEndTime() != null) {
            existingAppointment.setEndTime(appointmentDto.getEndTime());
        }

        // Validate startTime before endTime after updates
        if (existingAppointment.getStartTime().isAfter(existingAppointment.getEndTime())) {
            throw new AppException(ErrorCode.START_TIME_AFTER_END_TIME);
        }

        // Update status if provided
        if (appointmentDto.getStatus() != null) {
            existingAppointment.setStatus(appointmentDto.getStatus());
        }

        // Update model if provided
        if (appointmentDto.getModelId() > 0
                && appointmentDto.getModelId() != existingAppointment.getModel().getModelId()) {
            Model model = modelService.getModelEntityById(appointmentDto.getModelId());
            existingAppointment.setModel(model);
        }

        // Update customer if provided
        if (appointmentDto.getCustomerId() > 0
                && appointmentDto.getCustomerId() != existingAppointment.getCustomer().getCustomerId()) {
            Customer customer = customerService.getCustomerEntityById(appointmentDto.getCustomerId());
            existingAppointment.setCustomer(customer);
        }

        // Update staff if provided
        if (appointmentDto.getStaffId() > 0
                && appointmentDto.getStaffId() != existingAppointment.getUser().getUserId()) {
            User staff = userService.getUserEntityById(appointmentDto.getStaffId());
            existingAppointment.setUser(staff);

        }

        // Update store if provided
        if (appointmentDto.getStoreId() > 0
                && appointmentDto.getStoreId() != existingAppointment.getStore().getStoreId()) {
            Store store = storeService.getStoreEntityById(appointmentDto.getStoreId());
            existingAppointment.setStore(store);
        }

        appointmentRepository.save(existingAppointment);
        return mapToDto(existingAppointment);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(int id, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        appointment.setStatus(status);
        appointmentRepository.save(appointment);
        return mapToDto(appointment);
    }

    private AppointmentDto mapToDto(Appointment appointment) {
        return AppointmentDto.builder()
                .appointmentId(appointment.getAppointmentId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .modelId(appointment.getModel().getModelId())
                .customerId(appointment.getCustomer().getCustomerId())
                .staffId(appointment.getUser().getUserId())
                .storeId(appointment.getStore().getStoreId())
                .build();
    }
}
