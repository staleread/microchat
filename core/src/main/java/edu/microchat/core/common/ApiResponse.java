package edu.microchat.core.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApiResponse<M extends BaseMetadata, D> {
  private M meta;
  private List<D> data;

  public ApiResponse(M meta, D data) {
    this.meta = meta;
    this.data = new ArrayList<>();
    this.data.add(data);
  }

  public ApiResponse(M meta, List<D> data) {
    this.meta = meta;
    this.data = new ArrayList<>(data);
  }

  public ApiResponse(M meta) {
    this.meta = meta;
  }
}
