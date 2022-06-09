package com.softavail.recordingapi.repository;

import com.softavail.recordingapi.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookRepository  extends JpaRepository<Webhook, String>, JpaSpecificationExecutor<Webhook> {
    void  deleteAllByCallId(String callId);
}
