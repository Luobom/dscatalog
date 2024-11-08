package com.w3lsolucoes.dscatalog.projections;

import java.util.List;

public interface ProductCategoryProjection {
    Long getId();
    String getName();
    Double getPrice();
    List<CategoryProjection> getCategories();
}

