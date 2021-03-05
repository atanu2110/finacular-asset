package com.finadv.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.finadv.assets.entities.UserAssets;

/**
 * @author atanu
 *
 */
@Repository
public interface UserAssetRepository extends JpaRepository<UserAssets, Integer> {

	@Query(value = "SELECT * FROM user_assets u WHERE u.user_Id = :userid", nativeQuery = true)
	List<UserAssets> findUserAssetByUserId(@Param("userid") Long id);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM user_assets u WHERE u.user_Id = :userid AND u.nick_name = :email AND u.asset_instrument_id = :instrumentId", nativeQuery = true)
	void deleteUserAssetByUserIdEmailAndInstrument(@Param("userid") Long id, @Param("email") String email,
			@Param("instrumentId") int instrumentId);
	
	
	@Query(value = "SELECT distinct(user_Id) FROM user_assets", nativeQuery = true)
	List<Long> findDistinctUserId();
}
