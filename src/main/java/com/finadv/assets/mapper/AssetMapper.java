package com.finadv.assets.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.finadv.assets.dto.UserAssetDto;
import com.finadv.assets.dto.UserAssetsDto;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;

/**
 * @author atanu
 *
 */
@Mapper(componentModel = "spring")
public interface AssetMapper {

	UserAssetDto convertToUserAssetDto(UserAsset userAsset);
	
	@Mappings({ 
	     @Mapping(source = "id", target = "assetId"),
	     @Mapping(source = "code", target = "rtCode")
	    })
	UserAssetsDto convertToUserAssetsDto(UserAssets userAssets);
}
