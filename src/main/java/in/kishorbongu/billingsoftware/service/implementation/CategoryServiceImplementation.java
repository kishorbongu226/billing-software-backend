package in.kishorbongu.billingsoftware.service.implementation;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.kishorbongu.billingsoftware.entity.CategoryEntity;
import in.kishorbongu.billingsoftware.io.CategoryRequest;
import in.kishorbongu.billingsoftware.io.CategoryResponse;
import in.kishorbongu.billingsoftware.repository.CategoryRepository;
import in.kishorbongu.billingsoftware.repository.ItemRepository;

import in.kishorbongu.billingsoftware.service.CategoryService;
import in.kishorbongu.billingsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImplementation implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request,MultipartFile file) {
        String imgUrl = fileUploadService.uploadFile(file);
        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImgUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }

    public List<CategoryResponse> read(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> convertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory)
    {
        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());
       return CategoryResponse.builder()
                    .categoryId(newCategory.getCategoryId())
                    .name(newCategory.getName())
                    .description(newCategory.getDescription())
                    .bgcolor(newCategory.getBgcolor())
                    .imgUrl(newCategory.getImgUrl())
                    .createdAt(newCategory.getCreatedAt())
                    .updatedAt(newCategory.getUpdatedAt())
                    .items(itemsCount)
                    .build(); 
    }


    private CategoryEntity convertToEntity(CategoryRequest request){
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgcolor(request.getBgcolor())
                .build();
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found : "+categoryId));
        fileUploadService.deleteFile(existingCategory.getImgUrl());                    
        categoryRepository.delete(existingCategory);
    }
    
}
