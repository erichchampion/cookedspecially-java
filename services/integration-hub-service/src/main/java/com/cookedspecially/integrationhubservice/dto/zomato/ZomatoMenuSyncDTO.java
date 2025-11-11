package com.cookedspecially.integrationhubservice.dto.zomato;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZomatoMenuSyncDTO {

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("categories")
    private List<CategoryDTO> categories;
}
