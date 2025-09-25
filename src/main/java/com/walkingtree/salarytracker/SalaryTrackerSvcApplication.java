package com.walkingtree.salarytracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.walkingtree.salarytracker.auth.User;
import com.walkingtree.salarytracker.auth.UserRepository;
import com.walkingtree.salarytracker.entity.CurrencyRate;
import com.walkingtree.salarytracker.entity.SalaryRecord;
import com.walkingtree.salarytracker.repository.CurrencyRateRepository;
import com.walkingtree.salarytracker.repository.SalaryRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SalaryTrackerSvcApplication {
	/*
	 * Quick Links: 
	 * http://localhost:8080/oauth2/authorization/google
	 * java "-Dspring.profiles.active=dev" -jar salary-tracker-svc-0.0.1-SNAPSHOT.jar
	 */

	public static void main(String[] args) {
		var context = SpringApplication.run(SalaryTrackerSvcApplication.class, args);
		var env = context.getEnvironment();
		String appName = env.getProperty("spring.application.name", "Application");
		String port = env.getProperty("server.port", "8080");
		String localUrl = "http://localhost:" + port;
        String[] profiles = env.getActiveProfiles();
        String profileInfo = profiles.length > 0 ? String.join(", ", profiles) : "default";
        log.info("--------------------------------------------------------------");
        log.info("\tApplication '{}' is running!", appName);
        log.info("\t------------------------------------------------------");
        log.info("\tUrl:      \t{}", localUrl);
        log.info("\tProfile(s): \t{}", profileInfo);
        log.info("--------------------------------------------------------------");
	}

	@Bean
	public CommandLineRunner dataLoader(
			CurrencyRateRepository currencyRateRepository,UserRepository userRepository,
			SalaryRecordRepository salaryRecordRepository) {
		return args -> {

			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Log to verify if the count check is working
            long count = userRepository.count();
            System.out.println("Number of users in the database: " + count);

            if (count == 0) {
                User user1 = new User(
                        0, "Tejal", "Talele", "tejalsarode29@gmail.com", 9579676068L,
                        passwordEncoder.encode("Teju@2810"), "ADMIN"
                );

                userRepository.save(user1);

                System.out.println("Users saved successfully!");
            } else {
                System.out.println("Users already exist, skipping insertion.");
            }
			
			currencyRateRepository.saveAll(List.of(
				new CurrencyRate(null, "INR", "USD", BigDecimal.valueOf(0.012), LocalDate.parse("2024-01-01"), null),
				new CurrencyRate(null, "USD", "INR", BigDecimal.valueOf(82.50), LocalDate.parse("2024-01-01"), null),
				new CurrencyRate(null, "AED", "USD", BigDecimal.valueOf(0.272), LocalDate.parse("2024-01-01"), null),
				new CurrencyRate(null, "USD", "AED", BigDecimal.valueOf(3.67), LocalDate.parse("2024-01-01"), null),
				new CurrencyRate(null, "EUR", "USD", BigDecimal.valueOf(1.18), LocalDate.parse("2024-01-01"), null),
				new CurrencyRate(null, "USD", "EUR", BigDecimal.valueOf(0.85), LocalDate.parse("2024-01-01"), null)
			));

			// Insert Salary Records
			salaryRecordRepository.saveAll(List.of(
				new SalaryRecord(null, "EMP001", "2021-22", "TechCorp India", BigDecimal.valueOf(800000), BigDecimal.valueOf(50000), BigDecimal.valueOf(25000), "INR", null, BigDecimal.valueOf(850000), BigDecimal.valueOf(825000), null, null),
				new SalaryRecord(null, "EMP001", "2022-23", "TechCorp India", BigDecimal.valueOf(950000), BigDecimal.valueOf(75000), BigDecimal.valueOf(30000), "INR", null, BigDecimal.valueOf(1025000), BigDecimal.valueOf(995000), null, null),
				new SalaryRecord(null, "EMP001", "2024-25", "GlobalTech UAE", BigDecimal.valueOf(216000), BigDecimal.valueOf(30000), BigDecimal.valueOf(7200), "AED", null, BigDecimal.valueOf(253200), BigDecimal.valueOf(246000), null, null),
				new SalaryRecord(null, "EMP001", "2023-24", "GlobalTech UAE", BigDecimal.valueOf(180000), BigDecimal.valueOf(24000), BigDecimal.valueOf(6000), "AED", null, BigDecimal.valueOf(204600), BigDecimal.valueOf(198600), null, null),
				new SalaryRecord(null, "EMP002", "2021-22", "Innovatech Europe", BigDecimal.valueOf(60000), BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), "EUR", null, BigDecimal.valueOf(65000), BigDecimal.valueOf(63000), null, null),
				new SalaryRecord(null, "EMP002", "2022-23", "Innovatech Europe", BigDecimal.valueOf(70000), BigDecimal.valueOf(7000), BigDecimal.valueOf(2500), "EUR", null, BigDecimal.valueOf(77000), BigDecimal.valueOf(74500), null, null),
				new SalaryRecord(null, "EMP002", "2023-24", "TechCorp India", BigDecimal.valueOf(900000), BigDecimal.valueOf(60000), BigDecimal.valueOf(35000), "INR", null, BigDecimal.valueOf(960000), BigDecimal.valueOf(925000), null, null),
				new SalaryRecord(null, "EMP002", "2024-25", "TechCorp India", BigDecimal.valueOf(1100000), BigDecimal.valueOf(80000), BigDecimal.valueOf(40000), "INR", null, BigDecimal.valueOf(1180000), BigDecimal.valueOf(1140000), null, null)
			));
		};
	}
}
