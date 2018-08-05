package com.yq.service;

import com.yq.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Simple to Introduction
 * className: UserRepository
 *
 * @author EricYang
 * @version 2018/8/5 18:44
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
