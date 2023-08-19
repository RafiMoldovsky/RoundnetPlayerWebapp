package backend.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import backend.backend.model.Player;
import backend.backend.repositories.PlayerRepository;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/players/{playerName}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String playerName) {
        logger.info("Received request for player: {}", playerName);
        
        Player player = playerRepository.findPlayerByName(playerName);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/allplayers")
    public ResponseEntity<List<String>> getAllPlayerNames() {
        return ResponseEntity.ok(playerRepository.getAllPlayerNames());
    }
}
