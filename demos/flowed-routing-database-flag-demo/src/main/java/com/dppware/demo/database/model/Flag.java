package com.dppware.demo.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "flag")
@Data
public class Flag {

	  @Id
	  private Integer id;

	  @Column(name = "feature")
	  private String feature;

	  @Column(name = "enabled")
	  private boolean enabled;
}
