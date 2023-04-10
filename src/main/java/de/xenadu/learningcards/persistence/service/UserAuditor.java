package de.xenadu.learningcards.persistence.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.CreatedByAndTimestampAudit;
import de.xenadu.learningcards.service.GetUserInfo;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * Sets user information to entities.
 */
@ApplicationScoped
public class UserAuditor {

    private GetUserInfo getUserInfo;

    @Inject
    public void setGetUserInfo(GetUserInfo getUserInfo) {
        this.getUserInfo = getUserInfo;
    }

    /**
     * Update user info before persisting.
     *
     * @param o Object of type {@link CreatedByAndTimestampAudit}
     */
    @PrePersist
    public void prePersist(Object o) {
        if (o instanceof CreatedByAndTimestampAudit auditEntity) {
            UserInfo userInfo = getUserInfo.authenticatedUser();
            auditEntity.setCreatedBy(userInfo.getId());
        }
    }

    @PreUpdate
    public void preUpdate(Object o) {

    }

    @PreRemove
    public void preRemove(Object o) {

    }

    @PostLoad
    public void postLoad(Object o) {

    }

    @PostRemove
    public void postRemove(Object o) {

    }

    @PostUpdate
    public void postUpdate(Object o) {

    }

    @PostPersist
    public void postPersist(Object o) {

    }
}
