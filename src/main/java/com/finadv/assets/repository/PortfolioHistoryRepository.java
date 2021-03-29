package com.finadv.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.PortfolioHistory;

/**
 * @author atanu
 *
 */
@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistory, Integer> {

	@Query(value = "SELECT * FROM portfolio_history p WHERE p.user_id = :userid", nativeQuery = true)
	List<PortfolioHistory> getPortfolioHistoryForUser(@Param("userid") Long id);

}
