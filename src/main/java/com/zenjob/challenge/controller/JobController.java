package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.service.JobService;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(path = "/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @ResponseBody
    public ResponseDto<RequestJobResponse> requestJob(@RequestBody @Valid RequestJobRequestDto dto) {
        Job job = jobService.createJob(UUID.randomUUID(), dto.start, dto.end);
        return ResponseDto.<RequestJobResponse>builder()
                .data(RequestJobResponse.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }

    // Task A
    //AS a Company I want to be able to cancel a job I ordered previously AND
    // if the job gets cancelled all shifts get cancelled as well
    @DeleteMapping(path = "cancelJobAndShifts/{jobId}")
    public ResponseEntity<?> cancelJobAndShifts(@PathVariable("jobId") UUID uuid) {
        Optional<Job> jobs = jobService.getJobs(uuid);
        if (jobs.isPresent()){
            jobService.deleteShift(uuid);
            jobService.deleteJob(uuid);
            return  ResponseEntity.ok().body("All Shifts Cancelled");
        }
        return  ResponseEntity.notFound().build();
    }

    //Task C
    //AS a Company I want to be able to cancel all shifts booked for a
    // specific talent AND if the shifts are cancelled there has to be new shifts created as substitutes
    @DeleteMapping(path = "cancelAllShift/{talentId}")
    public ResponseDto<RequestJobResponse> cancelAllShifts(@PathVariable("talentId") UUID talentId) {
        Optional<Shift> talentList = jobService.getTalent(talentId);
        if (talentList.isPresent()){
            jobService.deleteShift(talentId);
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(1);
            Job job = jobService.createJob(talentId,startDate,endDate);
            return ResponseDto.<RequestJobResponse>builder()
                    .data(RequestJobResponse.builder()
                            .jobId(job.getId())
                            .build())
                    .build();
        }
        return null;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class RequestJobRequestDto {
        @NotNull
        private UUID companyId;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate start;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate   end;
    }

    @Builder
    @Data
    private static class RequestJobResponse {
        UUID jobId;
    }
}
