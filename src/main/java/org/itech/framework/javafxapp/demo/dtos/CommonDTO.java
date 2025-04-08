package org.itech.framework.javafxapp.demo.dtos;

import javafx.beans.property.*;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeConstant;
import org.itech.framework.javafxapp.demo.common.date_time.DateTimeUtil;
import org.itech.framework.javafxapp.demo.data_access.BasedEntity;

import java.util.Date;

public class CommonDTO {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Date> createdDateTime = new SimpleObjectProperty<>();
    private final StringProperty createdDateTimeDesc = new SimpleStringProperty();
    private final ObjectProperty<Date> updatedDateTime = new SimpleObjectProperty<>();
    private final StringProperty updatedDateTimeDesc = new SimpleStringProperty();

    protected void setFields(BasedEntity based) {
        setId(based.getId());
        setCreatedDateTime(based.getCreatedDateTime());
        setUpdatedDateTime(based.getUpdatedDateTime());

        if (getCreatedDateTime() != null) {
            setCreatedDateTimeDesc(DateTimeUtil.dateToString(
                    getCreatedDateTime(), DateTimeConstant.STANDARD_12_HOUR_DATE_MINUTE_FORMAT
            ));
        }

        if (getUpdatedDateTime() != null) {
            setUpdatedDateTimeDesc(DateTimeUtil.dateToString(
                    getUpdatedDateTime(), DateTimeConstant.STANDARD_12_HOUR_DATE_MINUTE_FORMAT
            ));
        }
    }

    // ID Property
    public long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    public void setId(long id) { this.id.set(id); }

    // Created DateTime
    public Date getCreatedDateTime() { return createdDateTime.get(); }
    public ObjectProperty<Date> createdDateTimeProperty() { return createdDateTime; }
    public void setCreatedDateTime(Date createdDateTime) { this.createdDateTime.set(createdDateTime); }

    // Created DateTime Description
    public String getCreatedDateTimeDesc() { return createdDateTimeDesc.get(); }
    public StringProperty createdDateTimeDescProperty() { return createdDateTimeDesc; }
    public void setCreatedDateTimeDesc(String createdDateTimeDesc) { this.createdDateTimeDesc.set(createdDateTimeDesc); }

    // Updated DateTime
    public Date getUpdatedDateTime() { return updatedDateTime.get(); }
    public ObjectProperty<Date> updatedDateTimeProperty() { return updatedDateTime; }
    public void setUpdatedDateTime(Date updatedDateTime) { this.updatedDateTime.set(updatedDateTime); }

    // Updated DateTime Description
    public String getUpdatedDateTimeDesc() { return updatedDateTimeDesc.get(); }
    public StringProperty updatedDateTimeDescProperty() { return updatedDateTimeDesc; }
    public void setUpdatedDateTimeDesc(String updatedDateTimeDesc) { this.updatedDateTimeDesc.set(updatedDateTimeDesc); }
}