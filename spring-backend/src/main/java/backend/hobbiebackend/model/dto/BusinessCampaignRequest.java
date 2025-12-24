package backend.hobbiebackend.model.dto;

public class BusinessCampaignRequest {
    private String username; // username của business đang login
    private String subject;
    private String content;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
