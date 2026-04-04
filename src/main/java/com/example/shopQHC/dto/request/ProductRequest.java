package com.example.shopQHC.dto.request;

import com.example.shopQHC.entity.Product;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;

    private String imageUrl;
    private String color;
    private String brand;
    private String ageGroup;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Product.Gender gender;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private Product.Status status;

    private Boolean isNew = false;

    @NotNull(message = "Số lượng đã bán không được để trống")
    @Min(value = 0, message = "Số lượng đã bán phải lớn hơn hoặc bằng 0")
    private Integer soldQuantity = 0;
}