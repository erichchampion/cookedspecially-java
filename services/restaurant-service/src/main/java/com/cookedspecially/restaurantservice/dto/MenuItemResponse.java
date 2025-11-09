package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;
    private Integer calories;
    private Integer preparationTimeMinutes;
    private LocalDateTime createdAt;

    public static MenuItemResponse fromEntity(MenuItem item) {
        return MenuItemResponse.builder()
            .id(item.getId())
            .restaurantId(item.getRestaurant() != null ? item.getRestaurant().getId() : null)
            .name(item.getName())
            .description(item.getDescription())
            .price(item.getPrice())
            .category(item.getCategory())
            .imageUrl(item.getImageUrl())
            .isAvailable(item.getIsAvailable())
            .isVegetarian(item.getIsVegetarian())
            .isVegan(item.getIsVegan())
            .isGlutenFree(item.getIsGlutenFree())
            .isSpicy(item.getIsSpicy())
            .calories(item.getCalories())
            .preparationTimeMinutes(item.getPreparationTimeMinutes())
            .createdAt(item.getCreatedAt())
            .build();
    }
}
