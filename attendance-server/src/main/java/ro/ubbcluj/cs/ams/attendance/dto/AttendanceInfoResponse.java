package ro.ubbcluj.cs.ams.attendance.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceInfoResponse {

    private String barcode;
    private LocalDateTime createdAt;
}
