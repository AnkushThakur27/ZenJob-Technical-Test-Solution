package com.zenjob.challenge.repository;

import com.zenjob.challenge.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findAllByJob_Id(UUID uuid);
    Optional<Shift>  deleteByJobAndStartTimeAndEndTime (UUID uuid,Instant startTime, Instant endTime);
}
