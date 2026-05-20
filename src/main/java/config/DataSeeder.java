package com.att.tdp.issueflow.config;

import com.att.tdp.issueflow.entities.*;
import com.att.tdp.issueflow.enums.*;
import com.att.tdp.issueflow.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ensures that the data is only seeded if there are no users in the system
        if (userRepository.count() == 0) {
            // creates the users
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Admin User")
                    .email("admin@issueflow.com")
                    .role(Role.ADMIN)
                    .build();
                    
            User dev = User.builder()
                    .username("dev1")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Developer One")
                    .email("dev1@issueflow.com")
                    .role(Role.DEVELOPER)
                    .build();
                    
            userRepository.save(admin);
            userRepository.save(dev);

            // creates the project
            Project project = Project.builder()
                    .name("Drone Rescue System")
                    .description("System for survivor detection")
                    .owner(admin)
                    .build();
            projectRepository.save(project);

            // creates the first ticket
            Ticket ticket = Ticket.builder()
                    .title("Train YOLOv11 model")
                    .description("Train the detection model with synthetic images")
                    .status(TicketStatus.TODO)
                    .priority(TicketPriority.HIGH)
                    .type(TicketType.TECHNICAL)
                    .project(project)
                    .assignee(dev)
                    .dueDate(LocalDateTime.now().plusDays(7))
                    .build();
            ticketRepository.save(ticket);
            
            System.out.println("Dummy data seeded successfully!");
        } else {
            System.out.println("Data already exists. Skipping seeding.");
        }
    }
}