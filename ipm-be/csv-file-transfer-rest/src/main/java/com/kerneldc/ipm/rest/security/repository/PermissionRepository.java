package com.kerneldc.ipm.rest.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.kerneldc.ipm.rest.security.domain.permission.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
	List<Permission> findAllByOrderByName();
}
