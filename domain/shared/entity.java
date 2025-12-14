package domain.shared;

import java.sql.Date;

public class entity<Props> {
    private String id;
    private Props props;
    private Date createdAt;
    private Date updatedAt;
    private Boolean status;

    private entity(Props props, Date createdAt, Date updatedAt, Boolean status, Optional<String> id) {
        this.id = id == null ? id : java.util.UUID.randomUUID().toString();
        this.props = props;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public static <Props> entity<Props> create(Props props, Optional<String> id) {
        Date now = new Date(System.currentTimeMillis());
        return new entity<>(props, now, now, true, id);
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
