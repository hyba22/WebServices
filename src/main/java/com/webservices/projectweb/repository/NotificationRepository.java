package com.webservices.projectweb.repository;

import com.webservices.projectweb.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
