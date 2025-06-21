package com.jobportal.controller.lecturer_controller;


import com.jobportal.service.lecturer.LecturerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/lecturer/api")
public class LecturerRest {
    LecturerService lecturerService;

    @GetMapping("/week-task/{internId}/{week}")
    public ResponseEntity<?> getWeekTask(@PathVariable("week") int week,
                                         @PathVariable("internId") int internId) {
        return ResponseEntity.ok(lecturerService.getWeekTask(internId, week));
    }
}
