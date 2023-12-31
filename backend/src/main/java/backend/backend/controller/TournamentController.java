package backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import backend.backend.model.Tournament;
import backend.backend.repositories.TournamentRepository;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://roundnetplayer.vercel.app/"})
@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentRepository TournamentRepository;

    @GetMapping("/alltournaments")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        
        List<Tournament> tournaments = TournamentRepository.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }
}
