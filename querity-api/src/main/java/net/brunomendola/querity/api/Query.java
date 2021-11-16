package net.brunomendola.querity.api;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Query {
  private Condition filter;
  private Pagination pagination;
  @Builder.Default
  @NonNull
  private List<Sort> sort = new ArrayList<>();

  public boolean isSimpleConditionFilter() {
    return filter instanceof SimpleCondition;
  }

  public boolean hasFilter() {
    return filter != null &&
        (isSimpleConditionFilter() || !((ConditionsWrapper) filter).isEmpty());
  }

  public boolean hasPagination() {
    return pagination != null;
  }

  public boolean hasSort() {
    return !sort.isEmpty();
  }
}
