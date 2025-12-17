package model.core;

import java.time.Instant;

public class Entity<Props> {
    private String id;
    private Props props;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean status;

    protected Entity(Props props, String id) {
        this.id = id;
        this.props = props;
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.status = true;
    }

    public static <Props> Entity<Props> create(Props props, String id) {
        String generatedId = id == null ? java.util.UUID.randomUUID().toString() : id;
        return new Entity<>(props, generatedId);
    }

    public String getId() {
        return id;
    }
    public Props getProps() {
        return props;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }   

    public Boolean isActive() {
        return status;
    }

    public void changeStatus() {
        this.status = !status;
        this.updatedAt = Instant.now();
        return;
    }

}
