package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.service.JobService;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final JobService jobService;

    @GetMapping(path = "/{jobId}")
    @ResponseBody
    public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("jobId") UUID uuid) {
        List<ShiftResponse> shiftResponses = jobService.getShifts(uuid).stream()
                .map(shift -> ShiftResponse.builder()
                        .id(uuid)
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getCreatedAt())
                        .end(shift.getEndTime())
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.<GetShiftsResponse>builder()
                .data(GetShiftsResponse.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    @PatchMapping(path = "/{id}/book")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void bookTalent(@PathVariable("id") UUID shiftId, @RequestBody @Valid ShiftController.BookTalentRequestDto dto) {
        jobService.bookTalent(shiftId, dto.talent);
    }

    //Task B
    //AS a Company I want to be able to cancel a single shift of a job I ordered previously
    @DeleteMapping(path = "cancelSingleShift/{jobId}")
    public ResponseEntity<?> cancelSingleShifts(@PathVariable("jobId") UUID shiftId,
                                                @RequestParam(required = true, value = "startTime") Instant startTime,
                                                @RequestParam(required = true, value = "endTime") Instant endTime) {
        Optional<Job> jobList = jobService.getJobs(shiftId);
        if (jobList.isPresent()){
            jobService.deleteShift(shiftId,startTime,endTime);
            return  ResponseEntity.ok().body("Single Shifts Cancelled");
        }
        return  ResponseEntity.notFound().build();
    }

    @NoArgsConstructor
    @Data
    private static class BookTalentRequestDto {
        UUID talent;
    }

    @Builder
    @Data
    private static class GetShiftsResponse {
        List<ShiftResponse> shifts;
    }

    @Builder
    @Data
    private static class ShiftResponse {
        UUID    id;
        UUID    talentId;
        UUID    jobId;
        Instant start;
        Instant end;
    }
}
