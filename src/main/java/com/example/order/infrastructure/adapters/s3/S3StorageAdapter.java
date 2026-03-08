// infrastructure/adapters/s3/S3StorageAdapter.java
@Component
public class S3StorageAdapter implements VideoStoragePort {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public void upload(String videoId, byte[] data) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("uploads/" + videoId)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(data));
    }
}