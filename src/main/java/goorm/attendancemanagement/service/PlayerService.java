package goorm.attendancemanagement.service;

import goorm.attendancemanagement.domain.dao.Course;
import goorm.attendancemanagement.domain.dao.Player;
import goorm.attendancemanagement.domain.dao.Role;
import goorm.attendancemanagement.domain.dto.*;
import goorm.attendancemanagement.repository.CourseRepository;
import goorm.attendancemanagement.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PlayerService {

    private final CourseRepository courseRepository;
    private final PlayerRepository playerRepository;
    public List<GetPlayersByCourseDto> getPlayersByCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        return course.getPlayers().stream()
                .map(player -> new GetPlayersByCourseDto(
                        course.getCourseName(),
                        player.getPlayerId(),
                        player.getPlayerName(),
                        player.getPlayerEmail()
                ))
                .collect(Collectors.toList());
    }

    public void createPlayer(CreatePlayerDto player) {
        boolean exists = playerRepository.existsByPlayerEmail(player.getPlayerEmail());

        if (exists) {
            throw new IllegalArgumentException("플레이어의 Email '" + player.getPlayerEmail() + "'가 이미 존재합니다.");
        }

        Course findCourse = courseRepository.findById(player.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + player.getCourseId()));

        Player newPlayer = new Player(player.getPlayerEmail(), player.getPlayerPassword(), player.getPlayerName(), findCourse, Role.ROLE_PLAYER);
        playerRepository.save(newPlayer);
    }


    public PlayerInfoDto getPlayerInfo(int playerId) {
        return playerRepository.findById(playerId)
                .map(player -> new PlayerInfoDto(
                        player.getCourse().getCourseName(),
                        player.getPlayerEmail(),
                        player.getPlayerName()))
                .orElseThrow(() -> new EntityNotFoundException("Can not found this player"));  // 플레이어가 없을 경우 null 반환
    }
    public void changePlayerPassword(int playerId, PasswordChangeRequestDto passwordChangeRequestDto) {
        Optional<Player> playerOptional = playerRepository.findById(playerId);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();

            // 현재 비밀번호 확인
            if (!player.getPlayerPassword().equals(passwordChangeRequestDto.getCurrentPassword())) {
                return;  // 현재 비밀번호 불일치
            }

            // 새 비밀번호와 확인 비밀번호 일치 확인
            if (!passwordChangeRequestDto.getNewPassword().equals(passwordChangeRequestDto.getConfirmPassword())) {
                return;  // 새 비밀번호 불일치
            }

            // 비밀번호 업데이트
            player.changePassword(passwordChangeRequestDto.getNewPassword());
            playerRepository.save(player);
        }
    }

    public void deletePlayerById(int playerId) {
        playerRepository.deleteById(playerId);
    }

    public void updatePlayerInfo(int playerId, UpdatePlayerInfoDto newPlayerInfo) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        player.changePassword(newPlayerInfo.getPlayerPassword());
        playerRepository.save(player);


    }
}
