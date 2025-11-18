package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Appointment;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.AppointmentStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.AppointmentDto;
import swp391.fa25.saleElectricVehicle.payload.request.appointment.CreateAppointmentRequest;
import swp391.fa25.saleElectricVehicle.repository.AppointmentRepository;
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
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        // Validate exists
        Model model = modelService.getModelEntityById(request.getModelId());
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Validate startTime and endTime
        if (request.getStartTime() == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
        }

        Appointment newAppointment = Appointment.builder()
                .model(model)
                .customer(customer)
                .user(staff)
                .store(store)
                .startTime(request.getStartTime())
                .endTime(request.getStartTime().plusHours(1)) // Mặc định endTime là 1 giờ sau startTime
                .status(AppointmentStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(newAppointment);
        return mapToDto(newAppointment);
    }

    @Override
    public AppointmentDto getAppointmentById(int id) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        Appointment appointment;
        if (isManager) {
            // Manager: chỉ check store
            appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

            if (appointment.getStore().getStoreId() != store.getStoreId()) {
                throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
            }
        } else {
            // Staff: check cả store và userId
            appointment = appointmentRepository.findByStore_StoreIdAndUser_UserIdAndAppointmentId(
                    store.getStoreId(), currentUser.getUserId(), id);
            if (appointment == null) {
                throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
            }
        }

        return mapToDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        List<Appointment> appointments;
        if (isManager) {
            // Manager: xem tất cả appointments trong store
            appointments = appointmentRepository.findByStore_StoreId(store.getStoreId());
        } else {
            // Staff: chỉ xem appointments của chính họ trong store
            appointments = appointmentRepository.findByStore_StoreIdAndUser_UserId(
                    store.getStoreId(), currentUser.getUserId());
        }

        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByCustomerId(int customerId) {
        // Validate customer exists
        customerService.getCustomerEntityById(customerId);

        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        List<Appointment> appointments;
        if (isManager) {
            // Manager: xem appointments của customer trong store
            appointments = appointmentRepository.findByStore_StoreIdAndCustomer_CustomerId(
                    store.getStoreId(), customerId);
        } else {
            // Staff: chỉ xem appointments của customer mà họ tạo trong store
            appointments = appointmentRepository.findByStore_StoreIdAndUser_UserIdAndCustomer_CustomerId(
                    store.getStoreId(), currentUser.getUserId(), customerId);
        }

        return appointments.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStaffId(int staffId) {
        // Validate staff exists
        userService.getUserEntityById(staffId);

        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        List<Appointment> appointments;
        if (isManager) {
            // Manager: xem appointments của staff trong store
            appointments = appointmentRepository.findByStore_StoreIdAndUser_UserId(
                    store.getStoreId(), staffId);
        } else {
            // Staff: chỉ xem appointments của chính họ trong store
            if (staffId != currentUser.getUserId()) {
                throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
            }
            appointments = appointmentRepository.findByStore_StoreIdAndUser_UserId(
                    store.getStoreId(), currentUser.getUserId());
        }

        return appointments.stream().map(this::mapToDto).toList();
    }

    // @Override
    // public List<AppointmentDto> getAppointmentsByStoreId(int storeId) {
    //     // Validate store exists
    //     storeService.getStoreEntityById(storeId);

    //     User currentUser = userService.getCurrentUserEntity();
    //     Store currentStore = storeService.getCurrentStoreEntity(currentUser.getUserId());
    //     boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

    //     // Only allow viewing appointments from own store
    //     if (storeId != currentStore.getStoreId()) {
    //         throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
    //     }

    //     List<Appointment> appointments;
    //     if (isManager) {
    //         // Manager: xem tất cả appointments trong store
    //         appointments = appointmentRepository.findByStore_StoreId(storeId);
    //     } else {
    //         // Staff: chỉ xem appointments của chính họ trong store
    //         appointments = appointmentRepository.findByStore_StoreIdAndUser_UserId(
    //                 storeId, currentUser.getUserId());
    //     }

    //     return appointments.stream().map(this::mapToDto).toList();
    // }

    // @Override
    // public List<AppointmentDto> getAppointmentsByModelId(int modelId) {
    //     // Validate model exists
    //     modelService.getModelEntityById(modelId);

    //     User currentUser = userService.getCurrentUserEntity();
    //     Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
    //     boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

    //     List<Appointment> appointments;
    //     if (isManager) {
    //         // Manager: xem appointments của model trong store
    //         appointments = appointmentRepository.findByStore_StoreIdAndModel_ModelId(
    //                 store.getStoreId(), modelId);
    //     } else {
    //         // Staff: chỉ xem appointments của model mà họ tạo trong store
    //         appointments = appointmentRepository.findByStore_StoreIdAndUser_UserIdAndModel_ModelId(
    //                 store.getStoreId(), currentUser.getUserId(), modelId);
    //     }

    //     return appointments.stream().map(this::mapToDto).toList();
    // }

    // @Override
    // public List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status) {
    //     User currentUser = userService.getCurrentUserEntity();
    //     Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
    //     boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

    //     List<Appointment> appointments;
    //     if (isManager) {
    //         // Manager: xem appointments theo status trong store
    //         appointments = appointmentRepository.findByStore_StoreIdAndStatus(
    //                 store.getStoreId(), status);
    //     } else {
    //         // Staff: chỉ xem appointments theo status mà họ tạo trong store
    //         appointments = appointmentRepository.findByStore_StoreIdAndUser_UserIdAndStatus(
    //                 store.getStoreId(), currentUser.getUserId(), status);
    //     }

    //     return appointments.stream().map(this::mapToDto).toList();
    // }

    @Override
    public void deleteAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        appointmentRepository.delete(appointment);
    }

    @Override
    public AppointmentDto updateAppointment(int id, AppointmentDto appointmentDto) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");

        // Get appointment with access control
        Appointment existingAppointment;
        if (isManager) {
            // Manager: chỉ check store
            existingAppointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

            if (existingAppointment.getStore().getStoreId() != store.getStoreId()) {
                throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
            }
        } else {
            // Staff: check cả store và userId
            existingAppointment = appointmentRepository.findByStore_StoreIdAndUser_UserIdAndAppointmentId(
                    store.getStoreId(), currentUser.getUserId(), id);
            if (existingAppointment == null) {
                throw new AppException(ErrorCode.APPOINTMENT_NOT_FOUND);
            }
        }

        // Update startTime if provided
        if (appointmentDto.getStartTime() != null) {
            if (appointmentDto.getStartTime().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
            }
            existingAppointment.setStartTime(appointmentDto.getStartTime());
            // Update endTime accordingly (1 hour after startTime)
            existingAppointment.setEndTime(appointmentDto.getStartTime().plusHours(1));
        }

        // Update model if provided
        if (appointmentDto.getModelId() > 0
                && appointmentDto.getModelId() != existingAppointment.getModel().getModelId()) {
            Model model = modelService.getModelEntityById(appointmentDto.getModelId());
            existingAppointment.setModel(model);
        }

        appointmentRepository.save(existingAppointment);
        return mapToDto(existingAppointment);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(int id, AppointmentStatus status) {
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
                .updatedAt(appointment.getUpdatedAt())
                .modelId(appointment.getModel().getModelId())
                .modelName(appointment.getModel().getModelName())
                .customerId(appointment.getCustomer().getCustomerId())
                .customerName(appointment.getCustomer().getFullName())
                .customerPhone(appointment.getCustomer().getPhone())
                .staffId(appointment.getUser().getUserId())
                .staffName(appointment.getUser().getFullName())
                .build();
    }
}
