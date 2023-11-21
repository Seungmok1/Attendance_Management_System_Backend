package goorm.attendancebook.service;

import goorm.attendancebook.domain.Player;
import goorm.attendancebook.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
//
//    public Optional<Player> getPlayerById(Long id) {
//        return playerRepository.findById(id);
//    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
//
//    public void deletePlayer(Long id) {
//        playerRepository.deleteById(id);
//    }

}