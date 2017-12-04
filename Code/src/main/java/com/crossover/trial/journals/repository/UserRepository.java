package com.crossover.trial.journals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginName(String loginName);

    @Query(	"select u from User u "
    	  + "inner join u.subscriptions s " 
    	  + "where s.category = :category ")
    List<User> findBySubscriptionCategory(@Param("category") Category category);

}
