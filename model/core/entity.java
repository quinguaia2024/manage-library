package model.core;

import java.sql.Date;

public class entity<Props> {
    private String id;
    private Props props;
    private Date createdAt;
    private Date updatedAt;
    private Boolean status;

    protected entity(Props props,String id) {
        this.id = id;
        this.props = props;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = null;
        this.status = true;
    }

    public static <Props> entity<Props> create(Props props, String id) {
        String generatedId = id == null ? java.util.UUID.randomUUID().toString() : id;
        return new entity<>(props,generatedId);
    }

    public String getId() {
        return id;
    }
    public Props getProps() {
        return props;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }   

    public Boolean isActive() {
        return status;
    }

    public void changeStatus() {
        this.status = !status;
        this.updatedAt = new Date(System.currentTimeMillis());
        return;
    }

}
