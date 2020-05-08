package com.cmlx.transaction.basics.persist.repository;

import com.cmlx.transaction.basics.persist.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Desc
 * @Author cmlx
 * @Date 2020-5-8 0008 12:18
 */
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
