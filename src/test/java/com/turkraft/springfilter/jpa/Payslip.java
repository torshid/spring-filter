package com.turkraft.springfilter.jpa;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @JsonIgnoreProperties({"company", "manager", "staff", "payslips"})
  @ManyToOne
  private Employee employee;

  // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FilterConfig.DATE_FORMATTER.toPattern())
  @Temporal(TemporalType.DATE)
  private Date date;

}
