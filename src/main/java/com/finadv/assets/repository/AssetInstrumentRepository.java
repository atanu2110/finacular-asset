package com.finadv.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.AssetInstrument;

@Repository
public interface AssetInstrumentRepository extends JpaRepository<AssetInstrument, Integer> {

	/*
	 * @Query("SELECT n FROM AssetInstrument n WHERE n.asset_type_id = ?1")
	 * List<AssetInstrument> findByTypeAssetInstrument(int id);
	 */
}
