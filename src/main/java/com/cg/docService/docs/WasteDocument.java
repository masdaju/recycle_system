package com.cg.docService.docs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
@Data
public class WasteDocument {
    @JsonProperty("waste_id")
    private Long wasteId;
    private String name;
    private String description;
    private Long cid;
    private String unit;
    private Double price;
    @JsonProperty("img_url")
    private String imageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("create_date")
    private Date createDate;




}