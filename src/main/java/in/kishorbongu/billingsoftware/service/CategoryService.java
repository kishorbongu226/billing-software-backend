package in.kishorbongu.billingsoftware.service;



import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import in.kishorbongu.billingsoftware.io.CategoryRequest;
import in.kishorbongu.billingsoftware.io.CategoryResponse;


public interface CategoryService {
    
    CategoryResponse add(CategoryRequest request,MultipartFile multipartFile);

    List<CategoryResponse> read();

    void delete(String categoryId);
}
