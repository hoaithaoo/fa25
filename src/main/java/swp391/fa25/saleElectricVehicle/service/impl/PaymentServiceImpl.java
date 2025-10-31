package swp391.fa25.saleElectricVehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.fa25.saleElectricVehicle.config.VNPayConfig;
import swp391.fa25.saleElectricVehicle.entity.Contract;
import swp391.fa25.saleElectricVehicle.payload.request.payment.PaymentRequest;
import swp391.fa25.saleElectricVehicle.repository.PaymentRepository;
import swp391.fa25.saleElectricVehicle.service.ContractService;
import swp391.fa25.saleElectricVehicle.service.PaymentService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;


}
