// application/ports/output/VideoStoragePort.java
public interface VideoStoragePort {
    void upload(String videoId, byte[] data);
}