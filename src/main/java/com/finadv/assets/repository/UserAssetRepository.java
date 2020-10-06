package com.finadv.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.UserAssets;


/**
 * @author atanu
 *
 */
@Repository
public interface UserAssetRepository extends JpaRepository<UserAssets, Integer>{

	@Query(value="SELECT * FROM user_assets u WHERE u.user_Id = :userid", nativeQuery = true)
	List<UserAssets> findUserAssetByUserId(@Param("userid") Long id);
}
