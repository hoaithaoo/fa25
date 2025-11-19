package swp391.fa25.saleElectricVehicle.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.entity.Appointment;
import swp391.fa25.saleElectricVehicle.entity.Customer;
import swp391.fa25.saleElectricVehicle.entity.Model;
import swp391.fa25.saleElectricVehicle.entity.Store;
import swp391.fa25.saleElectricVehicle.entity.TestDriveConfig;
import swp391.fa25.saleElectricVehicle.entity.User;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.AppointmentStatus;
import swp391.fa25.saleElectricVehicle.exception.AppException;
import swp391.fa25.saleElectricVehicle.exception.ErrorCode;
import swp391.fa25.saleElectricVehicle.payload.dto.AppointmentDto;
import swp391.fa25.saleElectricVehicle.payload.request.appointment.CreateAppointmentRequest;
import swp391.fa25.saleElectricVehicle.repository.AppointmentRepository;
import swp391.fa25.saleElectricVehicle.service.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Autowired
    private TestDriveConfigService testDriveConfigService;

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        // Validate exists
        Model model = modelService.getModelEntityById(request.getModelId());
        Customer customer = customerService.getCustomerEntityById(request.getCustomerId());
        User staff = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(staff.getUserId());

        // Lấy TestDriveConfig của store
        TestDriveConfig config = testDriveConfigService.getTestDriveConfigEntity();

        // Validate startTime
        if (request.getStartTime() == null) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
        }

        // Validate startTime nằm trong khoảng giờ làm việc của config
        LocalTime requestTime = request.getStartTime().toLocalTime();
        LocalTime configStartTime = config.getStartTime();
        LocalTime configEndTime = config.getEndTime();
        
        if (requestTime.isBefore(configStartTime) || requestTime.isAfter(configEndTime) || requestTime.equals(configEndTime)) {
            throw new AppException(ErrorCode.APPOINTMENT_TIME_OUT_OF_RANGE);
        }

        // Tính endTime dựa trên appointmentDurationMinutes
        LocalDateTime endTime = request.getStartTime().plusMinutes(config.getAppointmentDurationMinutes());

        // Kiểm tra endTime không vượt quá giờ làm việc
        LocalTime endTimeLocal = endTime.toLocalTime();
        if (endTimeLocal.isAfter(configEndTime)) {
            throw new AppException(ErrorCode.APPOINTMENT_TIME_OUT_OF_RANGE);
        }

        // Kiểm tra maxAppointmentsPerModelPerSlot (chỉ đếm CONFIRMED và IN_PROGRESS)
        // Đếm số appointments của model này trùng thời gian với appointment hiện tại
        long appointmentsCountInTimeRange = appointmentRepository.countAppointmentsByModelAndTimeRange(
            store.getStoreId(),
            model.getModelId(),
            request.getStartTime(),
            endTime,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.IN_PROGRESS
        );
        if (appointmentsCountInTimeRange >= config.getMaxAppointmentsPerModelPerSlot()) {
            throw new AppException(ErrorCode.APPOINTMENTS_PER_MODEL_PER_SLOT_EXCEEDED);
        }

        // Kiểm tra buffer time 15 phút (tránh thời gian trả xe trễ)
        LocalDateTime minStartTime = request.getStartTime().minusMinutes(15);
        long appointmentsTooClose = appointmentRepository.countAppointmentsEndingTooClose(
            store.getStoreId(),
            request.getStartTime(),
            minStartTime,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.IN_PROGRESS
        );
        if (appointmentsTooClose > 0) {
            throw new AppException(ErrorCode.APPOINTMENT_BUFFER_TIME_VIOLATED);
        }

        Appointment newAppointment = Appointment.builder()
                .model(model)
                .customer(customer)
                .user(staff)
                .store(store)
                .startTime(request.getStartTime())
                .endTime(endTime) // Sử dụng endTime tính từ config
                .status(AppointmentStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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

        // Lấy TestDriveConfig
        TestDriveConfig config = testDriveConfigService.getTestDriveConfigEntity();

        // Update startTime if provided
        if (appointmentDto.getStartTime() != null) {
            if (appointmentDto.getStartTime().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.PAST_APPOINTMENT_TIME);
            }

            // Validate startTime nằm trong khoảng giờ làm việc
            LocalTime requestTime = appointmentDto.getStartTime().toLocalTime();
            LocalTime configStartTime = config.getStartTime();
            LocalTime configEndTime = config.getEndTime();
            
            if (requestTime.isBefore(configStartTime) || requestTime.isAfter(configEndTime) || requestTime.equals(configEndTime)) {
                throw new AppException(ErrorCode.APPOINTMENT_TIME_OUT_OF_RANGE);
            }

            // Tính lại endTime dựa trên config
            LocalDateTime newEndTime = appointmentDto.getStartTime().plusMinutes(config.getAppointmentDurationMinutes());

            // Kiểm tra endTime không vượt quá giờ làm việc
            LocalTime newEndTimeLocal = newEndTime.toLocalTime();
            if (newEndTimeLocal.isAfter(configEndTime)) {
                throw new AppException(ErrorCode.APPOINTMENT_TIME_OUT_OF_RANGE);
            }

            // Xác định model để kiểm tra (có thể là model hiện tại hoặc model mới nếu đổi)
            Model modelToCheck = existingAppointment.getModel();
            if (appointmentDto.getModelId() > 0
                    && appointmentDto.getModelId() != existingAppointment.getModel().getModelId()) {
                modelToCheck = modelService.getModelEntityById(appointmentDto.getModelId());
            }

            // Kiểm tra maxAppointmentsPerModelPerSlot (chỉ đếm CONFIRMED và IN_PROGRESS)
            // Đếm số appointments của model này trùng thời gian với appointment hiện tại
            long appointmentsCountInTimeRange = appointmentRepository.countAppointmentsByModelAndTimeRange(
                store.getStoreId(),
                modelToCheck.getModelId(),
                appointmentDto.getStartTime(),
                newEndTime,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.IN_PROGRESS
            );
            
            // Loại trừ appointment hiện tại khỏi count nếu nó cùng model và trùng thời gian
            // Lý do: Khi update appointment, query sẽ tìm thấy appointment cũ trong database
            // Nếu không trừ đi, sẽ tự block chính mình và không thể update được
            // Ví dụ: Update appointment 13:00-14:00 sang 13:00-14:00 (chỉ đổi model) thì cần trừ đi
            if ((existingAppointment.getStatus() == AppointmentStatus.CONFIRMED || 
                 existingAppointment.getStatus() == AppointmentStatus.IN_PROGRESS) &&
                existingAppointment.getModel().getModelId() == modelToCheck.getModelId() &&
                existingAppointment.getStartTime().isBefore(newEndTime) &&
                existingAppointment.getEndTime().isAfter(appointmentDto.getStartTime())) {
                appointmentsCountInTimeRange--; // Trừ appointment hiện tại vì nó sẽ bị replace bằng appointment mới
            }
            if (appointmentsCountInTimeRange >= config.getMaxAppointmentsPerModelPerSlot()) {
                throw new AppException(ErrorCode.APPOINTMENTS_PER_MODEL_PER_SLOT_EXCEEDED);
            }

            // Kiểm tra buffer time 15 phút (tránh thời gian trả xe trễ)
            // Logic: Nếu có appointment CONFIRMED/IN_PROGRESS kết thúc trong 15 phút trước startTime mới -> không cho phép
            LocalDateTime minStartTime = appointmentDto.getStartTime().minusMinutes(15);
            long appointmentsTooClose = appointmentRepository.countAppointmentsEndingTooClose(
                store.getStoreId(),
                appointmentDto.getStartTime(),
                minStartTime,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.IN_PROGRESS
            );
            
            // Loại trừ appointment hiện tại khỏi count nếu endTime cũ nằm trong khoảng buffer
            // Lý do: Khi update appointment từ 13:00-14:00 sang 14:30-15:30, query sẽ tìm thấy appointment cũ (endTime 14:00)
            // trong khoảng buffer (14:15-14:30), nhưng đây là appointment đang được update, không phải appointment khác
            // Nếu không trừ đi, sẽ tự block chính mình
            if ((existingAppointment.getStatus() == AppointmentStatus.CONFIRMED || 
                 existingAppointment.getStatus() == AppointmentStatus.IN_PROGRESS) &&
                existingAppointment.getEndTime().isBefore(appointmentDto.getStartTime()) &&
                existingAppointment.getEndTime().isAfter(minStartTime)) {
                appointmentsTooClose--; // Trừ appointment hiện tại vì endTime cũ sẽ không còn nữa sau khi update
            }
            if (appointmentsTooClose > 0) {
                throw new AppException(ErrorCode.APPOINTMENT_BUFFER_TIME_VIOLATED);
            }

            existingAppointment.setStartTime(appointmentDto.getStartTime());
            existingAppointment.setEndTime(newEndTime);
        }

        // Update model if provided
        if (appointmentDto.getModelId() > 0
                && appointmentDto.getModelId() != existingAppointment.getModel().getModelId()) {
            Model newModel = modelService.getModelEntityById(appointmentDto.getModelId());
            existingAppointment.setModel(newModel);
        }

        existingAppointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(existingAppointment);
        return mapToDto(existingAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto updateAppointmentStatus(int id, AppointmentStatus status) {
        User currentUser = userService.getCurrentUserEntity();
        Store store = storeService.getCurrentStoreEntity(currentUser.getUserId());
        boolean isManager = currentUser.getRole().getRoleName().equalsIgnoreCase("Quản lý cửa hàng");
        
        // Get appointment with access control
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
        
        // Validation: Không cho phép thay đổi status nếu đã COMPLETED hoặc CANCELLED
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppException(ErrorCode.APPOINTMENT_NOT_EDITABLE);
        }
        
        // Validation: Không cho phép chuyển về CONFIRMED nếu đã COMPLETED hoặc CANCELLED
        // (Trường hợp này đã được check ở trên, nhưng để chắc chắn)
        if (status == AppointmentStatus.CONFIRMED && 
            (appointment.getStatus() == AppointmentStatus.COMPLETED || 
             appointment.getStatus() == AppointmentStatus.CANCELLED)) {
            throw new AppException(ErrorCode.APPOINTMENT_NOT_EDITABLE);
        }
        
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
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
