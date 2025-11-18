package swp391.fa25.saleElectricVehicle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.fa25.saleElectricVehicle.entity.entity_enum.AppointmentStatus;
import swp391.fa25.saleElectricVehicle.payload.dto.AppointmentDto;
import swp391.fa25.saleElectricVehicle.payload.request.appointment.CreateAppointmentRequest;
import swp391.fa25.saleElectricVehicle.payload.response.ApiResponse;
import swp391.fa25.saleElectricVehicle.service.AppointmentService;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(@RequestBody CreateAppointmentRequest request) {
        AppointmentDto createdAppointment = appointmentService.createAppointment(request);
        ApiResponse<AppointmentDto> response = ApiResponse.<AppointmentDto>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create appointment successfully")
                .data(createdAppointment)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentById(@PathVariable int id) {
        AppointmentDto appointmentDto = appointmentService.getAppointmentById(id);
        ApiResponse<AppointmentDto> response = ApiResponse.<AppointmentDto>builder()
                .code(HttpStatus.OK.value())
                .message("Get appointment successfully")
                .data(appointmentDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Appointments fetched successfully")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByCustomerId(@PathVariable int customerId) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Appointments fetched successfully by customer")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByStaffId(@PathVariable int staffId) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsByStaffId(staffId);
        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
                .code(HttpStatus.OK.value())
                .message("Appointments fetched successfully by staff")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/store/{storeId}")
//    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByStoreId(@PathVariable int storeId) {
//        List<AppointmentDto> appointments = appointmentService.getAppointmentsByStoreId(storeId);
//        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Appointments fetched successfully by store")
//                .data(appointments)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/model/{modelId}")
//    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByModelId(@PathVariable int modelId) {
//        List<AppointmentDto> appointments = appointmentService.getAppointmentsByModelId(modelId);
//        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Appointments fetched successfully by model")
//                .data(appointments)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/status/{status}")
//    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
//        List<AppointmentDto> appointments = appointmentService.getAppointmentsByStatus(status);
//        ApiResponse<List<AppointmentDto>> response = ApiResponse.<List<AppointmentDto>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Appointments fetched successfully by status")
//                .data(appointments)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable int id) {
        appointmentService.deleteAppointmentById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete appointment successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointment(@PathVariable int id, @RequestBody AppointmentDto appointmentDto) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointment(id, appointmentDto);
        ApiResponse<AppointmentDto> response = ApiResponse.<AppointmentDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update appointment successfully")
                .data(updatedAppointment)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointmentStatus(
            @PathVariable int id,
            @RequestParam AppointmentStatus status) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
        ApiResponse<AppointmentDto> response = ApiResponse.<AppointmentDto>builder()
                .code(HttpStatus.OK.value())
                .message("Update appointment status successfully")
                .data(updatedAppointment)
                .build();
        return ResponseEntity.ok(response);
    }
}
