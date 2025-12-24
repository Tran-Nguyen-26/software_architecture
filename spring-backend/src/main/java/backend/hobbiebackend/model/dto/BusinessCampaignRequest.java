package backend.hobbiebackend.model.dto;

import jakarta.validation.constraints.NotBlank;

public class BusinessCampaignRequest {
    @NotBlank(message = "username must be not blank")
    private String username; // username của business đang login

    @NotBlank(message = "subject must be not blank")
    private String subject;

    @NotBlank(message = "content must be not blank")
    private String content;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
