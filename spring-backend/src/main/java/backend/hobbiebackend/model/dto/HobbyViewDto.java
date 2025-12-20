package backend.hobbiebackend.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HobbyViewDto {
    private Long id;
    private String name;
    private String slogan;
    private String description;
    private BigDecimal price;

    private String category;
    private String location;

    private String profileImgUrl;
    private String galleryImgUrl1;
    private String galleryImgUrl2;
    private String galleryImgUrl3;
}

