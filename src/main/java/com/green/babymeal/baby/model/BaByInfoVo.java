package com.green.babymeal.baby.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BaByInfoVo {

    private Long babyId;
    private LocalDate birthday;
    private String prefer;

}
