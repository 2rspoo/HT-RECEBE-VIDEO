// infrastructure/controllers/VideoUploadController.java
@RestController
@RequestMapping("/v1/videos")
public class VideoUploadController {
    private final UploadVideoUseCase uploadUseCase;

    public VideoUploadController(UploadVideoUseCase uploadUseCase) {
        this.uploadUseCase = uploadUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestHeader("X-User-Id") String userId) throws IOException {
        String videoId = uploadUseCase.execute(userId, file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.ok(videoId);
    }
}