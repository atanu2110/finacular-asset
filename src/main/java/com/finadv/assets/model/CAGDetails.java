package com.finadv.assets.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CAGDetails {
    private List<Fund> fundList = new ArrayList<>();
}
