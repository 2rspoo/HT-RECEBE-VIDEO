@Component
public class SqsQueueAdapter implements VideoQueuePort {
    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.url}")
    private String queueUrl;

    public void sendToProcess(Video video) {
        sqsTemplate.send(queueUrl, video);
    }
}