#!/usr/bin/env python3
import re
import glob

files = [
    "./restaurant-service/src/main/java/com/cookedspecially/restaurantservice/dto/UpdateMenuItemRequest.java",
    "./restaurant-service/src/main/java/com/cookedspecially/restaurantservice/dto/CreateMenuItemRequest.java",
    "./restaurant-service/src/main/java/com/cookedspecially/restaurantservice/dto/CreateRestaurantRequest.java",
    "./restaurant-service/src/main/java/com/cookedspecially/restaurantservice/dto/ErrorResponse.java",
    "./restaurant-service/src/main/java/com/cookedspecially/restaurantservice/dto/UpdateRestaurantRequest.java",
    "./notification-service/src/main/java/com/cookedspecially/notificationservice/dto/NotificationPreferenceRequest.java",
    "./notification-service/src/main/java/com/cookedspecially/notificationservice/dto/SendNotificationRequest.java",
    "./notification-service/src/main/java/com/cookedspecially/notificationservice/dto/ErrorResponse.java",
    "./payment-service/src/main/java/com/cookedspecially/paymentservice/dto/CreateRefundRequest.java",
    "./payment-service/src/main/java/com/cookedspecially/paymentservice/dto/CreatePaymentRequest.java",
    "./order-service/src/main/java/com/cookedspecially/orderservice/dto/UpdateOrderStatusRequest.java",
    "./order-service/src/main/java/com/cookedspecially/orderservice/dto/OrderItemRequest.java",
    "./order-service/src/main/java/com/cookedspecially/orderservice/dto/CancelOrderRequest.java"
]

for filepath in files:
    print(f"Processing {filepath}...")
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove Lombok annotations
    content = re.sub(r'^@Data\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@Builder\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@Getter\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@Setter\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@NoArgsConstructor\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@AllArgsConstructor\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@RequiredArgsConstructor\s*\n', '', content, flags=re.MULTILINE)
    content = re.sub(r'^@Slf4j\s*\n', '', content, flags=re.MULTILINE)

    with open(filepath, 'w') as f:
        f.write(content)
    print(f"  ✓ Fixed")

print("\nAll files fixed!")
