// application/usecases/UploadVideoUseCase.java
@Service // Aqui podemos usar Service para o Spring gerenciar
public class UploadVideoUseCase {
    private final VideoRepositoryPort repository;
    private final VideoStoragePort storage;
    private final VideoQueuePort queue;

    public UploadVideoUseCase(VideoRepositoryPort repository, VideoStoragePort storage, VideoQueuePort queue) {
        this.repository = repository;
        this.storage = storage;
        this.queue = queue;
    }

    public String execute(String userId, String fileName, byte[] data) {
        Video video = Video.createNew(userId, fileName);

        repository.save(video);        // Grava no DynamoDB
        storage.upload(video.id(), data); // Grava no S3
        queue.sendToProcess(video);   // Notifica o SQS

        return video.id();
    }
}