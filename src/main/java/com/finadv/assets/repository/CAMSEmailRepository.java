package com.finadv.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.CAMSEmailDB;
import com.finadv.assets.entities.UserAssets;

/**
 * @author atanu
 *
 */
@Repository
public interface CAMSEmailRepository extends JpaRepository<CAMSEmailDB, Integer>{

}
