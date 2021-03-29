package com.finadv.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finadv.assets.entities.UserIncomeExpenseDetail;

/**
 * @author atanu
 *
 */
@Repository
public interface UserIncomeExpenseRepository extends JpaRepository<UserIncomeExpenseDetail, Integer> {

	@Query(value = "SELECT * FROM user_income_expense_detail u WHERE u.user_Id = :userid", nativeQuery = true)
	UserIncomeExpenseDetail findUserIncomeExpenseById(@Param("userid") Long id);
}
