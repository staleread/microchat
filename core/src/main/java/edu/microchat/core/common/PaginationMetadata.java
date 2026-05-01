package edu.microchat.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationMetadata extends BaseMetadata {
  private int number;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean isFirst;
  private boolean isLast;
}
