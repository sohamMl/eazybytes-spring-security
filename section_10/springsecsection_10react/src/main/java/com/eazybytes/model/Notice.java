package com.eazybytes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("notice_details")
@Getter @Setter
public class Notice {

    @Id
    @Column("notice_id")
    private long noticeId;

    @Column("notice_summary")
    private String noticeSummary;

    @Column("notice_details")
    private String noticeDetails;

    @Column("notic_beg_dt")
    private String noticBegDt;

    @Column("notic_end_dt")
    private String noticEndDt;

    @JsonIgnore
    @Column("create_dt")
    private String createDt;

    @JsonIgnore
    @Column("update_dt")
    private String updateDt;
}
