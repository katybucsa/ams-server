package ro.ubbcluj.cs.ams.attendance.dto;


import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceRequest {

    @NotNull
    private Integer attendanceInfoId;
}
