package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.dto.ReserveClassroomResult;
import knu.database.lms.dto.controller.ReserveClassroomDto;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomRepository classRoomRepository;

    @GetMapping("/classroom")
    public ModelAndView classroomPage(
            @RequestParam(name = "buildingNumber", required = false) Integer buildingNumber,
            @SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        ModelAndView mav = new ModelAndView();

        if (userId == null) {
            mav.setViewName("redirect:/");
        }
        else {
            mav.setViewName("classroom");
        }

        if (buildingNumber == null) {
            List<Integer> buildingNumbers = classRoomRepository.getBuildingNumbers();

            mav.addObject("buildingNumbers", buildingNumbers);
        }
        else {
            List<Classroom> classrooms = classRoomRepository.getClassroomByBuilding(buildingNumber);

            mav.addObject("classrooms", classrooms);
        }

        return mav;
    }

    @ResponseBody
    @PostMapping("/api/classroom")
    public void reserveClassroom(@RequestBody ReserveClassroomDto reserveClassroomDto,
                                 @SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        ReserveClassroomResult result = classRoomRepository.reserveClassroom(userId, reserveClassroomDto.getBuildingNumber(), reserveClassroomDto.getRoomCode(),
                reserveClassroomDto.getStartDateTime(), reserveClassroomDto.getEndDateTime());

        if (!result.isSuccess) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.message);
        }
    }


    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }
}
