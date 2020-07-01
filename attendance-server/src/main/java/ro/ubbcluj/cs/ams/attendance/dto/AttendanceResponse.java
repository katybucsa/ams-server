package ro.ubbcluj.cs.ams.attendance.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceResponse {

    private Integer course;
    private Integer activity;
}
