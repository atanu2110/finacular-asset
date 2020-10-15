
package com.finadv.assets.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CAMS {
    private HolderInfo holderInfo;
    private List<FundInfo> fundInfoList = new ArrayList<>();
}
