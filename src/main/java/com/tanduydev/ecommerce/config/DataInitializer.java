package com.tanduydev.ecommerce.config;

import com.tanduydev.ecommerce.enums.UserStatus;
import com.tanduydev.ecommerce.model.Role;
import com.tanduydev.ecommerce.model.User;
import com.tanduydev.ecommerce.repository.RoleRepository;
import com.tanduydev.ecommerce.repository.UserRepository;
import com.tanduydev.ecommerce.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Đảm bảo nếu tạo Role lỗi thì không tạo Admin và ngược lại
    public void run(String... args) throws Exception {

        // 1. Khởi tạo Role mặc định nếu chưa có
        if (roleRepository.count() == 0) {
            log.info("[DATA INIT] Bắt đầu khởi tạo dữ liệu Role mặc định...");

            Role adminRole = new Role();
            adminRole.setName(AppConstants.ROLE_ADMIN);

            Role customerRole = new Role();
            customerRole.setName(AppConstants.ROLE_CUSTOMER);

            roleRepository.save(adminRole);
            roleRepository.save(customerRole);

            log.info("[DATA INIT] Khởi tạo Role thành công!");
        }

        // 2. Khởi tạo tài khoản Super Admin mặc định
        String adminEmail = "admin@dev.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("[DATA INIT] Bắt đầu khởi tạo tài khoản Super Admin...");

            Role adminRole = roleRepository.findByName(AppConstants.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ADMIN trong hệ thống!"));

            // Sử dụng class User (hoặc Admin nếu bạn có entity riêng kế thừa từ User)
            User adminUser = new User();
            adminUser.setFullName("Super Admin");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setPhone("0999999999");
            adminUser.setStatus(UserStatus.ACTIVE);
            adminUser.setRole(adminRole);

            userRepository.save(adminUser);

            log.info("[DATA INIT] Tạo Super Admin thành công!");
            log.info("[DATA INIT] Email: {} | Password: {}", adminEmail, "admin123");
        } else {
            log.info("[DATA INIT] Dữ liệu lõi đã sẵn sàng, bỏ qua bước khởi tạo.");
        }
    }
}