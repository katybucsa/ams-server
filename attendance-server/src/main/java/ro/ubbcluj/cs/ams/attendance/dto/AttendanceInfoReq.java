package ro.ubbcluj.cs.ams.attendance.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceInfoReq {

    @NotNull
    private Integer courseId;
    @NotNull
    private Integer activityId;
    @NotNull
    private Integer remainingTime;
}
