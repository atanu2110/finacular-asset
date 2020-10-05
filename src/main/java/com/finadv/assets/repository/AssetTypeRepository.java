package com.finadv.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.AssetType;

/**
 * @author atanu
 *
 */
@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, Integer>{

}
