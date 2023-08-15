@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/players/{playerName}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String playerName) {
        Player player = playerRepository.findByPlayerName(playerName);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
