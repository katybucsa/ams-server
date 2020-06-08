package ro.ubbcluj.cs.ams.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDto {

    private int id;
    private String username;
    private String userRole;
    private String endpoint;
    private LocalDateTime expirationTime;
    private int subsId;
}
